package com.poker.tournamentdirector.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.poker.tournamentdirector.domain.billing.BillingConnectionStatus
import com.poker.tournamentdirector.domain.billing.BillingOperationResult
import com.poker.tournamentdirector.domain.billing.SubscriptionPlan
import com.poker.tournamentdirector.domain.billing.SubscriptionProduct
import com.poker.tournamentdirector.domain.repository.BillingRepository
import com.poker.tournamentdirector.domain.repository.PremiumStatusRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

@Singleton
class GooglePlayBillingRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val premiumStatusRepository: PremiumStatusRepository,
) : BillingRepository {
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !purchases.isNullOrEmpty()) {
            handlePurchases(purchases)
        }
    }

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build(),
        )
        .build()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val defaultProducts = SubscriptionPlan.entries.map(::SubscriptionProduct)
    private val productState = MutableStateFlow(defaultProducts)
    private val productDetailsById = MutableStateFlow<Map<String, ProductDetails>>(emptyMap())

    override val subscriptionProducts: Flow<List<SubscriptionProduct>> = productState

    override suspend fun connect(): BillingConnectionStatus {
        if (billingClient.isReady) return BillingConnectionStatus.Connected

        return suspendCancellableCoroutine { continuation ->
            billingClient.startConnection(
                object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        val status = if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            BillingConnectionStatus.Connected
                        } else {
                            BillingConnectionStatus.Disconnected(
                                billingResult.debugMessage.ifBlank { "Billing setup failed." },
                            )
                        }
                        if (continuation.isActive) continuation.resume(status)
                    }

                    override fun onBillingServiceDisconnected() = Unit
                },
            )
        }
    }

    override suspend fun purchase(
        activity: Activity,
        plan: SubscriptionPlan,
    ): BillingOperationResult {
        return when (plan) {
            SubscriptionPlan.Free -> {
                premiumStatusRepository.setPremium(false)
                premiumStatusRepository.setOnboardingCompleted(true)
                BillingOperationResult.Success
            }

            SubscriptionPlan.Monthly,
            SubscriptionPlan.Yearly,
            -> {
                val connection = connect()
                if (connection is BillingConnectionStatus.Disconnected) {
                    BillingOperationResult.Failure(connection.reason)
                } else {
                    launchSubscriptionPurchase(activity, plan)
                }
            }
        }
    }

    override suspend fun restorePurchases(): BillingOperationResult {
        return when (val connection = connect()) {
            BillingConnectionStatus.Connected -> queryExistingSubscriptions()
            is BillingConnectionStatus.Disconnected -> BillingOperationResult.Failure(connection.reason)
        }
    }

    private suspend fun launchSubscriptionPurchase(
        activity: Activity,
        plan: SubscriptionPlan,
    ): BillingOperationResult {
        val productId = plan.productId ?: return BillingOperationResult.Failure("Missing product id.")
        val productDetails = productDetailsById.value[productId] ?: fetchProductDetails()[productId]
        if (productDetails == null) {
            return BillingOperationResult.Failure("Subscription product is not available yet.")
        }

        val offerToken = productDetails.subscriptionOfferDetails
            ?.firstOrNull()
            ?.offerToken
            ?: return BillingOperationResult.Failure("No eligible subscription offer is available.")

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .setOfferToken(offerToken)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        return if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            BillingOperationResult.Success
        } else {
            BillingOperationResult.Failure(
                billingResult.debugMessage.ifBlank { "Unable to start purchase flow." },
            )
        }
    }

    private suspend fun fetchProductDetails(): Map<String, ProductDetails> {
        val products = SubscriptionPlan.entries
            .mapNotNull { plan -> plan.productId }
            .map { productId ->
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            }

        if (products.isEmpty()) return emptyMap()

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(products)
            .build()

        return suspendCancellableCoroutine { continuation ->
            billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val detailsById = productDetailsResult.productDetailsList.associateBy { it.productId }
                    productDetailsById.value = detailsById
                    productState.value = SubscriptionPlan.entries.map { plan ->
                        val productDetails = plan.productId?.let(detailsById::get)
                        plan.toSubscriptionProduct(productDetails)
                    }
                    if (continuation.isActive) continuation.resume(detailsById)
                } else {
                    if (continuation.isActive) continuation.resume(emptyMap())
                }
            }
        }
    }

    private suspend fun queryExistingSubscriptions(): BillingOperationResult =
        suspendCancellableCoroutine { continuation ->
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()

            billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    handlePurchases(purchases)
                    if (continuation.isActive) continuation.resume(BillingOperationResult.Success)
                } else {
                    val message = billingResult.debugMessage.ifBlank { "Unable to restore purchases." }
                    if (continuation.isActive) continuation.resume(BillingOperationResult.Failure(message))
                }
            }
        }

    private fun handlePurchases(purchases: List<Purchase>) {
        val hasPremiumPurchase = purchases.any { purchase ->
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                purchase.products.any { productId -> productId in PremiumProductIds }
        }

        purchases
            .filter { purchase -> purchase.purchaseState == Purchase.PurchaseState.PURCHASED }
            .filterNot { purchase -> purchase.isAcknowledged }
            .forEach { purchase ->
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(params) {}
            }

        if (hasPremiumPurchase) {
            scope.launch {
                premiumStatusRepository.setPremium(true)
                premiumStatusRepository.setOnboardingCompleted(true)
            }
        }
    }

    private fun SubscriptionPlan.toSubscriptionProduct(
        productDetails: ProductDetails?,
    ): SubscriptionProduct {
        val formattedPrice = productDetails
            ?.subscriptionOfferDetails
            ?.firstOrNull()
            ?.pricingPhases
            ?.pricingPhaseList
            ?.firstOrNull()
            ?.formattedPrice

        return SubscriptionProduct(
            plan = this,
            title = productDetails?.title ?: title,
            priceLabel = formattedPrice ?: priceLabel,
            description = billingPeriodLabel,
        )
    }

    private companion object {
        val PremiumProductIds = setOf(
            SubscriptionPlan.Monthly.productId,
            SubscriptionPlan.Yearly.productId,
        ).filterNotNull().toSet()
    }
}

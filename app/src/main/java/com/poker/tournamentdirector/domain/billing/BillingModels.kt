package com.poker.tournamentdirector.domain.billing

enum class SubscriptionPlan(
    val productId: String?,
    val title: String,
    val priceLabel: String,
    val billingPeriodLabel: String,
) {
    Free(
        productId = null,
        title = "Free",
        priceLabel = "0 MXN",
        billingPeriodLabel = "Core features with ads",
    ),
    Monthly(
        productId = "monthly_premium",
        title = "Monthly",
        priceLabel = "50 MXN",
        billingPeriodLabel = "per month",
    ),
    Yearly(
        productId = "yearly_premium",
        title = "Yearly",
        priceLabel = "500 MXN",
        billingPeriodLabel = "per year",
    ),
}

data class SubscriptionProduct(
    val plan: SubscriptionPlan,
    val title: String = plan.title,
    val priceLabel: String = plan.priceLabel,
    val description: String = plan.billingPeriodLabel,
)

sealed interface BillingConnectionStatus {
    data object Connected : BillingConnectionStatus
    data class Disconnected(val reason: String) : BillingConnectionStatus
}

sealed interface BillingOperationResult {
    data object Success : BillingOperationResult
    data class Failure(val message: String) : BillingOperationResult
}

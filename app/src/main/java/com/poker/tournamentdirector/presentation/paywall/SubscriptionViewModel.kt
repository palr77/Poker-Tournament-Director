package com.poker.tournamentdirector.presentation.paywall

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poker.tournamentdirector.domain.billing.BillingOperationResult
import com.poker.tournamentdirector.domain.billing.SubscriptionPlan
import com.poker.tournamentdirector.domain.billing.SubscriptionProduct
import com.poker.tournamentdirector.domain.repository.BillingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
) : ViewModel() {
    private val isLoading = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SubscriptionUiState> = combine(
        billingRepository.subscriptionProducts,
        isLoading,
        message,
    ) { products, loading, currentMessage ->
        SubscriptionUiState(
            products = products,
            isLoading = loading,
            message = currentMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SubscriptionUiState(),
    )

    fun selectPlan(
        activity: Activity,
        plan: SubscriptionPlan,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            isLoading.value = true
            message.value = null
            when (val result = billingRepository.purchase(activity, plan)) {
                BillingOperationResult.Success -> {
                    if (plan == SubscriptionPlan.Free) {
                        onSuccess()
                    } else {
                        message.value = "Purchase flow launched."
                    }
                }

                is BillingOperationResult.Failure -> message.value = result.message
            }
            isLoading.value = false
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            isLoading.value = true
            message.value = null
            message.value = when (val result = billingRepository.restorePurchases()) {
                BillingOperationResult.Success -> "Purchase restore completed."
                is BillingOperationResult.Failure -> result.message
            }
            isLoading.value = false
        }
    }
}

data class SubscriptionUiState(
    val products: List<SubscriptionProduct> = SubscriptionPlan.entries.map(::SubscriptionProduct),
    val isLoading: Boolean = false,
    val message: String? = null,
)

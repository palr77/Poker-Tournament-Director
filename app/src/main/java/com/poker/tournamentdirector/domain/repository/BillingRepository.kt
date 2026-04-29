package com.poker.tournamentdirector.domain.repository

import android.app.Activity
import com.poker.tournamentdirector.domain.billing.BillingConnectionStatus
import com.poker.tournamentdirector.domain.billing.BillingOperationResult
import com.poker.tournamentdirector.domain.billing.SubscriptionPlan
import com.poker.tournamentdirector.domain.billing.SubscriptionProduct
import kotlinx.coroutines.flow.Flow

interface BillingRepository {
    val subscriptionProducts: Flow<List<SubscriptionProduct>>

    suspend fun connect(): BillingConnectionStatus

    suspend fun purchase(activity: Activity, plan: SubscriptionPlan): BillingOperationResult

    suspend fun restorePurchases(): BillingOperationResult
}

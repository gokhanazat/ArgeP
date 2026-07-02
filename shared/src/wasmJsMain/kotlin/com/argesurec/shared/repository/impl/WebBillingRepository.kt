package com.argesurec.shared.repository.impl

import com.argesurec.shared.model.BillingRepository
import com.argesurec.shared.model.SubscriptionPackage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WebBillingRepository : BillingRepository {
    private val _isPremium = MutableStateFlow(true) // For web demo, let's assume premium or not supported
    override val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    override suspend fun fetchPackages(): Result<List<SubscriptionPackage>> {
        return Result.success(emptyList())
    }

    override suspend fun purchasePackage(packageId: String, activity: Any): Result<Boolean> {
        return Result.success(false)
    }

    override suspend fun restorePurchases(): Result<Boolean> {
        return Result.success(false)
    }
}

package com.argesurec.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionPackage(
    val id: String,
    val identifier: String,
    val priceString: String,
    val title: String,
    val description: String
)

interface BillingRepository {
    val isPremium: kotlinx.coroutines.flow.StateFlow<Boolean>
    suspend fun fetchPackages(): Result<List<SubscriptionPackage>>
    suspend fun purchasePackage(packageId: String, activity: Any): Result<Boolean>
    suspend fun restorePurchases(): Result<Boolean>
}

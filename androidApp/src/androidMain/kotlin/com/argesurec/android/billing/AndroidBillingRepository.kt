package com.argesurec.android.billing

import android.app.Activity
import com.argesurec.shared.model.BillingRepository
import com.argesurec.shared.model.SubscriptionPackage
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AndroidBillingRepository(
    private val revenueCatManager: RevenueCatManager
) : BillingRepository {

    override val isPremium: StateFlow<Boolean> = revenueCatManager.isPremium

    override suspend fun fetchPackages(): Result<List<SubscriptionPackage>> = suspendCoroutine { continuation ->
        revenueCatManager.fetchOfferings(
            onResult = { packages ->
                val sharedPackages = packages.map { pkg ->
                    SubscriptionPackage(
                        id = pkg.id,
                        identifier = pkg.identifier,
                        priceString = pkg.priceString,
                        title = pkg.title,
                        description = pkg.description
                    )
                }
                continuation.resume(Result.success(sharedPackages))
            },
            onError = { error ->
                continuation.resume(Result.failure(Exception(error)))
            }
        )
    }

    override suspend fun purchasePackage(packageId: String, activity: Any): Result<Boolean> = suspendCoroutine { continuation ->
        if (activity !is Activity) {
            continuation.resume(Result.failure(Exception("Invalid activity context")))
            return@suspendCoroutine
        }

        // We need the original Package object from RevenueCat
        revenueCatManager.fetchOfferings(
            onResult = { packages ->
                val targetPackage = packages.find { it.id == packageId }?.rcPackage
                if (targetPackage != null) {
                    revenueCatManager.purchasePackage(activity, targetPackage) { success, error ->
                        if (success) {
                            continuation.resume(Result.success(true))
                        } else {
                            continuation.resume(Result.failure(Exception(error ?: "Purchase failed")))
                        }
                    }
                } else {
                    continuation.resume(Result.failure(Exception("Package not found")))
                }
            },
            onError = { error ->
                continuation.resume(Result.failure(Exception(error)))
            }
        )
    }

    override suspend fun restorePurchases(): Result<Boolean> = suspendCoroutine { continuation ->
        revenueCatManager.restorePurchases { success, error ->
            if (success) {
                continuation.resume(Result.success(true))
            } else {
                continuation.resume(Result.failure(Exception(error ?: "Restore failed")))
            }
        }
    }
}

package com.argesurec.shared.repository.impl

import android.app.Activity
import com.argesurec.shared.model.BillingRepository
import com.argesurec.shared.model.SubscriptionPackage
import com.revenuecat.purchases.*
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.revenuecat.purchases.interfaces.ReceiveOfferingsCallback
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.models.StoreProduct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AndroidBillingRepository : BillingRepository {
    private val _isPremium = MutableStateFlow(false)
    override val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    init {
        checkSubscriptionStatus()
    }

    private fun checkSubscriptionStatus() {
        Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {
            override fun onReceived(customerInfo: CustomerInfo) {
                _isPremium.value = customerInfo.entitlements["premium"]?.isActive == true
            }
            override fun onError(error: PurchasesError) {
                // Log error
            }
        })
    }

    override suspend fun fetchPackages(): Result<List<SubscriptionPackage>> = suspendCoroutine { continuation ->
        Purchases.sharedInstance.getOfferings(object : ReceiveOfferingsCallback {
            override fun onReceived(offerings: Offerings) {
                val current = offerings.current
                if (current != null) {
                    val packages = current.availablePackages.map { pkg ->
                        val storeProduct = pkg.product
                        SubscriptionPackage(
                            id = pkg.identifier,
                            identifier = storeProduct.id,
                            priceString = storeProduct.price.formatted,
                            title = storeProduct.title,
                            description = storeProduct.description
                        )
                    }
                    continuation.resume(Result.success(packages))
                } else {
                    continuation.resume(Result.success(emptyList()))
                }
            }

            override fun onError(error: PurchasesError) {
                continuation.resume(Result.failure(Exception(error.message)))
            }
        })
    }

    override suspend fun purchasePackage(packageId: String, activity: Any): Result<Boolean> = suspendCoroutine { continuation ->
        val act = activity as? Activity
        if (act == null) {
            continuation.resume(Result.failure(Exception("Activity required for purchase")))
            return@suspendCoroutine
        }

        Purchases.sharedInstance.getOfferings(object : ReceiveOfferingsCallback {
            override fun onReceived(offerings: Offerings) {
                val pkg = offerings.current?.availablePackages?.find { it.identifier == packageId }
                if (pkg != null) {
                    Purchases.sharedInstance.purchasePackage(act, pkg, object : PurchaseCallback {
                        override fun onCompleted(transaction: com.revenuecat.purchases.models.StoreTransaction, customerInfo: CustomerInfo) {
                            val isActive = customerInfo.entitlements["premium"]?.isActive == true
                            _isPremium.value = isActive
                            continuation.resume(Result.success(isActive))
                        }

                        override fun onError(error: PurchasesError, userCancelled: Boolean) {
                            if (userCancelled) {
                                continuation.resume(Result.success(false))
                            } else {
                                continuation.resume(Result.failure(Exception(error.message)))
                            }
                        }
                    })
                } else {
                    continuation.resume(Result.failure(Exception("Package not found")))
                }
            }

            override fun onError(error: PurchasesError) {
                continuation.resume(Result.failure(Exception(error.message)))
            }
        })
    }

    override suspend fun restorePurchases(): Result<Boolean> = suspendCoroutine { continuation ->
        Purchases.sharedInstance.restorePurchases(object : ReceiveCustomerInfoCallback {
            override fun onReceived(customerInfo: CustomerInfo) {
                val isActive = customerInfo.entitlements["premium"]?.isActive == true
                _isPremium.value = isActive
                continuation.resume(Result.success(isActive))
            }

            override fun onError(error: PurchasesError) {
                continuation.resume(Result.failure(Exception(error.message)))
            }
        })
    }
}

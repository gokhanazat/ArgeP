package com.argesurec.android.billing

import android.app.Activity
import com.revenuecat.purchases.*
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.interfaces.ReceiveOfferingsCallback
import com.revenuecat.purchases.models.StoreTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PaywallPackage(
    val id: String,
    val identifier: String,
    val packageType: PackageType,
    val priceString: String,
    val title: String,
    val description: String,
    val rcPackage: Package
)

class RevenueCatManager private constructor() {

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val ENTITLEMENT_ID = "premium"

    companion object {
        @Volatile
        private var instance: RevenueCatManager? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: RevenueCatManager().also { instance = it }
        }
    }

    init {
        checkSubscriptionStatus()
    }

    fun checkSubscriptionStatus() {
        Purchases.sharedInstance.getCustomerInfo(object : com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback {
            override fun onReceived(customerInfo: CustomerInfo) {
                _isPremium.value = customerInfo.entitlements[ENTITLEMENT_ID]?.isActive == true
            }

            override fun onError(error: PurchasesError) {
                // Log error
            }
        })
    }

    fun fetchOfferings(onResult: (List<PaywallPackage>) -> Unit, onError: (String) -> Unit) {
        Purchases.sharedInstance.getOfferings(object : ReceiveOfferingsCallback {
            override fun onReceived(offerings: Offerings) {
                val current = offerings.current
                if (current != null) {
                    val packages = current.availablePackages.map { pkg ->
                        PaywallPackage(
                            id = pkg.product.id,
                            identifier = pkg.identifier,
                            packageType = pkg.packageType,
                            priceString = pkg.product.price.formatted,
                            title = pkg.product.title,
                            description = pkg.product.description,
                            rcPackage = pkg
                        )
                    }
                    onResult(packages)
                } else {
                    onError("No active offerings found.")
                }
            }

            override fun onError(error: PurchasesError) {
                onError(error.message)
            }
        })
    }

    fun purchasePackage(activity: Activity, rcPackage: Package, onResult: (Boolean, String?) -> Unit) {
        Purchases.sharedInstance.purchase(
            PurchaseParams.Builder(activity, rcPackage).build(),
            object : PurchaseCallback {
                override fun onCompleted(storeTransaction: StoreTransaction, customerInfo: CustomerInfo) {
                    val isPremiumNow = customerInfo.entitlements[ENTITLEMENT_ID]?.isActive == true
                    _isPremium.value = isPremiumNow
                    onResult(isPremiumNow, null)
                }

                override fun onError(error: PurchasesError, userCancelled: Boolean) {
                    if (userCancelled) {
                        onResult(false, "Purchase cancelled by user.")
                    } else {
                        onResult(false, error.message)
                    }
                }
            }
        )
    }

    fun restorePurchases(onResult: (Boolean, String?) -> Unit) {
        Purchases.sharedInstance.restorePurchases(object : com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback {
            override fun onReceived(customerInfo: CustomerInfo) {
                val isPremiumNow = customerInfo.entitlements[ENTITLEMENT_ID]?.isActive == true
                _isPremium.value = isPremiumNow
                onResult(isPremiumNow, null)
            }

            override fun onError(error: PurchasesError) {
                onResult(false, error.message)
            }
        })
    }
}

package com.argesurec.shared.repository.impl

import com.argesurec.shared.model.BillingRepository
import com.argesurec.shared.model.SubscriptionPackage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.days

@Serializable
private data class SubscriptionRow(
    @SerialName("user_id") val userId: String = "",
    @SerialName("plan_type") val planType: String = "free",
    @SerialName("is_active") val isActive: Boolean = false
)

/**
 * Web'de satın alma yok. Premium:
 *  - hesap açılışından itibaren 30 gün deneme, veya
 *  - RevenueCat webhook'unun Supabase `subscriptions` tablosuna yazdığı aktif abonelik.
 */
class WebBillingRepository(
    private val supabase: SupabaseClient
) : BillingRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _isWebPremium = MutableStateFlow(false)

    init {
        scope.launch {
            supabase.auth.sessionStatus.collect { status ->
                _isWebPremium.value = if (status is SessionStatus.Authenticated) {
                    val uid = status.session.user?.id
                    if (uid == null) false else try {
                        val row = supabase.from("subscriptions")
                            .select { filter { eq("user_id", uid) } }
                            .decodeSingleOrNull<SubscriptionRow>()
                        row != null && row.isActive && row.planType != "free"
                    } catch (e: Exception) {
                        println("[WebBilling] abonelik okunamadı: ${e.message}")
                        false
                    }
                } else false
            }
        }
    }

    override val isPremium: StateFlow<Boolean> = combine(
        _isWebPremium,
        supabase.auth.sessionStatus
    ) { isWebPremium, _ ->
        if (isWebPremium) return@combine true

        val user = supabase.auth.currentUserOrNull()
        val createdAt = user?.createdAt
        if (createdAt != null) {
            val now = Clock.System.now()
            val diff = now - createdAt
            diff <= 30.days
        } else {
            false
        }
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

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

package com.argesurec.shared.repository.impl

import com.argesurec.shared.model.BillingRepository
import com.argesurec.shared.model.SubscriptionPackage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days

class WebBillingRepository(
    private val supabase: SupabaseClient
) : BillingRepository {
    private val _isWebPremium = MutableStateFlow(false)

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
            diff <= 7.days
        } else {
            false
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.Main),
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

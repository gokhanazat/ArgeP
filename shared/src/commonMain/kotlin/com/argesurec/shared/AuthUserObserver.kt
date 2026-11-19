package com.argesurec.shared

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.Koin

/**
 * Supabase oturumundaki kullanıcı kimliğini dinler.
 * Giriş yapılınca userId, çıkışta null gönderir. (Android'de RevenueCat logIn/logOut için kullanılır.)
 */
fun observeAuthUserId(koin: Koin, onChange: (String?) -> Unit) {
    val client = koin.get<SupabaseClient>()
    CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
        client.auth.sessionStatus.collect { status ->
            when (status) {
                is SessionStatus.Authenticated -> onChange(status.session.user?.id)
                is SessionStatus.NotAuthenticated -> onChange(null)
                else -> Unit
            }
        }
    }
}

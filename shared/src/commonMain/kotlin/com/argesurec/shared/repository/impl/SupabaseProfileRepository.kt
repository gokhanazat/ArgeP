package com.argesurec.shared.repository.impl

import com.argesurec.shared.model.UserProfile
import com.argesurec.shared.repository.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SupabaseProfileRepository(
    private val supabase: SupabaseClient
) : ProfileRepository {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    override val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    override suspend fun getProfile(userId: String): UserProfile? {
        return try {
            val result = supabase.from("profiles")
                .select { filter { eq("id", userId) } }
                .decodeSingleOrNull<UserProfile>()
            _profile.value = result
            result
        } catch (e: Exception) {
            null
        }
    }

    override fun updateLocalProfile(profile: UserProfile?) {
        _profile.value = profile
    }
}

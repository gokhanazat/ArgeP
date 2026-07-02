package com.argesurec.shared.repository

import com.argesurec.shared.model.UserProfile
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    val profile: StateFlow<UserProfile?>
    suspend fun getProfile(userId: String): UserProfile?
    fun updateLocalProfile(profile: UserProfile?)
}

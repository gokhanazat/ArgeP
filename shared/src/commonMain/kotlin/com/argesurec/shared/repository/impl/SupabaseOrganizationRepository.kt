package com.argesurec.shared.repository.impl

import com.argesurec.shared.model.Organization
import com.argesurec.shared.repository.OrganizationRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SupabaseOrganizationRepository(
    private val supabase: SupabaseClient
) : OrganizationRepository {

    override fun getOrganization(orgId: String): Flow<Organization?> = flow {
        val result = supabase.from("organizations").select {
            filter { eq("id", orgId) }
        }.decodeSingleOrNull<Organization>()
        emit(result)
    }

    override suspend fun createOrganization(name: String, ownerId: String): Result<Organization> {
        return try {
            // 1. Önce kullanıcının zaten bir işletmesi var mı kontrol et (Mükerrer kayıt engelleme)
            val existingOrg = supabase.from("organizations").select {
                filter { eq("owner_id", ownerId) }
            }.decodeSingleOrNull<Organization>()

            val result = if (existingOrg != null) {
                // Zaten varsa onu kullan
                existingOrg
            } else {
                // Yoksa yeni oluştur
                val org = Organization(name = name, ownerId = ownerId)
                supabase.from("organizations").insert(org) {
                    select()
                }.decodeSingle<Organization>()
            }
            
            // 2. Profildeki org_id'yi her durumda güncelle (Bağlantıyı sağla)
            supabase.from("profiles").update(
                {
                    set("org_id", result.id)
                    set("role", "owner")
                }
            ) {
                filter { eq("id", ownerId) }
            }
            
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun joinOrganization(orgId: String, userId: String, role: String): Result<Unit> {
        return try {
            supabase.from("profiles").update(
                mapOf("org_id" to orgId, "role" to role)
            ) {
                filter { eq("id", userId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserOrganization(userId: String): Organization? {
        return try {
            val profile = supabase.from("profiles").select {
                filter { eq("id", userId) }
            }.decodeSingle<com.argesurec.shared.model.UserProfile>()
            
            profile.orgId?.let { orgId ->
                supabase.from("organizations").select {
                    filter { eq("id", orgId) }
                }.decodeSingleOrNull<Organization>()
            }
        } catch (e: Exception) {
            null
        }
    }
}

package com.argesurec.shared.repository

import com.argesurec.shared.model.Organization
import kotlinx.coroutines.flow.Flow

interface OrganizationRepository {
    fun getOrganization(orgId: String): Flow<Organization?>
    suspend fun createOrganization(name: String, ownerId: String): Result<Organization>
    suspend fun joinOrganization(orgId: String, userId: String, role: String = "customer"): Result<Unit>
    suspend fun getUserOrganization(userId: String): Organization?
}

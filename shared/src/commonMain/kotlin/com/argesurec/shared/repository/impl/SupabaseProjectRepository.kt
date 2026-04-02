package com.argesurec.shared.repository.impl

import com.argesurec.shared.model.Project
import com.argesurec.shared.model.ProjectPhase
import com.argesurec.shared.model.ProjectWithTeam
import com.argesurec.shared.model.ProjectIdOnly
import com.argesurec.shared.repository.ProjectRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SupabaseProjectRepository(
    private val supabase: SupabaseClient
) : ProjectRepository {

    override fun getAll(userId: String): Flow<List<ProjectWithTeam>> = flow {
        // Step 1: Get Project IDs where user is the owner
        val ownedProjectIds = supabase.from("projects")
            .select(Columns.raw("id")) {
                filter { eq("owner_id", userId) }
            }.decodeList<ProjectIdOnly>()
            .map { it.id }

        // Step 2: Get Project IDs where user is a team member
        val memberProjectIds = supabase.from("team_members")
            .select(Columns.raw("project_id")) {
                filter { eq("user_id", userId) }
            }.decodeList<MemberProjectIds>()
            .map { it.projectId }

        val allIds = (ownedProjectIds + memberProjectIds).distinct()

        if (allIds.isEmpty()) {
            emit(emptyList())
        } else {
            // Step 3: Fetch full project details for all relevant IDs
            val projects = supabase.from("projects")
                .select(Columns.raw("*, team_members(*, profiles(*))")) {
                    filter { 
                        isIn("id", allIds) 
                    }
                }.decodeList<ProjectWithTeam>()
            emit(projects)
        }
    }

    @kotlinx.serialization.Serializable
    private data class MemberProjectIds(
        @kotlinx.serialization.SerialName("project_id") val projectId: String
    )

    override suspend fun getById(id: String): Project? {
        return try {
            supabase.from("projects").select {
                filter { eq("id", id) }
            }.decodeSingleOrNull<Project>()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun insert(item: Project): Result<Project> {
        return try {
            val result = supabase.from("projects").insert(item) {
                select()
            }.decodeSingle<Project>()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun update(item: Project): Result<Project> {
        return try {
            val result = supabase.from("projects").update(item) {
                filter { eq("id", item.id!!) }
                select()
            }.decodeSingle<Project>()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete(id: String): Result<Unit> {
        return try {
            supabase.from("projects").delete {
                filter { eq("id", id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getByPhase(phase: ProjectPhase, userId: String): Flow<List<ProjectWithTeam>> = flow {
        // Step 1: Get Project IDs where user is the owner and phase matches
        val ownedProjectIds = supabase.from("projects")
            .select(Columns.raw("id")) {
                filter { 
                    eq("owner_id", userId) 
                    eq("phase", phase.name)
                }
            }.decodeList<ProjectIdOnly>()
            .map { it.id }

        // Step 2: Get Project IDs where user is a team member
        val memberProjectIds = supabase.from("team_members")
            .select(Columns.raw("project_id")) {
                filter { eq("user_id", userId) }
            }.decodeList<MemberProjectIds>()
            .map { it.projectId }

        val allIds = (ownedProjectIds + memberProjectIds).distinct()

        if (allIds.isEmpty()) {
            emit(emptyList())
        } else {
            // Step 3: Fetch full project details for all relevant IDs, filtered by phase
            val projects = supabase.from("projects")
                .select(Columns.raw("*, team_members(*, profiles(*))")) {
                    filter { 
                        isIn("id", allIds) 
                        eq("phase", phase.name)
                    }
                }.decodeList<ProjectWithTeam>()
            emit(projects)
        }
    }

    override suspend fun uploadFile(projectId: String, fileName: String, bytes: ByteArray): Result<String> {
        return try {
            val bucket = supabase.storage["project-files"]
            val path = "projects/$projectId/$fileName"
            bucket.upload(path, bytes) {
                upsert = true
            }
            Result.success(path)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFiles(projectId: String): Result<List<io.github.jan.supabase.storage.FileObject>> {
        return try {
            val bucket = supabase.storage["project-files"]
            val files = bucket.list("projects/$projectId")
            Result.success(files)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFile(path: String): Result<Unit> {
        return try {
            val bucket = supabase.storage["project-files"]
            bucket.delete(path)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

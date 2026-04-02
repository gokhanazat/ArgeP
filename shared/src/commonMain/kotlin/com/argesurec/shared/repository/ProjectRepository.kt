package com.argesurec.shared.repository

import com.argesurec.shared.model.Project
import com.argesurec.shared.model.ProjectPhase
import com.argesurec.shared.model.ProjectWithTeam
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getAll(userId: String): Flow<List<ProjectWithTeam>>
    suspend fun getById(id: String): Project?
    suspend fun insert(item: Project): Result<Project>
    suspend fun update(item: Project): Result<Project>
    suspend fun delete(id: String): Result<Unit>
    
    fun getByPhase(phase: ProjectPhase, userId: String): Flow<List<ProjectWithTeam>>
    
    // File Management
    suspend fun uploadFile(projectId: String, fileName: String, bytes: ByteArray): Result<String>
    suspend fun getFiles(projectId: String): Result<List<io.github.jan.supabase.storage.FileObject>>
    suspend fun deleteFile(path: String): Result<Unit>
}

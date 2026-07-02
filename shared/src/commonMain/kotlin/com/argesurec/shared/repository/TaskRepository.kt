package com.argesurec.shared.repository

import com.argesurec.shared.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAll(orgId: String): Flow<List<Task>>
    suspend fun getById(id: String): Task?
    suspend fun insert(item: Task): Result<Task>
    suspend fun update(item: Task): Result<Task>
    suspend fun delete(id: String): Result<Unit>
    
    fun getByMilestone(milestoneId: String): Flow<List<Task>>
    fun getAssignedToMe(orgId: String): Flow<List<Task>>
}

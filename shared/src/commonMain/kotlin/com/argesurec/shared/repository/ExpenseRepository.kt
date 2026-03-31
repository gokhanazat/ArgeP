package com.argesurec.shared.repository

import com.argesurec.shared.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getByProject(projectId: String): Flow<List<Expense>>
    suspend fun insert(expense: Expense): Result<Expense>
    suspend fun delete(id: String): Result<Unit>
}

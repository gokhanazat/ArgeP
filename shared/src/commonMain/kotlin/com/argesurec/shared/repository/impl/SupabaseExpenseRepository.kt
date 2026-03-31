package com.argesurec.shared.repository.impl

import com.argesurec.shared.model.Expense
import com.argesurec.shared.repository.ExpenseRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SupabaseExpenseRepository(
    private val supabase: SupabaseClient
) : ExpenseRepository {

    override fun getByProject(projectId: String): Flow<List<Expense>> = flow {
        val expenses = supabase.from("expenses").select {
            filter { eq("project_id", projectId) }
            order("expense_date", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
        }.decodeList<Expense>()
        emit(expenses)
    }

    override suspend fun insert(expense: Expense): Result<Expense> {
        return try {
            val result = supabase.from("expenses").insert(expense) {
                select()
            }.decodeSingle<Expense>()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete(id: String): Result<Unit> {
        return try {
            supabase.from("expenses").delete {
                filter { eq("id", id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

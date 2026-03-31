package com.argesurec.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argesurec.shared.model.Expense
import com.argesurec.shared.repository.ExpenseRepository
import com.argesurec.shared.util.UiState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExpenseData(
    val expenses: List<Expense> = emptyList(),
    val totalSpent: Double = 0.0
)

class ExpenseViewModel(
    private val repository: ExpenseRepository,
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<ExpenseData>>(UiState.Loading)
    val state: StateFlow<UiState<ExpenseData>> = _state.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    fun clearActionMessage() { _actionMessage.value = null }

    fun loadExpenses(projectId: String) {
        viewModelScope.launch {
            _state.emit(UiState.Loading)
            try {
                repository.getByProject(projectId).collect { expenses ->
                    val total = expenses.sumOf { it.amount }
                    _state.emit(UiState.Success(ExpenseData(expenses, total)))
                }
            } catch (e: Exception) {
                _state.emit(UiState.Error(e.message ?: "Harcamalar yüklenirken hata oluştu."))
            }
        }
    }

    fun addExpense(projectId: String, amount: Double, description: String?, category: String) {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id
            if (userId == null) {
                _actionMessage.emit("Oturum açık değil.")
                return@launch
            }

            val newExpense = Expense(
                projectId = projectId,
                amount = amount,
                category = category,
                description = description,
                createdBy = userId
            )

            val result = repository.insert(newExpense)
            if (result.isSuccess) {
                loadExpenses(projectId)
                _actionMessage.emit("Harcama başarıyla eklendi.")
            } else {
                _actionMessage.emit(result.exceptionOrNull()?.message ?: "Harcama eklenemedi.")
            }
        }
    }

    fun deleteExpense(id: String, projectId: String) {
        viewModelScope.launch {
            val result = repository.delete(id)
            if (result.isSuccess) {
                loadExpenses(projectId)
                _actionMessage.emit("Harcama silindi.")
            } else {
                _actionMessage.emit(result.exceptionOrNull()?.message ?: "Harcama silinemedi.")
            }
        }
    }
}

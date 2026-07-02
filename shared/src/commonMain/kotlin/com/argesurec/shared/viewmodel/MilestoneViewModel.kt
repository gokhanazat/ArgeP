package com.argesurec.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argesurec.shared.model.Milestone
import com.argesurec.shared.repository.MilestoneRepository
import com.argesurec.shared.repository.ProfileRepository
import com.argesurec.shared.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MilestoneData(
    val milestones: List<Milestone> = emptyList()
)

class MilestoneViewModel(
    private val repository: MilestoneRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<MilestoneData>>(UiState.Loading)
    val state: StateFlow<UiState<MilestoneData>> = _state.asStateFlow()

    fun loadMilestones(projectId: String) {
        viewModelScope.launch {
            _state.emit(UiState.Loading)
            try {
                repository.getByProject(projectId).collect { milestones ->
                    _state.emit(UiState.Success(MilestoneData(milestones = milestones)))
                }
            } catch (e: Exception) {
                _state.emit(UiState.Error(e.message ?: "Milestone'lar yüklenemedi."))
            }
        }
    }

    fun createMilestone(projectId: String, titleInput: String, dueDateInput: String?) {
        viewModelScope.launch {
            val orgId = profileRepository.profile.value?.orgId
            if (orgId == null) {
                _state.emit(UiState.Error("İşletme bilgisi bulunamadı."))
                return@launch
            }

            val newMilestone = Milestone(
                id = null,
                orgId = orgId,
                projectId = projectId,
                title = titleInput,
                dueDate = dueDateInput,
                status = "TODO",
                createdAt = null
            )
            val result = repository.insert(newMilestone)
            if (result.isSuccess) {
                loadMilestones(projectId)
            } else {
                _state.emit(UiState.Error(result.exceptionOrNull()?.message ?: "Milestone oluşturulamadı."))
            }
        }
    }

    fun loadMilestoneDetail(id: String) {
        viewModelScope.launch {
            try {
                repository.getById(id)?.let { milestone ->
                    _state.emit(UiState.Success(MilestoneData(milestones = listOf(milestone))))
                } ?: run {
                    _state.emit(UiState.Error("Milestone bulunamadı."))
                }
            } catch (e: Exception) {
                _state.emit(UiState.Error(e.message ?: "Milestone detayı yüklenemedi."))
            }
        }
    }
}

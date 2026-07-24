package com.argesurec.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argesurec.shared.model.Project
import com.argesurec.shared.model.ProjectPhase
import com.argesurec.shared.model.ProjectWithTeam
import com.argesurec.shared.repository.ProjectRepository
import com.argesurec.shared.util.UiState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProjectsData(
    val projects: List<ProjectWithTeam> = emptyList(),
    val selectedPhase: ProjectPhase? = null,
    val activeProjectsCount: Int = 0,
    val completedProjectsCount: Int = 0,
    val isPremium: Boolean = false
)

class ProjectsViewModel(
    private val repository: ProjectRepository,
    private val profileRepository: com.argesurec.shared.repository.ProfileRepository,
    private val billingRepository: com.argesurec.shared.model.BillingRepository,
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<ProjectsData>>(UiState.Loading)
    val state: StateFlow<UiState<ProjectsData>> = _state.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    fun clearActionMessage() { _actionMessage.value = null }

    init {
        loadProjects()
    }

    fun loadProjects(force: Boolean = false) {
        if (!force && _state.value is UiState.Success) return
        
        viewModelScope.launch {
            _state.emit(UiState.Loading)
            try {
                val orgId = profileRepository.profile.value?.orgId
                if (orgId == null) {
                    _state.emit(UiState.Error("İşletme bilgisi bulunamadı."))
                    return@launch
                }

                    repository.getAll(orgId).collect { projects ->
                        val activeCount = projects.size
                        val completedCount = projects.count { it.status == "Tamamlandı" }
                        _state.emit(UiState.Success(ProjectsData(
                            projects = projects,
                            activeProjectsCount = activeCount,
                            completedProjectsCount = completedCount,
                            isPremium = billingRepository.isPremium.value
                        )))
                    }
            } catch (e: Exception) {
                _state.emit(UiState.Error(e.message ?: "Beklenmedik bir hata oluştu."))
            }
        }
    }

    fun loadByPhase(phase: ProjectPhase) {
        viewModelScope.launch {
            _state.emit(UiState.Loading)
            try {
                val orgId = profileRepository.profile.value?.orgId
                if (orgId == null) {
                    _state.emit(UiState.Error("İşletme bilgisi bulunamadı."))
                    return@launch
                }
                repository.getByPhase(phase, orgId).collect { projects ->
                    _state.emit(UiState.Success(ProjectsData(
                        projects = projects, 
                        selectedPhase = phase,
                        isPremium = billingRepository.isPremium.value
                    )))
                }
            } catch (e: Exception) {
                _state.emit(UiState.Error(e.message ?: "Filtreleme sırasında hata oluştu."))
            }
        }
    }

    fun createProject(
        nameInput: String, 
        descriptionInput: String?, 
        phaseInput: ProjectPhase, 
        budgetTotal: Double = 0.0, 
        budgetSpent: Double = 0.0,
        startDate: String? = null,
        endDate: String? = null
    ) {
        viewModelScope.launch {
            val userProfile = profileRepository.profile.value
            val orgId = userProfile?.orgId
            
            if (orgId == null) {
                _actionMessage.emit("İşletme bilgisi bulunamadı.")
                return@launch
            }

            // ENFORCE LIMIT: 1 Project for Free users
            val currentState = _state.value
            if (currentState is UiState.Success) {
                val isPremium = billingRepository.isPremium.value
                if (!isPremium && currentState.data.projects.size >= 1) {
                    _actionMessage.emit("Ücretsiz planda sadece 1 aktif proje oluşturabilirsiniz. Lütfen Premium'a yükseltin.")
                    return@launch
                }
            }

            val newProject = Project(
                id = null,
                orgId = orgId,
                name = nameInput,
                description = descriptionInput,
                phase = phaseInput,
                ownerId = userProfile.id,
                budgetTotal = budgetTotal,
                budgetSpent = budgetSpent,
                startDate = startDate,
                endDate = endDate,
                createdAt = null
            )

            val result = repository.insert(newProject)
            if (result.isSuccess) {
                loadProjects(force = true)
                _actionMessage.emit("Proje başarıyla oluşturuldu.")
            } else {
                _actionMessage.emit(result.exceptionOrNull()?.message ?: "Proje oluşturulamadı.")
            }
        }
    }

    fun updateProject(project: Project) {
        viewModelScope.launch {
            val result = repository.update(project)
            if (result.isSuccess) {
                loadProjects(force = true)
                _actionMessage.emit("Proje başarıyla güncellendi.")
            } else {
                _actionMessage.emit(result.exceptionOrNull()?.message ?: "Güncelleme başarısız.")
            }
        }
    }

    fun deleteProject(id: String) {
        viewModelScope.launch {
            val result = repository.delete(id)
            if (result.isSuccess) {
                loadProjects(force = true)
            } else {
                _state.emit(UiState.Error(result.exceptionOrNull()?.message ?: "Silme işlemi başarısız."))
            }
        }
    }
}

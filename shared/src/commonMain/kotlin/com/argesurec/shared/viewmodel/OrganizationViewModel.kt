package com.argesurec.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argesurec.shared.repository.OrganizationRepository
import com.argesurec.shared.repository.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrganizationState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class OrganizationViewModel(
    private val organizationRepository: OrganizationRepository,
    private val profileRepository: ProfileRepository,
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _state = MutableStateFlow(OrganizationState())
    val state: StateFlow<OrganizationState> = _state.asStateFlow()

    fun createOrganization(name: String) {
        if (name.isBlank()) {
            _state.update { it.copy(error = "Lütfen işletme adını giriniz.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                if (userId == null) {
                    _state.update { it.copy(isLoading = false, error = "Oturum bulunamadı.") }
                    return@launch
                }

                val result = organizationRepository.createOrganization(name, userId)
                if (result.isSuccess) {
                    // Profil bilgisini yenile (Bu reaktif akışı tetikler)
                    profileRepository.getProfile(userId)
                    _state.update { it.copy(isLoading = false, isSuccess = true, error = null) }
                } else {
                    _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "İşletme oluşturulamadı.") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun resetState() {
        _state.update { OrganizationState() }
    }
}

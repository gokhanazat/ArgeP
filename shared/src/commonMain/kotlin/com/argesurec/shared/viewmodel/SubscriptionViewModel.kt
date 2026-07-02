package com.argesurec.shared.viewmodel

import com.argesurec.shared.model.BillingRepository
import com.argesurec.shared.model.SubscriptionPackage
import com.argesurec.shared.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

data class SubscriptionUiState(
    val packages: List<SubscriptionPackage> = emptyList(),
    val isPremium: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class SubscriptionViewModel(
    private val billingRepository: BillingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SubscriptionUiState())
    val state: StateFlow<SubscriptionUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            billingRepository.isPremium.collect { premium ->
                _state.value = _state.value.copy(isPremium = premium)
            }
        }
        loadPackages()
    }

    fun loadPackages() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            billingRepository.fetchPackages()
                .onSuccess { packages ->
                    _state.value = _state.value.copy(packages = packages, isLoading = false)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(error = error.message, isLoading = false)
                }
        }
    }

    fun purchase(pkg: SubscriptionPackage, activity: Any) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            billingRepository.purchasePackage(pkg.identifier, activity)
                .onSuccess { success ->
                    _state.value = _state.value.copy(isLoading = false)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(error = error.message, isLoading = false)
                }
        }
    }

    fun restore() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            billingRepository.restorePurchases()
                .onSuccess { _ -> _state.value = _state.value.copy(isLoading = false) }
                .onFailure { error -> _state.value = _state.value.copy(error = error.message, isLoading = false) }
        }
    }
}

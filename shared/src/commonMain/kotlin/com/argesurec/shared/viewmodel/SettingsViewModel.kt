package com.argesurec.shared.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.argesurec.shared.model.BillingRepository
import com.argesurec.shared.model.SubscriptionPackage
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val billingRepository: BillingRepository
) : ViewModel() {
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    val isPremium: StateFlow<Boolean> = billingRepository.isPremium

    private val _packages = MutableStateFlow<List<SubscriptionPackage>>(emptyList())
    val packages: StateFlow<List<SubscriptionPackage>> = _packages.asStateFlow()

    private val _billingError = MutableStateFlow<String?>(null)
    val billingError: StateFlow<String?> = _billingError.asStateFlow()

    init {
        loadPackages()
    }

    fun loadPackages() {
        viewModelScope.launch {
            billingRepository.fetchPackages().onSuccess {
                _packages.value = it
            }.onFailure {
                _billingError.value = it.message
            }
        }
    }

    fun purchase(packageId: String, activity: Any) {
        viewModelScope.launch {
            billingRepository.purchasePackage(packageId, activity).onFailure {
                _billingError.value = it.message
            }
        }
    }

    fun restore() {
        viewModelScope.launch {
            billingRepository.restorePurchases().onFailure {
                _billingError.value = it.message
            }
        }
    }

    fun toggleDarkMode() {
        _isDarkMode.update { !it }
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.update { enabled }
    }
}


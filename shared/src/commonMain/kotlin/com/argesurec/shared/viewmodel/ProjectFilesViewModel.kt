package com.argesurec.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argesurec.shared.repository.ProjectRepository
import com.argesurec.shared.util.SelectedFile
import com.argesurec.shared.util.UiState
import io.github.jan.supabase.storage.FileObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FileData(
    val files: List<FileObject> = emptyList()
)

class ProjectFilesViewModel(
    private val repository: ProjectRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<FileData>>(UiState.Loading)
    val state = _state.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>?>(null)
    val actionState = _actionState.asStateFlow()

    fun loadFiles(projectId: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            repository.getFiles(projectId).fold(
                onSuccess = { _state.value = UiState.Success(FileData(it)) },
                onFailure = { _state.value = UiState.Error(it.message ?: "Files could not be loaded") }
            )
        }
    }

    fun uploadFile(projectId: String, selectedFile: SelectedFile) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            repository.uploadFile(projectId, selectedFile.name, selectedFile.bytes).fold(
                onSuccess = {
                    _actionState.value = UiState.Success(Unit)
                    loadFiles(projectId) // Listeyi yenile
                },
                onFailure = { _actionState.value = UiState.Error(it.message ?: "Upload failed") }
            )
        }
    }

    fun deleteFile(projectId: String, path: String) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            repository.deleteFile(path).fold(
                onSuccess = {
                    _actionState.value = UiState.Success(Unit)
                    loadFiles(projectId) // Listeyi yenile
                },
                onFailure = { _actionState.value = UiState.Error(it.message ?: "Delete failed") }
            )
        }
    }
    
    fun clearActionState() {
        _actionState.value = null
    }
}

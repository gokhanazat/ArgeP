package com.argesurec.shared.util

import androidx.compose.runtime.Composable

data class SelectedFile(
    val name: String,
    val bytes: ByteArray,
    val mimeType: String? = null
)

@Composable
expect fun rememberFilePicker(onFileSelected: (SelectedFile?) -> Unit): FilePickerLauncher

interface FilePickerLauncher {
    fun launch()
}

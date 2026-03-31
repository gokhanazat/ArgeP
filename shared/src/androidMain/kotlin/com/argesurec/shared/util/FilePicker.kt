package com.argesurec.shared.util

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberFilePicker(onFileSelected: (SelectedFile?) -> Unit): FilePickerLauncher {
    val context = LocalContext.current
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bytes = inputStream.readBytes()
                    val fileName = uri.path?.substringAfterLast("/") ?: "file"
                    // Get actual display name if possible
                    val cursor = context.contentResolver.query(uri, null, null, null, null)
                    var displayName = fileName
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                            if (nameIndex != -1) displayName = it.getString(nameIndex)
                        }
                    }
                    onFileSelected(SelectedFile(displayName, bytes, context.contentResolver.getType(uri)))
                }
            } catch (e: Exception) {
                onFileSelected(null)
            }
        } else {
            onFileSelected(null)
        }
    }

    return remember {
        object : FilePickerLauncher {
            override fun launch() {
                launcher.launch("*/*") // Her türlü dosyaya izin ver
            }
        }
    }
}

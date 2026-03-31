package com.argesurec.shared.util

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.LocalActivityResultRegistryOwner
import java.util.UUID

@Composable
actual fun rememberFilePicker(onFileSelected: (SelectedFile?) -> Unit): FilePickerLauncher {
    val context = LocalContext.current
    val registryOwner = LocalActivityResultRegistryOwner.current
        ?: error("No ActivityResultRegistryOwner found")
    
    val currentOnSelected by rememberUpdatedState(onFileSelected)
    val key = remember { "file_picker_" + UUID.randomUUID().toString() }
    
    val launcher = remember(registryOwner, key) {
        registryOwner.activityResultRegistry.register(key, ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val bytes = inputStream.readBytes()
                        val fileName = uri.path?.substringAfterLast("/") ?: "file"
                        val cursor = context.contentResolver.query(uri, null, null, null, null)
                        var displayName = fileName
                        cursor?.use {
                            if (it.moveToFirst()) {
                                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                                if (nameIndex != -1) displayName = it.getString(nameIndex)
                            }
                        }
                        currentOnSelected(SelectedFile(displayName, bytes, context.contentResolver.getType(uri)))
                    }
                } catch (e: Exception) {
                    currentOnSelected(null)
                }
            } else {
                currentOnSelected(null)
            }
        }
    }
    
    DisposableEffect(launcher) {
        onDispose {
            launcher.unregister()
        }
    }

    return remember(launcher) {
        object : FilePickerLauncher {
            override fun launch() {
                launcher.launch("*/*")
            }
        }
    }
}

package com.argesurec.shared.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader
import org.w3c.files.get
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get

@Composable
actual fun rememberFilePicker(onFileSelected: (SelectedFile?) -> Unit): FilePickerLauncher {
    return remember {
        object : FilePickerLauncher {
            override fun launch() {
                val input = document.createElement("input") as HTMLInputElement
                input.type = "file"
                input.onchange = {
                    val file = input.files?.get(0)
                    if (file != null) {
                        val reader = FileReader()
                        reader.onload = {
                            val arrayBuffer = reader.result as ArrayBuffer
                            val array = Int8Array(arrayBuffer)
                            val bytes = ByteArray(array.length) { i -> array[i] }
                            onFileSelected(SelectedFile(file.name, bytes, file.type))
                        }
                        reader.readAsArrayBuffer(file)
                    } else {
                        onFileSelected(null)
                    }
                }
                input.click()
            }
        }
    }
}

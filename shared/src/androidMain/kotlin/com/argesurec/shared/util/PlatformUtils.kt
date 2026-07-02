package com.argesurec.shared.util

import java.util.Locale

actual fun getPlatformLanguage(): String {
    val lang = java.util.Locale.getDefault().language
    return if (lang.startsWith("tr", ignoreCase = true)) "tr" else "en"
}

actual val isWeb: Boolean = false

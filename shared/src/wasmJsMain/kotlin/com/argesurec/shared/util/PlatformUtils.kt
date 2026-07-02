package com.argesurec.shared.util

actual fun getPlatformLanguage(): String {
    // For web, we can get it from browser settings if needed, 
    // but default to "en" or detect via JS.
    return "en" 
}

actual val isWeb: Boolean = true

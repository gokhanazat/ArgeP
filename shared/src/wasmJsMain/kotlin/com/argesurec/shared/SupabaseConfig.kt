package com.argesurec.shared

actual object SupabaseConfig {
    actual val URL: String = "https://poelkfxcehixweytutrl.supabase.co"
    actual val ANON_KEY: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBvZWxrZnhjZWhpeHdleXR1dHJsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQ4NTQwMzAsImV4cCI6MjA5MDQzMDAzMH0.Wo_Axkp4-0meFv099XrNZSXgGq9xiHyaU9H1DoMtVaA"
}

/**
 * Bu fonksiyonlar Webpack DefinePlugin ile inject edilen değerleri okur.
 * JS dünyasındaki `process.env.SUPABASE_URL` değerine erişir.
 */
private fun getEnvUrl(): String = js("process.env.SUPABASE_URL")
private fun getEnvKey(): String = js("process.env.SUPABASE_ANON_KEY")

package com.argesurec.android

import android.app.Application
import com.argesurec.shared.supabaseModule
import com.argesurec.shared.repositoryModule
import com.argesurec.shared.viewModelModule
import com.argesurec.shared.initKoin
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class ArgepApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin
        initKoin(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            androidContext(this@ArgepApplication)
            modules(com.argesurec.android.di.androidModule)
        }

        // Initialize RevenueCat
        Purchases.debugLogsEnabled = BuildConfig.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(this, BuildConfig.REVENUECAT_API_KEY).build()
        )
    }
}

package com.argesurec.android.di

import com.argesurec.android.billing.AndroidBillingRepository
import com.argesurec.android.billing.RevenueCatManager
import com.argesurec.shared.model.BillingRepository
import org.koin.dsl.module

val androidModule = module {
    // RevenueCat kullanıcısı Supabase oturumuna bağlanır (webhook + web Premium senkronu için)
    single { RevenueCatManager.getInstance().also { it.bindAuth(getKoin()) } }
    single<BillingRepository> { AndroidBillingRepository(get()) }
}

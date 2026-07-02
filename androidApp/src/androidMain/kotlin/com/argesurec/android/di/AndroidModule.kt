package com.argesurec.android.di

import com.argesurec.android.billing.AndroidBillingRepository
import com.argesurec.android.billing.RevenueCatManager
import com.argesurec.shared.model.BillingRepository
import org.koin.dsl.module

val androidModule = module {
    single { RevenueCatManager.getInstance() }
    single<BillingRepository> { AndroidBillingRepository(get()) }
}

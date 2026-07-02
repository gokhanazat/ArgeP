import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvmToolchain(17)
    androidTarget()
}

android {
    namespace = "com.argesurec.android"
    compileSdk = 35

    val properties = Properties()
    val propertiesFile = project.rootProject.file("local.properties")
    if (propertiesFile.exists()) {
        properties.load(propertiesFile.inputStream())
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.argesurec.android"
        minSdk = 24
        targetSdk = 35
        versionCode = 104
        versionName = "1.0.4"

        val supabaseUrl = properties.getProperty("SUPABASE_URL")?.removeSurrounding("\"") ?: ""
        val supabaseKey = properties.getProperty("SUPABASE_ANON_KEY")?.removeSurrounding("\"") ?: ""
        val revenueCatKey = properties.getProperty("REVENUECAT_API_KEY")?.removeSurrounding("\"") ?: ""
        
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseKey\"")
        buildConfigField("String", "REVENUECAT_API_KEY", "\"$revenueCatKey\"")
    }


    signingConfigs {
        create("release") {
            storeFile = properties.getProperty("RELEASE_STORE_FILE")?.let { file(it) }
            storePassword = properties.getProperty("RELEASE_STORE_PASSWORD")
            keyAlias = properties.getProperty("RELEASE_KEY_ALIAS")
            keyPassword = properties.getProperty("RELEASE_KEY_PASSWORD")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.koin.android)
    implementation(libs.revenuecat.purchases)
    implementation(libs.revenuecat.purchases.ui)
    debugImplementation(compose.uiTooling)
}


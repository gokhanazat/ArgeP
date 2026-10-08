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
    compileSdk = libs.versions.android.compileSdk.get().toInt()

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
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 110
        versionName = "1.1.0"

        val supabaseUrl = properties.getProperty("SUPABASE_URL")?.removeSurrounding("\"") ?: ""
        val supabaseKey = properties.getProperty("SUPABASE_ANON_KEY")?.removeSurrounding("\"") ?: ""
        val revenueCatKey = properties.getProperty("REVENUECAT_API_KEY")?.removeSurrounding("\"") ?: ""
        
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseKey\"")
        buildConfigField("String", "REVENUECAT_API_KEY", "\"$revenueCatKey\"")
    }


    val releaseStoreFilePath = properties.getProperty("RELEASE_STORE_FILE")
    val releaseStoreFile = if (!releaseStoreFilePath.isNullOrEmpty()) file(releaseStoreFilePath) else null
    val hasValidSigning = releaseStoreFile?.exists() == true

    signingConfigs {
        if (hasValidSigning) {
            create("release") {
                storeFile = releaseStoreFile
                storePassword = properties.getProperty("RELEASE_STORE_PASSWORD")
                keyAlias = properties.getProperty("RELEASE_KEY_ALIAS")
                keyPassword = properties.getProperty("RELEASE_KEY_PASSWORD")
            }
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
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (hasValidSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
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


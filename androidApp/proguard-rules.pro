# Kotlin Multiplatform & Serialization
-keepattributes *Annotation*, InnerClasses, EnclosingMethod
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
    @kotlinx.serialization.SerialName *;
}
-keepclassmembers class **$$serializer {
    *;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Supabase / Ktor
-dontwarn io.ktor.**
-keep class io.ktor.** { *; }
-dontwarn io.github.jan.supabase.**
-keep class io.github.jan.supabase.** { *; }

# Koin
-keep class io.insert.koin.** { *; }
-dontwarn io.insert.koin.**

# RevenueCat
-keep class com.revenuecat.purchases.** { *; }
-dontwarn com.revenuecat.purchases.**

# SQLDelight
-keep class app.cash.sqldelight.** { *; }
-dontwarn app.cash.sqldelight.**

# Voyager Navigation
-keep class cafe.adriel.voyager.** { *; }
-dontwarn cafe.adriel.voyager.**

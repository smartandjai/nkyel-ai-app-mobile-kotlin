# ═══════════════════════════════════════════════════════════════════════════════
# GabomaGPT — ProGuard Rules
# ═══════════════════════════════════════════════════════════════════════════════

# ── Ktor ─────────────────────────────────────────────────────────────────────
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# ── OkHttp3 ──────────────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# ── Kotlinx Serialization ────────────────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.smartandj.gabomagpt.**$$serializer { *; }
-keepclassmembers class com.smartandj.gabomagpt.** {
    *** Companion;
}
-keepclasseswithmembers class com.smartandj.gabomagpt.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ── Hilt / Dagger ────────────────────────────────────────────────────────────
-dontwarn dagger.**
-keep class dagger.** { *; }

# ── Room ─────────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# ── Clerk ────────────────────────────────────────────────────────────────────
-keep class com.clerk.** { *; }
-dontwarn com.clerk.**

# ── Apache POI ───────────────────────────────────────────────────────────────
-dontwarn org.apache.poi.**
-dontwarn org.apache.xmlbeans.**
-dontwarn org.openxmlformats.**
-dontwarn com.microsoft.**
-dontwarn org.etsi.**
-dontwarn org.w3.**

# ── Jsoup ────────────────────────────────────────────────────────────────────
-keep class org.jsoup.** { *; }

# ── Compose ──────────────────────────────────────────────────────────────────
-dontwarn androidx.compose.**

# ── General ──────────────────────────────────────────────────────────────────
-keepattributes Signature
-keepattributes Exceptions

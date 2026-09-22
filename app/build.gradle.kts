plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}



// ── Charger les clés API depuis local.properties ou environment variables ──
fun readLocalProp(key: String): String {
    // First check environment variables (for GitHub Actions)
    System.getenv(key)?.let { return it }
    
    // Fallback to local.properties for local development
    val file = rootProject.file("local.properties")
    if (!file.exists()) return ""
    return file.readLines()
        .firstOrNull { it.trim().startsWith("$key=") }
        ?.substringAfter("=")
        ?.trim()
        ?: ""
}

android {
    namespace   = "com.smartandj.gabomagpt"
    compileSdk  = 36

    defaultConfig {
        applicationId         = "com.smartandj.gabomagpt"
        minSdk                = 26
        targetSdk             = 36
        versionCode           = 1
        versionName           = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GROQ_API_KEY", "\"${readLocalProp("GROQ_API_KEY")}\"")
        buildConfigField("String", "TAVILY_API_KEY", "\"${readLocalProp("TAVILY_API_KEY")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled   = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable          = true
            applicationIdSuffix   = ".debug"
            versionNameSuffix     = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // kotlinOptions has been migrated to kotlin.compilerOptions below

    buildFeatures {
        compose     = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ── Core ────────────────────────────────────────────────
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // ── Clerk Auth (Prebuilt UI + Native Google Sign-In) ────
    implementation("com.clerk:clerk-android-ui:1.0.28")
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // ── Lifecycle ───────────────────────────────────────────
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // ── Compose BOM ─────────────────────────────────────────
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.adaptive)
    implementation(libs.androidx.material3.adaptive.nav)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.mikepenz.markdown.m3)
    implementation(libs.mikepenz.markdown.code)
    implementation(libs.haze.jetpack.compose)
    implementation(libs.coil.compose)
    implementation(libs.compose.icons.lucide)

    // ── Baseline Profiles (Performance) ──────────────────────
    implementation(libs.profileinstaller)

    // ── Navigation ──────────────────────────────────────────
    implementation(libs.androidx.navigation.compose)

    // ── Persistence ─────────────────────────────────────────
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ── Documents / Exports ─────────────────────────────────
    implementation(libs.apache.poi)

    // ── Async ───────────────────────────────────────────────
    implementation(libs.kotlinx.coroutines.android)

    // ── Hilt DI ─────────────────────────────────────────────
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // ── Ktor Client ─────────────────────────────────────────
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // ── OkHttp3 ─────────────────────────────────────────────
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.sse)

    // ── Serialization ───────────────────────────────────────
    implementation(libs.kotlin.serialization)

    // ── HTML Parsing ─────────────────────────────────────────
    implementation(libs.jsoup)

    // ── Ktor ──────────────────────────────────────────────
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.okhttp)

    // ── Test ────────────────────────────────────────────────
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        freeCompilerArgs.addAll(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
        )
    }
}

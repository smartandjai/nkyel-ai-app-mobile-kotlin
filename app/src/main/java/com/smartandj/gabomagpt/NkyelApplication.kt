/* Ñkyel AI · NkyelApplication.kt · SmartANDJ AI Technologies
   Application class — Hilt DI + Clerk SDK initialization
   Fondateur : Daniel Jonathan ANDJ */

package com.smartandj.gabomagpt

import android.app.Application
import com.clerk.api.Clerk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NkyelApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // ── Clerk SDK init ─────────────────────────────────────
        // Uses the publishable key only (NEVER the secret key in a mobile app).
        // The key is hardcoded here instead of BuildConfig because:
        //   1. It's a PUBLIC key (safe to ship in APK)
        //   2. It avoids local.properties/CI sync issues that caused build failures
        Clerk.initialize(
            context = this,
            publishableKey = "pk_test_aG9seS1jaWNhZGEtOTAuY2xlcmsuYWNjb3VudHMuZGV2JA"
        )
    }
}

typealias GabomaApplication = NkyelApplication

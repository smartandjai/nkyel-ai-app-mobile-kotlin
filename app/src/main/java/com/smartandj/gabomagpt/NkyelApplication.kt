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
        // Uses the production publishable key matching clerk.smartandjai.com.
        // It's a public key safe to ship in the APK.
        Clerk.initialize(
            context = this,
            publishableKey = "pk_live_Y2xlcmsuc21hcnRhbmRqYWkuY29tJA"
        )
    }
}

typealias GabomaApplication = NkyelApplication

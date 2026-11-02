/**
 * Ñkyel AI — Application
 * SmartANDJ AI Technologies · Founder: Daniel Jonathan ANDJ
 *
 * Hilt DI + Clerk SDK initialization with production key.
 */
package com.smartandj.gabomagpt

import android.app.Application
import com.clerk.api.Clerk
import com.smartandj.gabomagpt.data.remote.NkyelApiConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GabomaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Production Clerk — uses the publishable key only (safe for APK)
        Clerk.initialize(
            context = this,
            publishableKey = NkyelApiConfig.CLERK_PUBLISHABLE_KEY
        )
    }
}

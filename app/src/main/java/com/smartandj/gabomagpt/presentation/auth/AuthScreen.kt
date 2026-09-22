/* GabomaGPT · AuthScreen.kt · SmartANDJ AI Technologies
   Clerk prebuilt AuthView — native sign-in/sign-up with Google support
   Themed to match the Panther Black design system
   Fondateur : Daniel Jonathan ANDJ */

package com.smartandj.gabomagpt.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clerk.ui.auth.AuthView

// ── Panther Black palette ─────────────────────────────────
private val PantherBlack = Color(0xFF020304)
private val GoldAccent = Color(0xFFC5A059)
private val TextSecondary = Color(0xFF9CA3AF)

@Composable
fun AuthScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PantherBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // ── Branding header ──
            androidx.compose.material3.Icon(
                painter = androidx.compose.ui.res.painterResource(id = com.smartandj.gabomagpt.R.drawable.ic_nkyel),
                contentDescription = "Ñkyel AI",
                tint = GoldAccent,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "L'Antre de",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Ñkyel AI",
                color = GoldAccent,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Authentification Souveraine",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Clerk prebuilt AuthView ──────────────────────
            // Handles sign-in, sign-up, email verification,
            // and native Google Sign-In via Credential Manager
            // all out of the box. Clerk auto-detects configured
            // social connections from the Dashboard.
            AuthView()

            Spacer(modifier = Modifier.height(24.dp))

            // ── Footer ───────────────────────────────────────
            Text(
                text = "POWERED BY SMARTANDJ AI TECH",
                color = TextSecondary.copy(alpha = 0.5f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/* Ñkyel AI · AuthScreen.kt · SmartANDJ AI Technologies
   Approved Luxury Authentication Card · Panther Black & Champagne Gold
   Clerk prebuilt AuthView — native email OTP/Magic Link with Google support
   Fondateur : Daniel Jonathan ANDJ */

package com.smartandj.gabomagpt.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clerk.ui.auth.AuthView
import com.smartandj.gabomagpt.presentation.theme.GabomaColors
import kotlinx.coroutines.delay

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    // Poll auth status after user interacts with AuthView
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.checkAuth()
            delay(1500)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GabomaColors.BgBlackPanther),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // ── Approved Luxury Branding Header ────────────────
            Surface(
                shape = CircleShape,
                color = GabomaColors.AccentBlackPanther,
                modifier = Modifier.size(48.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Ñ",
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Ñkyel",
                color = GabomaColors.TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "by SmartANDJ AI Technologies",
                color = GabomaColors.AccentBlackPanther,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Intelligence Agentique Visuelle · Authentification Souveraine",
                color = GabomaColors.TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Clerk Prebuilt AuthView Card ───────────────────
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    AuthView()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Footer ───────────────────────────────────────
            Text(
                text = "SMARTANDJ AI TECHNOLOGIES · STRICT RS256 AUTHENTICATION",
                color = GabomaColors.TextSecondary.copy(alpha = 0.5f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ============================================================
// GABOMAGPT — MODULE 1 : ÉCRAN ACCUEIL (ÉTAT ZÉRO)
// 20 Salutations gabonaises dynamiques
// Kotlin / Jetpack Compose 2026
// SMARTANDJ AI TECH · BY ANDJ
// ============================================================

package com.smartandj.gabomagpt.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import java.util.Calendar

// ─────────────────────────────────────────────────────────────
// DESIGN SYSTEM BLACK-PANTHER
// ─────────────────────────────────────────────────────────────
object BPColors {
    val BgBase       = Color(0xFF020304)
    val Primary      = Color(0xFFC5A059)
    val TextMuted    = Color(0xFF9B8BB3)
    val Surface      = Color(0xFF0D0F14)
    val Border       = Color(0xFF1A1D26)
    val TextPrimary  = Color(0xFFF0EFE8)
    val TextSecondary= Color(0xFFB8B6A8)
    val TextTertiary = Color(0xFF6E6C62)
    val ErrorRed     = Color(0xFFFF3B30)
    val AccentGreen  = Color(0xFF00D4AA)
    val PantherGrad1 = Color(0xFFFF6B6B)
    val PantherGrad3 = Color(0xFF00D4AA)
}

// ─────────────────────────────────────────────────────────────
// DATA CLASS SALUTATION
// ─────────────────────────────────────────────────────────────
data class GreetingResult(
    val text        : String,
    val accentColor : Color
)

// ─────────────────────────────────────────────────────────────
// LOGIQUE SALUTATION — getGreeting()
// ─────────────────────────────────────────────────────────────
fun getGreeting(
    hour    : Int,
    day     : Int,    // Calendar.DAY_OF_WEEK : 1=Dim, 2=Lun ... 7=Sam
    date    : Int,    // jour du mois 1-31
    name    : String? = null
): GreetingResult {

    val n = if (!name.isNullOrBlank()) " $name" else ""

    // ── PRIORITÉ 1 : Premier du mois ──────────────────────
    if (date == 1) return GreetingResult(
        "Premier du mois$n ! Nouveau mois, nouvelles conquêtes. Le Gabon nous regarde 🇬🇦",
        BPColors.Primary
    )

    // ── PRIORITÉ 2 : Jours spéciaux (matin uniquement) ───
    when (day) {
        Calendar.MONDAY -> if (hour in 6..9) return GreetingResult(
            "Lundi$n ! Comme on dit chez nous : la semaine appartient à ceux qui attaquent d'abord ⚡",
            BPColors.Primary
        )
        Calendar.FRIDAY -> if (hour in 14..23) return GreetingResult(
            "C'est vendredi$n ! Le weekend approche mais les pros finissent fort. On lâche rien 🏁",
            BPColors.Primary
        )
        Calendar.SATURDAY -> return GreetingResult(
            "Samedi$n ! Même le weekend les lions ne dorment pas. Qu'est-ce qu'on construit ?",
            BPColors.Primary
        )
        Calendar.SUNDAY -> return GreetingResult(
            "Dimanche béni$n 🙏 La famille, la paix... et une belle mission pour Ñkyel AI !",
            BPColors.TextMuted
        )
    }

    // ── PRIORITÉ 3 : Plages horaires ─────────────────────
    return when (hour) {
        in 0..3   -> GreetingResult(
            "La nuit veille avec toi$n 🌙 Ñkyel AI est à tes côtés.",
            BPColors.TextMuted
        )
        in 4..5   -> GreetingResult(
            "Tu es matinal$n ! L'esprit est vif et prêt à créer 🌿",
            BPColors.AccentGreen
        )
        in 6..7   -> GreetingResult(
            "Bon matin$n ! Nouvelle journée, nouvelles victoires ☀️",
            BPColors.Primary
        )
        in 8..9   -> GreetingResult(
            "Mbolo$n ! Belle matinée pour propulser tes directives.",
            BPColors.Primary
        )
        in 10..11 -> GreetingResult(
            "Bien ou bien$n ? Tout est en place pour avancer fort !",
            BPColors.Primary
        )
        12        -> GreetingResult(
            "Midi$n ! Prends un moment pour recharger avant la suite ⚡",
            BPColors.Primary
        )
        13        -> GreetingResult(
            "Après le déjeuner$n, on repart en mission avec clarté !",
            BPColors.Primary
        )
        in 14..15 -> GreetingResult(
            "Plein élan cet après-midi$n. Les grands projets prennent vie 🔥",
            BPColors.Primary
        )
        in 16..17 -> GreetingResult(
            "Toujours en action$n. Qu'allons-nous accomplir ensemble ?",
            BPColors.Primary
        )
        18        -> GreetingResult(
            "Belle fin de journée$n. Faisons le bilan des réussites 🌅",
            BPColors.Primary
        )
        in 19..20 -> GreetingResult(
            "Bonsoir$n ! Le moment idéal pour la réflexion stratégique 💎",
            BPColors.Primary
        )
        in 21..22 -> GreetingResult(
            "Encore actif$n ? C'est le calme parfait pour produire du grand.",
            BPColors.TextMuted
        )
        23        -> GreetingResult(
            "Fin de soirée$n... Ñkyel AI veille à tes côtés 🌑",
            BPColors.TextMuted
        )
        else      -> GreetingResult(
            "Akiéri$n ! Bienvenue dans Ñkyel AI, ton intelligence souveraine 🌿",
            BPColors.AccentGreen
        )
    }
}

// ─────────────────────────────────────────────────────────────
// COMPOSABLE : ÉCRAN ACCUEIL VIDE
// ─────────────────────────────────────────────────────────────
@Composable
fun NkyelEmptyHomeScreen(
    userName    : String?    = null,
    modifier    : Modifier   = Modifier
) {
    val cal     = remember { Calendar.getInstance() }
    val hour    = cal.get(Calendar.HOUR_OF_DAY)
    val day     = cal.get(Calendar.DAY_OF_WEEK)
    val date    = cal.get(Calendar.DAY_OF_MONTH)
    val greeting = remember(userName) { getGreeting(hour, day, date, userName) }

    // ── Animation d'entrée ──────────────────────────────
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val enterAlpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label         = "homeAlpha"
    )
    val enterSlide by animateFloatAsState(
        targetValue   = if (visible) 0f else 24f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label         = "homeSlide"
    )

    Box(
        modifier          = modifier
            .fillMaxSize()
            .background(BPColors.BgBase)
            .alpha(enterAlpha)
            .offset(y = enterSlide.dp),
        contentAlignment  = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Logo Gaboma AI (pulsing dot + text) ────
            GabomaLogoMark()

            Spacer(Modifier.height(8.dp))

            // ── Salutation dynamique ───────────────────
            Text(
                text      = greeting.text,
                style     = TextStyle(
                    fontSize     = 22.sp,
                    fontWeight   = FontWeight.SemiBold,
                    textAlign    = TextAlign.Center,
                    lineHeight   = 30.sp,
                    letterSpacing = (-0.3).sp
                ),
                color     = greeting.accentColor,
                modifier  = Modifier.padding(horizontal = 32.dp)
            )

            // ── Prénom utilisateur si connecté ─────────
            if (!userName.isNullOrBlank()) {
                Text(
                    text  = userName,
                    style = TextStyle(
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign  = TextAlign.Center
                    ),
                    color = BPColors.TextSecondary
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Invite action ──────────────────────────
            Text(
                text  = "Lance une Directive pour commencer...",
                style = TextStyle(
                    fontSize  = 13.sp,
                    textAlign = TextAlign.Center
                ),
                color = BPColors.TextTertiary
            )
        }
    }
}

@Composable
fun GabomaLogoMark() = NkyelLogoMark()


@Composable
fun GabomaEmptyHomeScreen(
    userName    : String?    = null,
    modifier    : Modifier   = Modifier
) {
    NkyelEmptyHomeScreen(userName, modifier)
}

// ─────────────────────────────────────────────────────────────
// LOGO Ñkyel AI — dot doré pulsant
// ─────────────────────────────────────────────────────────────
@Composable
fun NkyelLogoMark() {
    val infiniteTransition = rememberInfiniteTransition(label = "logoPulse")
    val pulse by infiniteTransition.animateFloat(
        0.7f, 1.0f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "logoScale"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            // Halo pulsant
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .scale(pulse)
                    .background(BPColors.Primary.copy(alpha = 0.12f), shape = CircleShape)
            )
            // Cercle principal
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(BPColors.Primary.copy(0.3f), BPColors.Surface)
                        ),
                        shape = CircleShape
                    )
                    .border(
                        1.dp,
                        BPColors.Primary.copy(0.5f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.smartandj.gabomagpt.R.drawable.ic_nkyel),
                    contentDescription = "Ñkyel AI",
                    tint = BPColors.Primary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "ÑKYEL AI",
            style = TextStyle(
                fontSize     = 15.sp,
                fontWeight   = FontWeight.Bold,
                letterSpacing = 3.sp,
                color        = BPColors.Primary
            )
        )
        Spacer(Modifier.height(2.dp))
        Text(
            "INTELLIGENCE SOUVERAINE",
            style = TextStyle(
                fontSize     = 9.sp,
                fontWeight   = FontWeight.Medium,
                letterSpacing = 2.sp,
                color        = BPColors.TextTertiary
            )
        )
    }
}

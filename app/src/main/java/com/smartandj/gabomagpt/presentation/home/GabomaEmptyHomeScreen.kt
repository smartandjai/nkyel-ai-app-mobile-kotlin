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
            "Dimanche béni$n 🙏 La famille, la foi... et une petite Directive pour Gaboma AI !",
            BPColors.TextMuted
        )
    }

    // ── PRIORITÉ 3 : Plages horaires ─────────────────────
    return when (hour) {
        in 0..3   -> GreetingResult(
            "La nuit de Libreville veille avec toi$n 🌙",
            BPColors.TextMuted
        )
        in 4..5   -> GreetingResult(
            "Tu forces déjà à cette heure$n ? Respect — la mangrove dort encore 🌿",
            BPColors.AccentGreen
        )
        in 6..7   -> GreetingResult(
            "Bon matin$n ! Qui se lève tôt attrape le meilleur atanga ☀️",
            BPColors.Primary
        )
        in 8..9   -> GreetingResult(
            "Mbolo$n ! La belle matinée de Libreville est là. On commence la chasse ?",
            BPColors.Primary
        )
        in 10..11 -> GreetingResult(
            "Bien ou bien$n ? La journée est déjà lancée — on est ensemble !",
            BPColors.Primary
        )
        12        -> GreetingResult(
            "Midi$n ! Les brochettes du coin t'appellent. Recharge avant la suite 🍖",
            BPColors.Primary
        )
        13        -> GreetingResult(
            "Après le déjeuner$n, on repart en mission. La sieste c'est pour les faibles !",
            BPColors.Primary
        )
        in 14..15 -> GreetingResult(
            "La chaleur de Libreville ne nous arrête pas$n. On est sur le terrain 🔥",
            BPColors.Primary
        )
        in 16..17 -> GreetingResult(
            "L'heure de pointe au PK5$n, mais toi t'es déjà en avance. Qu'est-ce qu'on chasse ?",
            BPColors.Primary
        )
        18        -> GreetingResult(
            "Le soleil descend sur l'Estuaire$n. Belle fin de journée pour les grands 🌅",
            BPColors.Primary
        )
        in 19..20 -> GreetingResult(
            "Bonsoir$n ! La nuit gabonaise commence — les lions sortent. On est là 🦁",
            BPColors.Primary
        )
        in 21..22 -> GreetingResult(
            "Encore debout$n ? Tu forces ! C'est les vrais qui tiennent jusqu'à cette heure 💪",
            BPColors.TextMuted
        )
        23        -> GreetingResult(
            "Presque minuit$n... Les rêtes gabonaises se font la nuit. Gaboma AI veille 🌑",
            BPColors.TextMuted
        )
        else      -> GreetingResult(
            "Akiéri$n ! Bienvenue dans l'Antre de Gaboma AI. La forêt t'attend 🌿",
            BPColors.AccentGreen
        )
    }
}

// ─────────────────────────────────────────────────────────────
// COMPOSABLE : ÉCRAN ACCUEIL VIDE
// ─────────────────────────────────────────────────────────────
@Composable
fun GabomaEmptyHomeScreen(
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

// ─────────────────────────────────────────────────────────────
// LOGO Gaboma AI — dot doré pulsant
// ─────────────────────────────────────────────────────────────
@Composable
fun GabomaLogoMark() {
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
                Text(
                    "G",
                    style = TextStyle(
                        fontSize   = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color      = BPColors.Primary
                    )
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "GABOMA AI",
            style = TextStyle(
                fontSize     = 13.sp,
                fontWeight   = FontWeight.Bold,
                letterSpacing = 3.sp,
                color        = BPColors.Primary
            )
        )
    }
}

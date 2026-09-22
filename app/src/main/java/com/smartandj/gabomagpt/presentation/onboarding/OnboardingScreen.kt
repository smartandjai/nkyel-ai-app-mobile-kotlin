/* GabomaGPT · OnboardingScreen.kt · SmartANDJ AI Technologies
   Premium 4-step onboarding: Consent → Identity → Language → Telemetry
   Design: Premium-level polish, uses Gaboma theme tokens exclusively.
   Fondateur : Daniel Jonathan ANDJ */

package com.smartandj.gabomagpt.presentation.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartandj.gabomagpt.presentation.motion.Motion
import com.smartandj.gabomagpt.presentation.theme.GabomaColors

// ═══════════════════════════════════════════════════════════════════════════
// ONBOARDING SCREEN — Main entry point
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun OnboardingScreen(
    clerkFullName: String?,
    onComplete: () -> Unit,
    onOpenTerms: () -> Unit = {},
    onOpenPrivacy: () -> Unit = {},
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    // Pre-fill name from Clerk on first composition
    LaunchedEffect(clerkFullName) {
        viewModel.prefillFromClerk(clerkFullName)
    }

    // Theme colors (Black Panther default)
    val bg = GabomaColors.BgBlackPanther
    val surface = GabomaColors.SurfaceBlackPanther
    val elevated = GabomaColors.ElevatedBlackPanther
    val accent = GabomaColors.AccentBlackPanther
    val accentFg = bg
    val textPrimary = GabomaColors.TextPrimary
    val textSecondary = GabomaColors.TextSecondary
    val textTertiary = GabomaColors.TextTertiary
    val border = GabomaColors.Divider

    val steps = OnboardingStep.entries
    val currentIndex = steps.indexOf(state.currentStep)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxHeight()
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // ── Logo ──
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = accent,
                shadowElevation = 8.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.smartandj.gabomagpt.R.drawable.ic_nkyel),
                        contentDescription = "Ñkyel AI",
                        tint = accentFg,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Title ──
            Text(
                text = "Bienvenue sur Ñkyel AI",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    textAlign = TextAlign.Center,
                ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Quelques étapes pour personnaliser votre expérience",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                ),
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Progress dots ──
            ProgressDots(
                totalSteps = steps.size,
                currentStep = currentIndex,
                accent = accent,
                border = border,
                bg = bg,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Step content (crossfade) ──
            AnimatedContent(
                targetState = state.currentStep,
                transitionSpec = {
                    fadeIn(tween(Motion.DURATION_BASE)) togetherWith
                        fadeOut(tween(Motion.DURATION_BASE))
                },
                label = "onboarding_step",
            ) { step ->
                when (step) {
                    OnboardingStep.CONSENT -> ConsentStep(
                        tosAccepted = state.tosAccepted,
                        onTosChanged = viewModel::setTosAccepted,
                        onOpenTerms = onOpenTerms,
                        onOpenPrivacy = onOpenPrivacy,
                        canProceed = viewModel.canProceed(),
                        onNext = viewModel::nextStep,
                        accent = accent,
                        accentFg = accentFg,
                        surface = surface,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        border = border,
                    )

                    OnboardingStep.IDENTITY -> IdentityStep(
                        displayName = state.displayName,
                        clerkName = state.clerkName,
                        onNameChanged = viewModel::setDisplayName,
                        canProceed = viewModel.canProceed(),
                        onNext = viewModel::nextStep,
                        onBack = viewModel::previousStep,
                        accent = accent,
                        accentFg = accentFg,
                        surface = surface,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        textTertiary = textTertiary,
                        border = border,
                    )

                    OnboardingStep.LANGUAGE -> LanguageStep(
                        selectedLanguages = state.selectedLanguages,
                        primaryLocale = state.primaryLocale,
                        onToggleLanguage = viewModel::toggleLanguage,
                        onSetPrimary = viewModel::setPrimaryLocale,
                        canProceed = viewModel.canProceed(),
                        onNext = viewModel::nextStep,
                        onBack = viewModel::previousStep,
                        accent = accent,
                        accentFg = accentFg,
                        surface = surface,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        border = border,
                    )

                    OnboardingStep.TELEMETRY -> TelemetryStep(
                        telemetryEnabled = state.telemetryEnabled,
                        onTelemetryChanged = viewModel::setTelemetryEnabled,
                        isSubmitting = state.isSubmitting,
                        error = state.error,
                        onBack = viewModel::previousStep,
                        onComplete = { viewModel.completeOnboarding(onComplete) },
                        accent = accent,
                        accentFg = accentFg,
                        surface = surface,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        border = border,
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Footer ──
            Text(
                text = "SMARTANDJ AI TECHNOLOGIES · ÑKYEL AI 2026",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textTertiary,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.padding(bottom = 24.dp),
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// PROGRESS DOTS
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun ProgressDots(
    totalSteps: Int,
    currentStep: Int,
    accent: Color,
    border: Color,
    bg: Color,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        repeat(totalSteps) { index ->
            val isActive = index <= currentStep
            val isCompleted = index < currentStep
            val size by animateDpAsState(
                targetValue = if (index == currentStep) 10.dp else 8.dp,
                animationSpec = tween(Motion.DURATION_FAST),
                label = "dotSize",
            )
            val color by animateColorAsState(
                targetValue = when {
                    isCompleted -> accent
                    isActive -> accent.copy(alpha = 0.7f)
                    else -> border
                },
                animationSpec = tween(Motion.DURATION_FAST),
                label = "dotColor",
            )

            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(color),
            )

            if (index < totalSteps - 1) {
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// STEP 1: CONSENT
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun ConsentStep(
    tosAccepted: Boolean,
    onTosChanged: (Boolean) -> Unit,
    onOpenTerms: () -> Unit,
    onOpenPrivacy: () -> Unit,
    canProceed: Boolean,
    onNext: () -> Unit,
    accent: Color,
    accentFg: Color,
    surface: Color,
    textPrimary: Color,
    textSecondary: Color,
    border: Color,
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StepHeader(
            title = "Conditions d'utilisation",
            subtitle = "Quelques informations à vérifier avant de commencer.",
        )

        // ToS card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = surface,
            border = BorderStroke(1.dp, border),
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "En utilisant Ñkyel AI, vous acceptez nos conditions et notre politique de confidentialité.",
                    style = TextStyle(fontSize = 13.sp, color = textSecondary, lineHeight = 20.sp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onOpenTerms() },
                        color = accent.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, border),
                    ) {
                        Text(
                            "📜 CGU",
                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, color = accent),
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center,
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onOpenPrivacy() },
                        color = accent.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, border),
                    ) {
                        Text(
                            "🔒 Confidentialité",
                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, color = accent),
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                // Checkbox row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onTosChanged(!tosAccepted) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = tosAccepted,
                        onCheckedChange = onTosChanged,
                        colors = CheckboxDefaults.colors(
                            checkedColor = accent,
                            checkmarkColor = accentFg,
                            uncheckedColor = textSecondary,
                        ),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "J'accepte les conditions d'utilisation",
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textPrimary),
                    )
                }
            }
        }

        OnboardingButton(
            text = "Continuer",
            enabled = canProceed,
            onClick = onNext,
            accent = accent,
            accentFg = accentFg,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// STEP 2: IDENTITY
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun IdentityStep(
    displayName: String,
    clerkName: String,
    onNameChanged: (String) -> Unit,
    canProceed: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit,
    accent: Color,
    accentFg: Color,
    surface: Color,
    textPrimary: Color,
    textSecondary: Color,
    textTertiary: Color,
    border: Color,
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StepHeader(
            title = "Votre nom",
            subtitle = "Comment souhaitez-vous être appelé ?",
        )

        // Name input
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = surface,
            border = BorderStroke(1.dp, if (displayName.isNotEmpty()) accent.copy(alpha = 0.3f) else border),
        ) {
            BasicTextField(
                value = displayName,
                onValueChange = onNameChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textStyle = TextStyle(fontSize = 16.sp, color = textPrimary),
                cursorBrush = SolidColor(accent),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box {
                        if (displayName.isEmpty()) {
                            Text(
                                text = "Daniel Jonathan",
                                style = TextStyle(fontSize = 16.sp, color = textTertiary),
                            )
                        }
                        innerTextField()
                    }
                },
            )
        }

        // Hint if Clerk name was prefilled
        if (clerkName.isNotEmpty() && displayName != clerkName) {
            Text(
                text = "Nom depuis votre compte : $clerkName",
                style = TextStyle(fontSize = 12.sp, color = textSecondary),
                modifier = Modifier.padding(start = 4.dp),
            )
        }

        ButtonRow(
            onBack = onBack,
            onNext = onNext,
            canProceed = canProceed,
            accent = accent,
            accentFg = accentFg,
            textSecondary = textSecondary,
            border = border,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// STEP 3: LANGUAGE
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun LanguageStep(
    selectedLanguages: Set<String>,
    primaryLocale: String,
    onToggleLanguage: (String) -> Unit,
    onSetPrimary: (String) -> Unit,
    canProceed: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit,
    accent: Color,
    accentFg: Color,
    surface: Color,
    textPrimary: Color,
    textSecondary: Color,
    border: Color,
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StepHeader(
            title = "Vos langues",
            subtitle = "Sélectionnez les langues pour l'IA et l'interface.",
        )

        // Language cards
        NKYEL_LANGUAGES.forEach { lang ->
            val isSelected = lang.code in selectedLanguages
            val isPrimary = lang.code == primaryLocale

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToggleLanguage(lang.code) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) accent.copy(alpha = 0.08f) else surface,
                border = BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) accent.copy(alpha = 0.4f) else border,
                ),
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = lang.name,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) accent else textPrimary,
                            ),
                        )
                        Text(
                            text = lang.nativeName,
                            style = TextStyle(fontSize = 12.sp, color = textSecondary),
                        )
                    }

                    // Primary radio or check
                    if (isSelected) {
                        if (isPrimary) {
                            // Filled radio = primary
                            Surface(
                                modifier = Modifier.size(20.dp),
                                shape = CircleShape,
                                color = accent,
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(accentFg),
                                    )
                                }
                            }
                        } else {
                            // Checkmark chip = additional language
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onSetPrimary(lang.code) },
                                shape = RoundedCornerShape(6.dp),
                                color = accent.copy(alpha = 0.15f),
                            ) {
                                Icon(
                                    painter = rememberVectorPainter(Icons.Filled.Check),
                                    contentDescription = "Sélectionné",
                                    tint = accent,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .padding(2.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hint
        Text(
            text = "● = langue principale de l'interface · ✓ = langue additionnelle",
            style = TextStyle(fontSize = 11.sp, color = textSecondary),
            modifier = Modifier.padding(start = 4.dp),
        )

        ButtonRow(
            onBack = onBack,
            onNext = onNext,
            canProceed = canProceed,
            accent = accent,
            accentFg = accentFg,
            textSecondary = textSecondary,
            border = border,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// STEP 4: TELEMETRY
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun TelemetryStep(
    telemetryEnabled: Boolean,
    onTelemetryChanged: (Boolean) -> Unit,
    isSubmitting: Boolean,
    error: String?,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    accent: Color,
    accentFg: Color,
    surface: Color,
    textPrimary: Color,
    textSecondary: Color,
    border: Color,
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StepHeader(
            title = "Aider à améliorer Ñkyel AI",
            subtitle = "Vos données d'utilisation peuvent contribuer à améliorer les modèles Ñkyel.",
        )

        // Telemetry card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = surface,
            border = BorderStroke(1.dp, border),
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Les données de vos conversations pourront être utilisées, de manière anonymisée, pour entraîner les futurs modèles Ñkyel AI. Vous pouvez modifier ce choix à tout moment dans les paramètres.",
                    style = TextStyle(fontSize = 13.sp, color = textSecondary, lineHeight = 20.sp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Partager mes données d'utilisation",
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textPrimary),
                        modifier = Modifier.weight(1f),
                    )
                    Switch(
                        checked = telemetryEnabled,
                        onCheckedChange = onTelemetryChanged,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = accentFg,
                            checkedTrackColor = accent,
                            uncheckedThumbColor = accentFg,
                            uncheckedTrackColor = accent.copy(alpha = 0.2f),
                        ),
                        modifier = Modifier.scale(0.85f),
                    )
                }
            }
        }

        // Error
        if (error != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = GabomaColors.ErrorRed.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, GabomaColors.ErrorRed.copy(alpha = 0.2f)),
            ) {
                Text(
                    text = error,
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = GabomaColors.ErrorRed),
                    modifier = Modifier.padding(12.dp),
                )
            }
        }

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OnboardingBackButton(
                onClick = onBack,
                textSecondary = textSecondary,
                border = border,
                modifier = Modifier.weight(0.35f),
            )
            OnboardingButton(
                text = if (isSubmitting) "Lancement…" else "Entrer dans l'Antre",
                enabled = !isSubmitting,
                onClick = onComplete,
                accent = accent,
                accentFg = accentFg,
                modifier = Modifier.weight(0.65f),
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// SHARED COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun StepHeader(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = GabomaColors.TextPrimary,
            ),
        )
        Text(
            text = subtitle,
            style = TextStyle(fontSize = 13.sp, color = GabomaColors.TextSecondary),
        )
    }
}

@Composable
private fun OnboardingButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    accent: Color,
    accentFg: Color,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = accent,
            contentColor = accentFg,
            disabledContainerColor = accent.copy(alpha = 0.3f),
            disabledContentColor = accentFg.copy(alpha = 0.5f),
        ),
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = rememberVectorPainter(Icons.Filled.ArrowForward),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun OnboardingBackButton(
    onClick: () -> Unit,
    textSecondary: Color,
    border: Color,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, border),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = textSecondary),
    ) {
        Icon(
            painter = rememberVectorPainter(Icons.Filled.ArrowBack),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text("Retour", fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ButtonRow(
    onBack: () -> Unit,
    onNext: () -> Unit,
    canProceed: Boolean,
    accent: Color,
    accentFg: Color,
    textSecondary: Color,
    border: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OnboardingBackButton(
            onClick = onBack,
            textSecondary = textSecondary,
            border = border,
            modifier = Modifier.weight(0.35f),
        )
        OnboardingButton(
            text = "Continuer",
            enabled = canProceed,
            onClick = onNext,
            accent = accent,
            accentFg = accentFg,
            modifier = Modifier.weight(0.65f),
        )
    }
}

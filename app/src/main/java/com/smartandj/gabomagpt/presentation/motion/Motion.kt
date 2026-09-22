/* GabomaGPT · Motion.kt · SmartANDJ AI Technologies
   Shared motion constants — single source of truth for all animations
   Both platforms (Kotlin + Next.js) must use identical values.
   Fondateur : Daniel Jonathan ANDJ */

package com.smartandj.gabomagpt.presentation.motion

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * ═══════════════════════════════════════════════════════════════════════════
 *  GABOMA AI — MOTION LANGUAGE (MX)
 *  Identical values mirrored in web: src/lib/motion.ts
 * ═══════════════════════════════════════════════════════════════════════════
 */
object Motion {

    // ── Duration tokens (ms) ─────────────────────────────────────
    const val DURATION_INSTANT = 100   // icon swaps, checkbox fill
    const val DURATION_FAST    = 150   // button state changes, focus ring
    const val DURATION_BASE    = 220   // sheet/drawer open, card expand
    const val DURATION_SLOW    = 400   // route/tab transitions, page reveals

    // ── Easing tokens ────────────────────────────────────────────
    val EASE_STANDARD   = CubicBezierEasing(0.2f, 0f, 0f, 1f)     // most UI motion
    val EASE_DECELERATE = CubicBezierEasing(0f, 0f, 0f, 1f)       // things entering
    val EASE_ACCELERATE = CubicBezierEasing(0.3f, 0f, 1f, 1f)     // things leaving

    // ── Spring tokens ────────────────────────────────────────────
    const val SPRING_SNAPPY_STIFFNESS = 500f
    const val SPRING_SNAPPY_DAMPING   = Spring.DampingRatioNoBouncy

    // ── Convenience builders ─────────────────────────────────────

    /** Standard tween for most UI transitions */
    fun <T> tweenBase() = tween<T>(DURATION_BASE, easing = EASE_STANDARD)

    /** Fast tween for micro-interactions */
    fun <T> tweenFast() = tween<T>(DURATION_FAST, easing = EASE_STANDARD)

    /** Instant tween for icon swaps */
    fun <T> tweenInstant() = tween<T>(DURATION_INSTANT, easing = EASE_STANDARD)

    /** Slow tween for page-level reveals */
    fun <T> tweenSlow() = tween<T>(DURATION_SLOW, easing = EASE_STANDARD)

    /** Enter tween (decelerate) */
    fun <T> tweenEnter(duration: Int = DURATION_BASE) =
        tween<T>(duration, easing = EASE_DECELERATE)

    /** Exit tween (accelerate) */
    fun <T> tweenExit(duration: Int = DURATION_FAST) =
        tween<T>(duration, easing = EASE_ACCELERATE)

    /** Snappy spring for tactile feedback (send button, thumbs, toggles) */
    fun <T> springSnappy() = spring<T>(
        dampingRatio = SPRING_SNAPPY_DAMPING,
        stiffness = SPRING_SNAPPY_STIFFNESS,
    )

    // ── Signature mark state durations ───────────────────────────
    const val MARK_IDLE_CYCLE_MS    = 7000L  // ~7s per idle breathing cycle
    const val MARK_ACTIVE_CYCLE_MS  = 1200L  // faster during generation
}

// presentation/components/HapticManager.kt
package com.smartandj.gabomagpt.presentation.components

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  HAPTIC FEEDBACK MANAGER - Micro-interactions for premium feel
 *  iOS-like haptic types: Confirm, SegmentTick, ToggleOn, GestureEnd
 * ═══════════════════════════════════════════════════════════════════════════════
 */

enum class HapticFeedbackType {
    /** Send message / Confirm action - Strong pulse */
    CONFIRM,
    
    /** Model selection / Segment changed - Tick feedback */
    SEGMENT_TICK,
    
    /** Toggle on/off state change - Double pulse */
    TOGGLE_ON,
    
    /** Panel open/gesture end - Light pulse */
    GESTURE_END,
    
    /** Error state - Long negative pulse */
    ERROR,
    
    /** Success - Ascending pulses */
    SUCCESS
}

class HapticFeedbackManager(private val vibrator: Vibrator?) {
    
    fun provide(type: HapticFeedbackType) {
        if (vibrator == null || !vibrator.hasVibrator()) return
        
        val effect = when (type) {
            HapticFeedbackType.CONFIRM -> {
                // Strong single pulse: 50ms, amplitude 200
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    VibrationEffect.createOneShot(50, 200)
                } else {
                    return  // API < 26, no haptic support
                }
            }
            
            HapticFeedbackType.SEGMENT_TICK -> {
                // Tick feedback: light, short
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    VibrationEffect.createOneShot(30, 100)
                } else {
                    return
                }
            }
            
            HapticFeedbackType.TOGGLE_ON -> {
                // Double pulse: 25ms + 10ms gap + 25ms
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {  // API 31+
                    VibrationEffect.startComposition()
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.8f, 0)
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.8f, 50)
                        .compose()
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    VibrationEffect.createOneShot(40, 150)
                } else {
                    return
                }
            }
            
            HapticFeedbackType.GESTURE_END -> {
                // Light pulse for gesture completion
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    VibrationEffect.createOneShot(20, 80)
                } else {
                    return
                }
            }
            
            HapticFeedbackType.ERROR -> {
                // Negative feedback: longer, less amplitude
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    VibrationEffect.createOneShot(100, 150)
                } else {
                    return
                }
            }
            
            HapticFeedbackType.SUCCESS -> {
                // Ascending pulses: light → medium → strong
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {  // API 31+
                    VibrationEffect.startComposition()
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.4f, 0)
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.6f, 30)
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.8f, 60)
                        .compose()
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    VibrationEffect.createOneShot(60, 180)
                } else {
                    return
                }
            }
        }
        
        vibrator.vibrate(effect)
    }
}

/**
 * Composable wrapper for haptic feedback
 */
@Composable
fun rememberHapticFeedback(): HapticFeedbackManager {
    val context = LocalContext.current
    return remember {
        val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
        HapticFeedbackManager(vibrator)
    }
}

/**
 * Haptic feedback extension for common UI interactions
 */
fun HapticFeedbackManager.sendMessage() = provide(HapticFeedbackType.CONFIRM)
fun HapticFeedbackManager.selectModel() = provide(HapticFeedbackType.TOGGLE_ON)
fun HapticFeedbackManager.openPanel() = provide(HapticFeedbackType.GESTURE_END)
fun HapticFeedbackManager.copyCode() = provide(HapticFeedbackType.SEGMENT_TICK)
fun HapticFeedbackManager.showError() = provide(HapticFeedbackType.ERROR)
fun HapticFeedbackManager.showSuccess() = provide(HapticFeedbackType.SUCCESS)

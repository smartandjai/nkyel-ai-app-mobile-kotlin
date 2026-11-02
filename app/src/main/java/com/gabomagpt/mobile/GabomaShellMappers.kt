package com.gabomagpt.mobile

import com.smartandj.gabomagpt.domain.model.GabomaChatModel

fun ForceTier.toChatModel(): GabomaChatModel = when (this) {
    ForceTier.AURATA -> GabomaChatModel.AURATA
    ForceTier.SONAR -> GabomaChatModel.NYEL
    ForceTier.LOXO -> GabomaChatModel.WANDANA
    ForceTier.ONYX -> GabomaChatModel.ONYX_GRIS
    ForceTier.BLACK_PANTHER -> GabomaChatModel.BLACK_PANTHER
    ForceTier.NKYEL -> GabomaChatModel.GABOMA_SEER
}

fun GabomaChatModel.toForceTier(): ForceTier = when (this) {
    GabomaChatModel.AURATA -> ForceTier.AURATA
    GabomaChatModel.NYEL -> ForceTier.SONAR
    GabomaChatModel.WANDANA -> ForceTier.LOXO
    GabomaChatModel.ONYX_GRIS -> ForceTier.ONYX
    GabomaChatModel.BLACK_PANTHER -> ForceTier.BLACK_PANTHER
    GabomaChatModel.GABOMA_SEER -> ForceTier.NKYEL
}

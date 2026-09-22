package com.gabomagpt.mobile

import com.smartandj.gabomagpt.domain.model.NkyelChatModel

fun ForceTier.toChatModel(): NkyelChatModel = when (this) {
    ForceTier.AURATA -> NkyelChatModel.AURATA
    ForceTier.SONAR -> NkyelChatModel.NYEL
    ForceTier.LOXO -> NkyelChatModel.WANDANA
    ForceTier.ONYX -> NkyelChatModel.ONYX_GRIS
    ForceTier.BLACK_PANTHER -> NkyelChatModel.BLACK_PANTHER
    ForceTier.NKYEL -> NkyelChatModel.NKYEL_SEER
}

fun NkyelChatModel.toForceTier(): ForceTier = when (this) {
    NkyelChatModel.AURATA -> ForceTier.AURATA
    NkyelChatModel.NYEL -> ForceTier.SONAR
    NkyelChatModel.WANDANA -> ForceTier.LOXO
    NkyelChatModel.ONYX_GRIS -> ForceTier.ONYX
    NkyelChatModel.BLACK_PANTHER -> ForceTier.BLACK_PANTHER
    NkyelChatModel.NKYEL_SEER -> ForceTier.NKYEL
}

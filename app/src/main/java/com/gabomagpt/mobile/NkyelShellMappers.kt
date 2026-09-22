package com.ÑKYEL AI.mobile

import com.smartandj.ÑKYEL AI.domain.model.NkyelChatModel

fun ForceTier.toChatModel(): NkyelChatModel = when (this) {
    ForceTier.AURATA -> NkyelChatModel.AURATA
    ForceTier.SONAR -> NkyelChatModel.NYEL
    ForceTier.LOXO -> NkyelChatModel.WANDANA
    ForceTier.ONYX -> NkyelChatModel.ONYX_GRIS
    ForceTier.BLACK_PANTHER -> NkyelChatModel.BLACK_PANTHER
    ForceTier.NKYEL -> NkyelChatModel.Nkyel_SEER
}

fun NkyelChatModel.toForceTier(): ForceTier = when (this) {
    NkyelChatModel.AURATA -> ForceTier.AURATA
    NkyelChatModel.NYEL -> ForceTier.SONAR
    NkyelChatModel.WANDANA -> ForceTier.LOXO
    NkyelChatModel.ONYX_GRIS -> ForceTier.ONYX
    NkyelChatModel.BLACK_PANTHER -> ForceTier.BLACK_PANTHER
    NkyelChatModel.Nkyel_SEER -> ForceTier.NKYEL
}

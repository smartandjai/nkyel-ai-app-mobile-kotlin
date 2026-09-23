package com.gabomagpt.mobile

import com.smartandj.gabomagpt.domain.model.NkyelChatModel

fun ForceTier.toChatModel(): NkyelChatModel = when (this) {
    ForceTier.CHUI -> NkyelChatModel.CHUI
    ForceTier.TAI -> NkyelChatModel.TAI
    ForceTier.RADI -> NkyelChatModel.RADI
    ForceTier.RECHERCHE -> NkyelChatModel.RECHERCHE_WEB
    ForceTier.ONYX -> NkyelChatModel.ONYX_GRIS
    ForceTier.BLACK_PANTHER -> NkyelChatModel.BLUE_PANTHER
    ForceTier.NKYEL -> NkyelChatModel.NKYEL_SEER
}

fun NkyelChatModel.toForceTier(): ForceTier = when (this) {
    NkyelChatModel.CHUI -> ForceTier.CHUI
    NkyelChatModel.TAI -> ForceTier.TAI
    NkyelChatModel.RADI -> ForceTier.RADI
    NkyelChatModel.RECHERCHE_WEB -> ForceTier.RECHERCHE
    NkyelChatModel.ONYX_GRIS -> ForceTier.ONYX
    NkyelChatModel.BLUE_PANTHER -> ForceTier.BLACK_PANTHER
    NkyelChatModel.NKYEL_SEER -> ForceTier.NKYEL
}

package com.smartandj.gabomagpt.presentation.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.smartandj.gabomagpt.R

object GabomaIcons {
    val PawNew: ImageVector
        @Composable
        get() = ImageVector.vectorResource(id = R.drawable.gaboma_pawnew)

    val PawHistory: ImageVector
        @Composable
        get() = ImageVector.vectorResource(id = R.drawable.gaboma_pawhistory)

    val Rendu: ImageVector
        @Composable
        get() = ImageVector.vectorResource(id = R.drawable.gaboma_rendu)

    val RadarWandana: ImageVector
        @Composable
        get() = ImageVector.vectorResource(id = R.drawable.gaboma_radarwandana)

    val Wandana: ImageVector
        @Composable
        get() = ImageVector.vectorResource(id = R.drawable.gaboma_wandana)

    val Projets: ImageVector
        @Composable
        get() = ImageVector.vectorResource(id = R.drawable.gaboma_projets)

    val Trophee: ImageVector
        @Composable
        get() = ImageVector.vectorResource(id = R.drawable.gaboma_trophee)

    val Ombre: ImageVector
        @Composable
        get() = ImageVector.vectorResource(id = R.drawable.gaboma_ombre)
    val lucide_ghost = Icons.Filled.Settings
    val lucide_more_vertical = Icons.Filled.MoreVert
    val lucide_info = Icons.Filled.Info
    val Lock = Icons.Filled.Lock
    val lucide_check = Icons.Filled.Check
    val Message = Icons.Filled.ChatBubble
    val lucide_plus = Icons.Filled.Add
    val BatteryChargingFull = Icons.Filled.BatteryChargingFull
    val lucide_chevron_up = Icons.Filled.KeyboardArrowUp
    val lucide_chevron_down = Icons.Filled.KeyboardArrowDown
    val CameraAlt = Icons.Filled.CameraAlt
    val lucide_file_text = Icons.Filled.Description
    val lucide_layers = Icons.Filled.Layers
    val Extension = Icons.Filled.Extension
}

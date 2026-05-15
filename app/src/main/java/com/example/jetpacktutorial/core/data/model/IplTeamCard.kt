package com.example.jetpacktutorial.core.data.model

import java.io.Serializable

data class IplTeamCard(
    val teamId: String,
    val name: String,
    val shortCode: String,      // e.g., "RCB", "MI", "CSK"
    val fanCountFormatted: String, // e.g., "1.2M Fans"
    val primaryColorHex: String,  // Primary branding hex for gradient accents
    val secondaryColorHex: String
) : Serializable
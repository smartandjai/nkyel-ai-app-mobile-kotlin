package com.smartandj.gabomagpt.domain.model

data class SourceRef(
    val title: String,
    val host: String,
    val url: String,
    val snippet: String,
    val confidence: Float // 0.0 - 1.0 from Tavily score
)

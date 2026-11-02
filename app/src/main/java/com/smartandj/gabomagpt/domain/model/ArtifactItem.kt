package com.smartandj.gabomagpt.domain.model

enum class ArtifactType { TEXT, MARKDOWN, HTML, CODE, PDF, DOCX, XLSX, PPTX }

data class ArtifactItem(
    val id: String,
    val title: String,
    val type: ArtifactType,
    val content: String,
    val filePath: String? = null,
    val footer: String = "Généré par Gaboma AI"
)

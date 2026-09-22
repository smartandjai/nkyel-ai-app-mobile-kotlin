package com.smartandj.gabomagpt.domain.model

enum class ArtifactType { TEXT, MARKDOWN, HTML, CODE, PDF, DOCX, XLSX, PPTX }

data class ArtifactItem(
    val id: String,
    val title: String,
    val type: ArtifactType,
    val content: String,
    val filePath: String? = null,
    val storageUrl: String? = null,
    val sizeBytes: Long? = null,
    val version: Int = 1,
    val footer: String = "Généré par Ñkyel AI"
)

typealias NkyelArtifactItem = ArtifactItem
typealias NkyelArtifactType = ArtifactType

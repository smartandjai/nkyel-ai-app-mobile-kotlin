package com.smartandj.gabomagpt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val role: String,
    val content: String,
    val modelDisplayName: String?,
    val sourcesJson: String?,
    val artifactJson: String?,
    val createdAtMillis: Long
)

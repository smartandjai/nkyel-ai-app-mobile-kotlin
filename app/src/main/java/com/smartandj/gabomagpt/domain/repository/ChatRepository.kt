package com.smartandj.gabomagpt.domain.repository

import com.smartandj.gabomagpt.data.remote.dto.ChatStreamEvent
import com.smartandj.gabomagpt.domain.model.GabomaChatModel
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun streamMessage(
        message: String,
        model: GabomaChatModel,
        sessionId: String? = null,
        isLoxoActive: Boolean = false
    ): Flow<ChatStreamEvent>
}

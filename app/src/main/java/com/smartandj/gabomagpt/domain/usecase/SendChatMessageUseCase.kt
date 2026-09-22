package com.smartandj.gabomagpt.domain.usecase

import com.smartandj.gabomagpt.data.remote.dto.ChatStreamEvent
import com.smartandj.gabomagpt.domain.model.GabomaChatModel
import com.smartandj.gabomagpt.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(
        message: String,
        model: GabomaChatModel,
        sessionId: String?,
        isLoxoActive: Boolean
    ): Flow<ChatStreamEvent> {
        return repository.streamMessage(
            message = message,
            model = model,
            sessionId = sessionId,
            isLoxoActive = isLoxoActive
        )
    }
}

package com.smartandj.gabomagpt.domain.usecase

import com.smartandj.gabomagpt.data.local.dao.ChatDao
import com.smartandj.gabomagpt.data.local.entity.ConversationEntity
import com.smartandj.gabomagpt.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadConversationHistoryUseCase @Inject constructor(
    private val chatDao: ChatDao
) {
    fun getConversations(): Flow<List<ConversationEntity>> {
        return chatDao.getConversations()
    }

    fun getMessages(conversationId: String): Flow<List<MessageEntity>> {
        return chatDao.getMessagesForConversation(conversationId)
    }

    suspend fun saveConversation(conversation: ConversationEntity) {
        chatDao.insertConversation(conversation)
    }

    suspend fun saveMessage(message: MessageEntity) {
        chatDao.insertMessage(message)
    }
}

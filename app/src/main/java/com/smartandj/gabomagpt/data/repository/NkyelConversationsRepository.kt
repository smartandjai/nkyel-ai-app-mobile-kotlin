/**
 * Ñkyel AI — Conversations Repository (Neon-backed)
 * SmartANDJ AI Technologies · Founder: Daniel Jonathan ANDJ
 */
package com.smartandj.gabomagpt.data.repository

import com.smartandj.gabomagpt.data.remote.NkyelApiClient
import com.smartandj.gabomagpt.data.remote.NkyelApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class NkyelConversation(
    val id: String,
    val workspace_id: String? = null,
    val title: String? = null,
    val conversation_type: String = "CHAT",
    val model_profile: String? = null,
    val status: String = "active",
    val message_count: Int = 0,
    val last_message_at: String? = null,
    val created_at: String = "",
    val updated_at: String = "",
)

@Serializable
data class NkyelMessage(
    val id: String,
    val conversation_id: String,
    val role: String,
    val content: String,
    val model_id: String? = null,
    val token_count: Int? = null,
    val parent_message_id: String? = null,
    val created_at: String = "",
)

@Singleton
class NkyelConversationsRepository @Inject constructor(
    private val api: NkyelApiClient,
) {
    private val _conversations = MutableStateFlow<List<NkyelConversation>>(emptyList())
    val conversations: StateFlow<List<NkyelConversation>> = _conversations

    private val _messages = MutableStateFlow<List<NkyelMessage>>(emptyList())
    val messages: StateFlow<List<NkyelMessage>> = _messages

    private val _currentId = MutableStateFlow<String?>(null)
    val currentConversationId: StateFlow<String?> = _currentId

    suspend fun fetchConversations() {
        try {
            val resp = api.get(NkyelApiConfig.CONVERSATIONS)
            if (resp.isSuccessful) {
                val body = resp.body?.string() ?: "[]"
                val list = api.json.decodeFromString<List<NkyelConversation>>(body)
                _conversations.value = list
            }
        } catch (e: Exception) {
            // Network error — keep existing state
        }
    }

    suspend fun createConversation(
        title: String,
        type: String = "CHAT",
        modelProfile: String? = null,
    ): NkyelConversation? {
        return try {
            val payload = buildString {
                append("{\"title\":\"${title.replace("\"", "\\\"")}\",\"conversation_type\":\"$type\"")
                modelProfile?.let { append(",\"model_profile\":\"$it\"") }
                append("}")
            }
            val resp = api.post(NkyelApiConfig.CONVERSATIONS, payload)
            if (resp.isSuccessful) {
                val body = resp.body?.string() ?: return null
                val conv = api.json.decodeFromString<NkyelConversation>(body)
                _conversations.value = listOf(conv) + _conversations.value
                _currentId.value = conv.id
                _messages.value = emptyList()
                conv
            } else null
        } catch (e: Exception) { null }
    }

    suspend fun fetchMessages(conversationId: String) {
        try {
            val resp = api.get("${NkyelApiConfig.CONVERSATIONS}/$conversationId/messages")
            if (resp.isSuccessful) {
                val body = resp.body?.string() ?: "[]"
                val msgs = api.json.decodeFromString<List<NkyelMessage>>(body)
                _messages.value = msgs
                _currentId.value = conversationId
            }
        } catch (_: Exception) {}
    }

    suspend fun persistMessage(
        conversationId: String,
        role: String,
        content: String,
        modelId: String? = null,
    ): NkyelMessage? {
        return try {
            val payload = buildString {
                append("{\"role\":\"$role\",\"content\":\"${content.replace("\"", "\\\"").replace("\n", "\\n")}\"")
                modelId?.let { append(",\"model_id\":\"$it\"") }
                append("}")
            }
            val resp = api.post("${NkyelApiConfig.CONVERSATIONS}/$conversationId/messages", payload)
            if (resp.isSuccessful) {
                val body = resp.body?.string() ?: return null
                val msg = api.json.decodeFromString<NkyelMessage>(body)
                _messages.value = _messages.value + msg
                msg
            } else null
        } catch (_: Exception) { null }
    }

    suspend fun updateTitle(conversationId: String, title: String) {
        try {
            api.patch("${NkyelApiConfig.CONVERSATIONS}/$conversationId",
                "{\"title\":\"${title.replace("\"", "\\\"")}\"}")
            _conversations.value = _conversations.value.map {
                if (it.id == conversationId) it.copy(title = title) else it
            }
        } catch (_: Exception) {}
    }

    suspend fun deleteConversation(conversationId: String) {
        try {
            api.delete("${NkyelApiConfig.CONVERSATIONS}/$conversationId")
            _conversations.value = _conversations.value.filter { it.id != conversationId }
            if (_currentId.value == conversationId) {
                _currentId.value = null
                _messages.value = emptyList()
            }
        } catch (_: Exception) {}
    }

    fun selectConversation(id: String?) { _currentId.value = id }
    fun clearMessages() { _messages.value = emptyList() }
}

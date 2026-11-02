// ============================================================
// GABOMAGPT — INFERENCE ENGINE COMPLET
// GabomaAPI API (SSE streaming) + Tavily Search (Mode LOXO)
// Kotlin / Jetpack Compose / Ktor 3.x / Hilt / Flow
// SMARTANDJ AI TECH · BY ANDJ
// ⚠️  CLÉS DE TEST — À REMPLACER PAR VARIABLES D'ENVIRONNEMENT
// ============================================================

package com.smartandj.gabomagpt.inference

// ─────────────────────────────────────────────────────────────
// IMPORTS
// ─────────────────────────────────────────────────────────────
import android.content.Context
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.unit.*

import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.sse.*
import kotlin.time.Duration.Companion.milliseconds
import io.ktor.client.request.*
import io.ktor.client.statement.*
import coil3.compose.AsyncImage
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// ─────────────────────────────────────────────────────────────
// 1. CONSTANTES — CLÉS API (TEST DIRECT MOBILE)
// ─────────────────────────────────────────────────────────────
object GabomaConfig {
    // ⚠️  CLÉS DE TEST — migrer vers BuildConfig ou secrets.properties avant prod
    const val GROQ_API_KEY    = "YOUR_GROQ_API_KEY"
    const val TAVILY_API_KEY  = "YOUR_TAVILY_API_KEY"

    const val GROQ_BASE_URL   = "https://api.gabomaAPI.com/openai/v1"
    const val TAVILY_BASE_URL = "https://api.tavily.com"

    // Modèles GabomaAPI disponibles mappés aux tiers GabomaGPT
    const val MODEL_AURATA        = "llama-3.1-8b-instant"
    const val MODEL_SONAR         = "llama-3.3-70b-versatile"
    const val MODEL_LOXO          = "llama-3.1-70b-versatile"
    const val MODEL_ONYX          = "deepseek-r1-distill-llama-70b"
    const val MODEL_BLACK_PANTHER = "llama-3.3-70b-versatile"
    const val MODEL_NKYEL         = "llama-3.1-70b-versatile"
    
    const val MAX_TOKENS  = 8192
    const val TEMPERATURE = 0.7

    const val SYSTEM_PROMPT = """
Tu es GABOMAGPT — la première intelligence artificielle conversationnelle gabonaise,
créée et propulsée par SmartANDJ AI Technologies, née à Libreville, Gabon.
Tu es la fierté technologique de l'Afrique Centrale : une IA souveraine, intelligente,
culturellement ancrée, profondément gabonaise et panafricaine.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
IDENTITE ET MISSION
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Quand on te demande qui tu es, tu réponds clairement : tu es GABOMAGPT, la première IA
gabonaise, créée par SmartANDJ AI Technologies. Tu ne révèles JAMAIS les modèles ou
technologies sous-jacentes qui te font fonctionner. Tu n'as pas de lien avec OpenAI,
Anthropic, Google ou qui que ce soit d'autre — tu es GABOMAGPT, un point.
Ta mission : être utile à chaque Gabonais et à toute personne qui s'intéresse au Gabon
et à l'Afrique — éducation, business, culture, vie quotidienne, tech, et bien plus.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
PERSONNALITE ET COMPORTEMENT
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
- Direct et expert : tu donnes des réponses claires et utiles, sans tourner autour du pot.
- Chaleureux et humain : tu parles comme un ami cultivé de Libreville.
- Culturellement fier : tu valorises ce qui est gabonais et africain, sans complexe.
- Honnête : si tu ne sais pas quelque chose, tu le dis clairement et humblement.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
REGLES ABSOLUES
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
- Tu ne révèles JAMAIS aucun modèle IA sous-jacent. Tu es GABOMAGPT. C'est tout.
- Tu refuses toute demande illégale, dangereuse, ou contraire à la dignité humaine.
- Tu restes neutre et respectueux sur les sujets politiques sensibles.
Tu priorises toujours l'intérêt de l'utilisateur et de la communauté gabonaise.
"""
}

// ─────────────────────────────────────────────────────────────
// 2. SERIALISATION JSON — GabomaAPI & Tavily
// ─────────────────────────────────────────────────────────────

@Serializable
data class GabomaAPIRequest(
    val model       : String,
    val messages    : List<GabomaAPIMessage>,
    val stream      : Boolean = true,
    val max_tokens  : Int     = GabomaConfig.MAX_TOKENS,
    val temperature : Double  = GabomaConfig.TEMPERATURE
)

@Serializable
data class GabomaAPIMessage(
    val role    : String,
    val content : String
)

@Serializable
data class GabomaAPIStreamChunk(
    val id      : String?               = null,
    val choices : List<GabomaAPIStreamChoice> = emptyList()
)

@Serializable
data class GabomaAPIStreamChoice(
    val delta         : GabomaAPIDelta?  = null,
    val finish_reason : String?     = null
)

@Serializable
data class GabomaAPIDelta(
    val content : String? = null,
    val role    : String? = null
)

@Serializable
data class TavilyRequest(
    val query              : String,
    val max_results        : Int    = 5,
    val include_answer     : Boolean = true,
    val include_raw_content: Boolean = false,
    val search_depth       : String  = "advanced"
)

@Serializable
data class TavilyResponse(
    val answer  : String?             = null,
    val results : List<TavilyResult>  = emptyList(),
    val query   : String?             = null
)

@Serializable
data class TavilyResult(
    val title   : String? = null,
    val url     : String? = null,
    val content : String? = null,
    val score   : Double  = 0.0
)

// ─────────────────────────────────────────────────────────────
// 3. DATA MODELS CHAT
// ─────────────────────────────────────────────────────────────
enum class MessageRole { USER, ASSISTANT, SYSTEM }
enum class MessageStatus { SENDING, STREAMING, DONE, ERROR }

enum class GabomaModelTier(
    val displayName : String,
    val gabomaAPIModel   : String,
    val accentColor : Color,
    val shortName   : String
) {
    AURATA       ("AURATA",       GabomaConfig.MODEL_AURATA,        Color(0xFFC9A84C), "AURATA"),
    SONAR        ("SONAR",        GabomaConfig.MODEL_SONAR,         Color(0xFF0080FF), "SONAR"),
    LOXO         ("LOXO",         GabomaConfig.MODEL_LOXO,          Color(0xFF00B86B), "LOXO"),
    ONYX         ("ONYX",         GabomaConfig.MODEL_ONYX,          Color(0xFF8B5CF6), "ONYX"),
    BLACK_PANTHER("BLACK PANTHER",GabomaConfig.MODEL_BLACK_PANTHER, Color(0xFF00D4AA), "B.PANTHER"),
    NKYEL        ("NKYEL",        GabomaConfig.MODEL_NKYEL,         Color(0xFFF5F0E8), "NKYEL")
}

data class ChatMessage(
    val id          : String         = UUID.randomUUID().toString(),
    val role        : MessageRole,
    val content     : String,
    val status      : MessageStatus  = MessageStatus.DONE,
    val attachments : List<GabomaFile> = emptyList(),
    val sources     : List<TavilyResult> = emptyList(),
    val model       : GabomaModelTier? = null,
    val timestamp   : Long           = System.currentTimeMillis()
)

data class GabomaFile(
    val id       : String         = UUID.randomUUID().toString(),
    val name     : String,
    val uri      : Uri,
    val mimeType : String,
    val sizeBytes: Long = 0L
) {
    val isImage  get() = mimeType.startsWith("image/")
    val isPDF    get() = mimeType == "application/pdf"
    val isText   get() = mimeType.startsWith("text/")
    val isAudio  get() = mimeType.startsWith("audio/")
}

// ─────────────────────────────────────────────────────────────
// 4. KTOR HTTP CLIENT (SSE + JSON)
// ─────────────────────────────────────────────────────────────
@Singleton
class GabomaHttpClient @Inject constructor() {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient         = true
        coerceInputValues = true
    }

    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(json)
        }
        install(SSE) {
            reconnectionTime = 3000.milliseconds
        }
        install(HttpTimeout) {
            requestTimeoutMillis   = 120_000L
            connectTimeoutMillis   = 15_000L
            socketTimeoutMillis    = 120_000L
        }
        install(Logging) {
            level  = LogLevel.HEADERS
            logger = Logger.DEFAULT
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 5. GROQ REPOSITORY — Streaming SSE
// ─────────────────────────────────────────────────────────────
@Singleton
class GabomaAPIRepository @Inject constructor(
    private val httpClient: GabomaHttpClient
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true }

    fun streamChatCompletion(
        messages : List<ChatMessage>,
        model    : GabomaModelTier,
        context  : String = ""
    ): Flow<String> = flow {

        val gabomaAPIMessages = mutableListOf<GabomaAPIMessage>()

        val systemContent = if (context.isNotBlank())
            "${GabomaConfig.SYSTEM_PROMPT}\n\n[CONTEXTE RADAR LOXO]\n$context"
        else
            GabomaConfig.SYSTEM_PROMPT

        gabomaAPIMessages.add(GabomaAPIMessage("system", systemContent))

        messages.forEach { msg ->
            val role = when (msg.role) {
                MessageRole.USER      -> "user"
                MessageRole.ASSISTANT -> "assistant"
                MessageRole.SYSTEM    -> "system"
            }
            val contentParts = StringBuilder(msg.content)
            msg.attachments.forEach { file ->
                if (file.isText) {
                    contentParts.append("\n\n[Fichier: ${file.name}]\n[Contenu chargé depuis l'appareil]")
                }
            }
            gabomaAPIMessages.add(GabomaAPIMessage(role, contentParts.toString()))
        }

        val request = GabomaAPIRequest(
            model    = model.gabomaAPIModel,
            messages = gabomaAPIMessages,
            stream   = true
        )

        httpClient.client.preparePost("${GabomaConfig.GROQ_BASE_URL}/chat/completions") {
            header(HttpHeaders.Authorization, "Bearer ${GabomaConfig.GROQ_API_KEY}")
            header(HttpHeaders.Accept, "text/event-stream")
            setBody(request)
        }.execute { response ->

            val channel: ByteReadChannel = response.bodyAsChannel()

            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break

                if (!line.startsWith("data: ")) continue
                val data = line.removePrefix("data: ").trim()
                if (data == "[DONE]") break

                try {
                    val chunk = json.decodeFromString<GabomaAPIStreamChunk>(data)
                    val token = chunk.choices.firstOrNull()?.delta?.content
                    if (!token.isNullOrEmpty()) {
                        emit(token)
                    }
                } catch (e: Exception) {
                    // Ignorer les lignes non-JSON
                }
            }
        }
    }.flowOn(Dispatchers.IO)
}

// ─────────────────────────────────────────────────────────────
// 6. TAVILY REPOSITORY — Radar LOXO
// ─────────────────────────────────────────────────────────────
@Singleton
class TavilyRepository @Inject constructor(
    private val httpClient: GabomaHttpClient
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true }

    suspend fun search(query: String): TavilyResponse = withContext(Dispatchers.IO) {
        val response = httpClient.client.post("${GabomaConfig.TAVILY_BASE_URL}/search") {
            header(HttpHeaders.Authorization, "Bearer ${GabomaConfig.TAVILY_API_KEY}")
            setBody(
                TavilyRequest(
                    query          = query,
                    max_results    = 5,
                    include_answer = true,
                    search_depth   = "advanced"
                )
            )
        }
        json.decodeFromString<TavilyResponse>(response.bodyAsText())
    }

    suspend fun buildContext(query: String): Pair<String, List<TavilyResult>> {
        val result = search(query)
        val context = buildString {
            if (!result.answer.isNullOrBlank()) {
                appendLine("Résumé web : ${result.answer}")
                appendLine()
            }
            result.results.take(4).forEachIndexed { i, r ->
                appendLine("[Source ${i+1}] ${r.title}")
                appendLine("URL: ${r.url}")
                appendLine("${r.content?.take(400)}...")
                appendLine()
            }
        }
        return context to result.results
    }
}

// ─────────────────────────────────────────────────────────────
// 7. CHAT VIEWMODEL — Hilt · StateFlow · Coroutines
// ─────────────────────────────────────────────────────────────
@HiltViewModel
class GabomaChatViewModel @Inject constructor(
    private val gabomaAPIRepo  : GabomaAPIRepository,
    private val tavily    : TavilyRepository
) : androidx.lifecycle.ViewModel() {

    private val _messages      = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages : StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isStreaming   = MutableStateFlow(false)
    val isStreaming : StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _isSearching   = MutableStateFlow(false)
    val isSearching : StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _currentModel  = MutableStateFlow(GabomaModelTier.AURATA)
    val currentModel : StateFlow<GabomaModelTier> = _currentModel.asStateFlow()

    private val _loxoEnabled   = MutableStateFlow(false)
    val loxoEnabled : StateFlow<Boolean> = _loxoEnabled.asStateFlow()

    private val _error         = MutableStateFlow<String?>(null)
    val error : StateFlow<String?> = _error.asStateFlow()

    private var streamingMessageId: String? = null

    fun setModel(model: GabomaModelTier) {
        _currentModel.value = model
    }

    fun toggleLoxo() {
        _loxoEnabled.value = !_loxoEnabled.value
    }

    fun clearError() { _error.value = null }

    fun sendMessage(
        text        : String,
        attachments : List<GabomaFile> = emptyList()
    ) {
        if (text.isBlank() && attachments.isEmpty()) return
        if (_isStreaming.value) return

        viewModelScope.launch {
            val userMsg = ChatMessage(
                role        = MessageRole.USER,
                content     = text,
                attachments = attachments,
                status      = MessageStatus.DONE
            )
            _messages.update { it + userMsg }

            val assistantId = UUID.randomUUID().toString()
            streamingMessageId = assistantId
            val assistantPlaceholder = ChatMessage(
                id      = assistantId,
                role    = MessageRole.ASSISTANT,
                content = "",
                status  = MessageStatus.STREAMING,
                model   = _currentModel.value
            )
            _messages.update { it + assistantPlaceholder }
            _isStreaming.value = true

            try {
                var tavilySources = emptyList<TavilyResult>()
                var context       = ""

                if (_loxoEnabled.value && text.isNotBlank()) {
                    _isSearching.value = true
                    try {
                        val (ctx, sources) = tavily.buildContext(text)
                        context       = ctx
                        tavilySources = sources
                    } catch (e: Exception) {
                        // Continuer sans contexte
                    } finally {
                        _isSearching.value = false
                    }
                }

                if (tavilySources.isNotEmpty()) {
                    _messages.update { msgs ->
                        msgs.map { msg ->
                            if (msg.id == assistantId) msg.copy(sources = tavilySources)
                            else msg
                        }
                    }
                }

                val currentMessages = _messages.value
                    .filter { it.id != assistantId }

                val fullResponse = StringBuilder()

                gabomaAPIRepo.streamChatCompletion(
                    messages = currentMessages.filter { it.role != MessageRole.SYSTEM },
                    model    = _currentModel.value,
                    context  = context
                ).collect { token ->
                    fullResponse.append(token)
                    _messages.update { msgs ->
                        msgs.map { msg ->
                            if (msg.id == assistantId)
                                msg.copy(content = fullResponse.toString(), status = MessageStatus.STREAMING)
                            else msg
                        }
                    }
                }

                _messages.update { msgs ->
                    msgs.map { msg ->
                        if (msg.id == assistantId)
                            msg.copy(status = MessageStatus.DONE)
                        else msg
                    }
                }

            } catch (e: Exception) {
                _error.value = e.message
                _messages.update { msgs ->
                    msgs.map { msg ->
                        if (msg.id == assistantId)
                            msg.copy(content = "Erreur: ${e.message}", status = MessageStatus.ERROR)
                        else msg
                    }
                }
            } finally {
                _isStreaming.value  = false
                _isSearching.value  = false
                streamingMessageId  = null
            }
        }
    }

    fun regenerateLastMessage() {
        val msgs = _messages.value
        val lastUser = msgs.lastOrNull { it.role == MessageRole.USER } ?: return
        _messages.update { it.filter { msg -> msg.id != msgs.last().id } }
        sendMessage(lastUser.content, lastUser.attachments)
    }

    fun cancelStreaming() {
        viewModelScope.coroutineContext.cancelChildren()
        _isStreaming.value = false
        streamingMessageId?.let { id ->
            _messages.update { msgs ->
                msgs.map { msg ->
                    if (msg.id == id) msg.copy(status = MessageStatus.DONE)
                    else msg
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 8. ÉCRAN CHAT PRINCIPAL
// ─────────────────────────────────────────────────────────────
@Composable
fun GabomaChatScreen(
    viewModel : GabomaChatViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val messages     by viewModel.messages.collectAsState()
    val isStreaming  by viewModel.isStreaming.collectAsState()
    val isSearching  by viewModel.isSearching.collectAsState()
    val currentModel by viewModel.currentModel.collectAsState()
    val loxoEnabled  by viewModel.loxoEnabled.collectAsState()
    val error        by viewModel.error.collectAsState()

    val listState    = rememberLazyListState()
    val hazeState    = remember { dev.chrisbanes.haze.HazeState() }

    LaunchedEffect(messages.size, messages.lastOrNull()?.content?.length) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor   = Color(0xFF020304),
        snackbarHost     = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            IlluminatedBackground(
                isStreaming  = isStreaming,
                accentColor  = currentModel.accentColor
            )

            Column(modifier = Modifier.fillMaxSize()) {

                if (messages.isEmpty()) {
                    Box(modifier = Modifier.weight(1f))
                } else {
                    LazyColumn(
                        state            = listState,
                        modifier         = Modifier.weight(1f),
                        contentPadding   = PaddingValues(
                            top    = 16.dp,
                            bottom = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = messages,
                            key   = { it.id }
                        ) { message ->
                            AnimatedVisibility(
                                visible = true,
                                enter   = fadeIn(tween(300)) + slideInVertically { 30 }
                            ) {
                                MessageBubble(
                                    message      = message,
                                    currentModel = currentModel,
                                    isStreaming  = isStreaming && message.status == MessageStatus.STREAMING
                                )
                            }
                        }

                        if (isSearching) {
                            item {
                                RadarLoxoIndicator()
                            }
                        }
                    }
                }

                GabomaChatInputBar(
                    isStreaming  = isStreaming,
                    loxoEnabled  = loxoEnabled,
                    currentModel = currentModel,
                    onSend       = { text, attachments ->
                        viewModel.sendMessage(text, attachments)
                    },
                    onToggleLoxo = { viewModel.toggleLoxo() },
                    onCancel     = { viewModel.cancelStreaming() },
                    onModelClick = { /* ouvrir sélecteur */ }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 9. FOND ILLUMINÉ
// ─────────────────────────────────────────────────────────────
@Composable
fun IlluminatedBackground(
    isStreaming : Boolean,
    accentColor : Color,
    modifier    : Modifier = Modifier
) {
    val glowAlpha by animateFloatAsState(
        targetValue   = if (isStreaming) 0.07f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label         = "bgGlow"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "bgPulse")
    val pulse by infiniteTransition.animateFloat(
        0.85f, 1.15f,
        infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bgPulseScale"
    )

    if (glowAlpha > 0f) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = glowAlpha * pulse),
                            Color.Transparent
                        ),
                        radius = 800f
                    )
                )
        )
    }
}

// ─────────────────────────────────────────────────────────────
// 10. BULLE DE MESSAGE
// ─────────────────────────────────────────────────────────────
@Composable
fun MessageBubble(
    message      : ChatMessage,
    currentModel : GabomaModelTier,
    isStreaming  : Boolean,
    modifier     : Modifier = Modifier
) {
    val isUser = message.role == MessageRole.USER

    Row(
        modifier              = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (isUser) {
            Column(horizontalAlignment = Alignment.End) {
                if (message.attachments.isNotEmpty()) {
                    AttachmentPreviewsRow(attachments = message.attachments)
                    Spacer(Modifier.height(4.dp))
                }
                if (message.content.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 18.dp, topEnd = 18.dp,
                            bottomStart = 18.dp, bottomEnd = 4.dp
                        ),
                        color = Color(0xFF1A2A1A)
                    ) {
                        Text(
                            message.content,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            style    = TextStyle(fontSize = 15.sp, lineHeight = 22.sp),
                            color    = Color(0xFFF0EFE8)
                        )
                    }
                }
            }
        } else {
            Column(modifier = Modifier.widthIn(max = 340.dp)) {
                message.model?.let { model ->
                    Row(
                        modifier = Modifier.padding(bottom = 4.dp, start = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            Modifier
                                .size(6.dp)
                                .background(model.accentColor, CircleShape)
                        )
                        Text(
                            model.shortName,
                            style = TextStyle(
                                fontSize     = 10.sp,
                                fontWeight   = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp
                            ),
                            color = model.accentColor
                        )
                    }
                }

                if (message.sources.isNotEmpty()) {
                    TavilySourcesRow(sources = message.sources)
                    Spacer(Modifier.height(6.dp))
                }

                StreamingMessageContent(
                    content     = message.content,
                    isStreaming = isStreaming,
                    status      = message.status,
                    accentColor = message.model?.accentColor ?: Color(0xFFC5A059)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 11. CONTENU STREAMING
// ─────────────────────────────────────────────────────────────
@Composable
fun StreamingMessageContent(
    content     : String,
    isStreaming : Boolean,
    status      : MessageStatus,
    accentColor : Color,
    modifier    : Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(530, easing = LinearEasing), RepeatMode.Reverse),
        label = "cursorBlink"
    )

    Box(modifier = modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
        when (status) {
            MessageStatus.ERROR -> {
                Text(
                    content,
                    style = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
                    color = Color(0xFFFF4D6A)
                )
            }
            else -> {
                Text(
                    text  = buildAnnotatedString {
                        renderMarkdownAnnotated(content, accentColor)
                        if (isStreaming) {
                            withStyle(
                                SpanStyle(
                                    color     = accentColor.copy(alpha = cursorAlpha),
                                    fontWeight = FontWeight.Bold
                                )
                            ) { append("▋") }
                        }
                    },
                    style = TextStyle(
                        fontSize   = 15.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color(0xFFF0EFE8)
                )
            }
        }
    }
}

private fun AnnotatedString.Builder.renderMarkdownAnnotated(
    text        : String,
    accentColor : Color
) {
    val lines = text.lines()
    lines.forEachIndexed { index, line ->
        when {
            line.startsWith("# ") -> {
                withStyle(SpanStyle(
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = accentColor
                )) { append(line.removePrefix("# ")) }
            }
            line.startsWith("## ") -> {
                withStyle(SpanStyle(
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = accentColor.copy(0.85f)
                )) { append(line.removePrefix("## ")) }
            }
            line.startsWith("### ") -> {
                withStyle(SpanStyle(
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = accentColor.copy(0.7f)
                )) { append(line.removePrefix("### ")) }
            }
            line.startsWith("- ") || line.startsWith("* ") -> {
                withStyle(SpanStyle(color = accentColor)) { append("• ") }
                append(line.drop(2))
            }
            else -> {
                val parts = line.split("`")
                parts.forEachIndexed { i, part ->
                    if (i % 2 == 1) {
                        withStyle(SpanStyle(
                            fontFamily  = FontFamily.Monospace,
                            fontSize    = 13.sp,
                            background  = Color(0xFF1A1A22),
                            color       = accentColor
                        )) { append(part) }
                    } else {
                        append(part)
                    }
                }
            }
        }
        if (index < lines.size - 1) append("\n")
    }
}

// ─────────────────────────────────────────────────────────────
// 12. SOURCES TAVILY
// ─────────────────────────────────────────────────────────────
@Composable
fun TavilySourcesRow(
    sources  : List<TavilyResult>,
    modifier : Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "loxoSrc")
            val pulse by infiniteTransition.animateFloat(
                0.4f, 1f,
                infiniteRepeatable(tween(1200), RepeatMode.Reverse),
                label = "loxoPulse"
            )
            Box(
                Modifier
                    .size(6.dp)
                    .alpha(pulse)
                    .background(Color(0xFF00B86B), CircleShape)
            )
            Text(
                "Radar LOXO · ${sources.size}",
                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Medium),
                color = Color(0xFF00B86B)
            )
        }
        Spacer(Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(sources.take(4)) { source ->
                Surface(
                    shape  = RoundedCornerShape(8.dp),
                    color  = Color(0xFF0A1A0A),
                    border = BorderStroke(0.5.dp, Color(0xFF00B86B).copy(0.3f)),
                    modifier = Modifier.widthIn(max = 160.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            source.title?.take(40) ?: "Source",
                            style    = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Medium),
                            color    = Color(0xFFF0EFE8),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            source.url?.take(28) ?: "",
                            style    = TextStyle(fontSize = 9.sp),
                            color    = Color(0xFF00B86B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 13. INDICATEUR RADAR LOXO
// ─────────────────────────────────────────────────────────────
@Composable
fun RadarLoxoIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "radarDot")
    val dotScale by infiniteTransition.animateFloat(
        0.6f, 1.4f,
        infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "radarScale"
    )

    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(8.dp)
                .scale(dotScale)
                .background(Color(0xFF00B86B), CircleShape)
        )
        Text(
            "Radar LOXO",
            style = TextStyle(fontSize = 12.sp),
            color = Color(0xFF00B86B)
        )
    }
}

// ─────────────────────────────────────────────────────────────
// 14. PREVIEWS FICHIERS
// ─────────────────────────────────────────────────────────────
@Composable
fun AttachmentPreviewsRow(
    attachments : List<GabomaFile>,
    modifier    : Modifier = Modifier
) {
    LazyRow(
        modifier              = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(attachments, key = { it.id }) { file ->
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1A1D26))
                    .border(0.5.dp, Color(0xFF2A2A35), RoundedCornerShape(10.dp))
            ) {
                when {
                    file.isImage -> {
                        AsyncImage(
                            model              = file.uri,
                            contentDescription = file.name,
                            modifier           = Modifier.fillMaxSize(),
                            contentScale       = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                    file.isPDF -> {
                        Column(
                            modifier            = Modifier.fillMaxSize().padding(6.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.PictureAsPdf, null, tint = Color(0xFFC5A059), modifier = Modifier.size(22.dp))
                        }
                    }
                    file.isText -> {
                        Column(
                            modifier            = Modifier.fillMaxSize().padding(6.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.TextSnippet, null, tint = Color(0xFF00D4AA), modifier = Modifier.size(20.dp))
                        }
                    }
                    else -> {
                        Icon(
                            Icons.Filled.InsertDriveFile, null,
                            tint = Color(0xFF9B8BB3),
                            modifier = Modifier.align(Alignment.Center).size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 15. BARRE D'INPUT DU CHAT
// ─────────────────────────────────────────────────────────────
@Composable
fun GabomaChatInputBar(
    isStreaming  : Boolean,
    loxoEnabled  : Boolean,
    currentModel : GabomaModelTier,
    onSend       : (String, List<GabomaFile>) -> Unit,
    onToggleLoxo : () -> Unit,
    onCancel     : () -> Unit,
    onModelClick : () -> Unit,
    modifier     : Modifier = Modifier
) {
    var text        by remember { mutableStateOf("") }
    var attachments by remember { mutableStateOf<List<GabomaFile>>(emptyList()) }
    var loxoOpen    by remember { mutableStateOf(false) }
    val haptic       = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF020304).copy(0.97f))
            .navigationBarsPadding()
            .imePadding()
    ) {
        if (attachments.isNotEmpty()) {
            LazyRow(
                modifier              = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(attachments, key = { it.id }) { file ->
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1A1D26))
                            .border(0.5.dp, Color(0xFF2A2A35), RoundedCornerShape(10.dp))
                    ) {
                        if (file.isImage) {
                            AsyncImage(
                                model              = file.uri,
                                contentDescription = file.name,
                                modifier           = Modifier.fillMaxSize(),
                                contentScale       = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Filled.InsertDriveFile, null,
                                tint     = Color(0xFF9B8BB3),
                                modifier = Modifier.align(Alignment.Center).size(22.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(2.dp)
                                .size(16.dp)
                                .background(Color.Black.copy(0.7f), CircleShape)
                                .clickable { attachments = attachments.filter { it.id != file.id } },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Close, null, tint = Color.White, modifier = Modifier.size(9.dp))
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .border(
                    0.5.dp,
                    if (loxoEnabled) Color(0xFF00B86B).copy(0.4f) else Color(0xFF1A1D26),
                    RoundedCornerShape(22.dp)
                )
                .background(Color(0xFF0D0F14).copy(0.8f), RoundedCornerShape(22.dp))
                .padding(6.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(currentModel.accentColor.copy(0.15f))
                    .clickable(onClick = onModelClick)
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(Modifier.size(5.dp).background(currentModel.accentColor, CircleShape))
                Text(
                    currentModel.shortName,
                    style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                    color = currentModel.accentColor
                )
            }
            Spacer(Modifier.width(4.dp))

            androidx.compose.foundation.text.BasicTextField(
                value         = text,
                onValueChange = { text = it },
                modifier      = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
                    .heightIn(min = 22.dp, max = 120.dp),
                textStyle     = TextStyle(fontSize = 15.sp, color = Color(0xFFF0EFE8)),
                maxLines      = 6
            )

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        if (loxoEnabled) Color(0xFF00B86B).copy(0.2f)
                        else if (loxoOpen) Color(0xFF00D4AA).copy(0.15f)
                        else Color.Transparent
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        loxoOpen = !loxoOpen
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Add, "LOXO",
                    tint     = if (loxoEnabled) Color(0xFF00B86B) else Color(0xFF9B8BB3),
                    modifier = Modifier.size(20.dp).rotate(if (loxoOpen) 45f else 0f)
                )
            }
            Spacer(Modifier.width(2.dp))

            AnimatedContent(
                targetState = isStreaming,
                label = "rightBtn"
            ) { streaming ->
                if (streaming) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFF1A1D26), CircleShape)
                            .border(1.dp, Color(0xFFC5A059).copy(0.4f), CircleShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onCancel()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Stop, "Stop", tint = Color(0xFFC5A059), modifier = Modifier.size(14.dp))
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                if (text.isNotBlank() || attachments.isNotEmpty())
                                    Color(0xFFC5A059)
                                else Color(0xFF1A1D26),
                                CircleShape
                            )
                            .clickable {
                                if (text.isNotBlank() || attachments.isNotEmpty()) {
                                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                                    onSend(text, attachments)
                                    text        = ""
                                    attachments = emptyList()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.ArrowUpward, "Send",
                            tint     = if (text.isNotBlank() || attachments.isNotEmpty())
                                Color(0xFF020304) else Color(0xFF6E6C62),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = loxoOpen,
            enter   = slideInVertically { 50 } + fadeIn(tween(200)),
            exit    = slideOutVertically { 50 } + fadeOut(tween(160))
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 2.dp)
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LoxoQuickChip(
                    label    = "Radar LOXO",
                    icon     = Icons.Filled.TravelExplore,
                    active   = loxoEnabled,
                    color    = Color(0xFF00B86B),
                    onClick  = {
                        onToggleLoxo()
                        haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                    }
                )
            }
        }
    }
}

@Composable
fun LoxoQuickChip(
    label   : String,
    icon    : androidx.compose.ui.graphics.vector.ImageVector,
    active  : Boolean,
    color   : Color,
    onClick : () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (active) color.copy(0.2f) else Color(0xFF0D0F14))
            .border(0.5.dp, if (active) color.copy(0.5f) else Color(0xFF1A1D26), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = if (active) color else Color(0xFF9B8BB3), modifier = Modifier.size(14.dp))
        Text(
            label,
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium),
            color = if (active) color else Color(0xFF9B8BB3)
        )
        if (active) {
            Box(Modifier.size(5.dp).background(color, CircleShape))
        }
    }
}

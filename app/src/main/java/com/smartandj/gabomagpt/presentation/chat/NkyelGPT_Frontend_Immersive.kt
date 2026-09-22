package com.smartandj.gabomagpt.presentation.chat

import com.smartandj.gabomagpt.domain.model.ChatMessage
import com.smartandj.gabomagpt.domain.model.ChatRole

import com.smartandj.gabomagpt.domain.model.*
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Source
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.min
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope

private val AbyssBlack = Color(0xFF0A0A0F)
private val Surface1 = Color(0xFF0E0E13)
private val Surface2 = Color(0xFF14141B)
private val Surface3 = Color(0xFF1B1B24)
private val Surface4 = Color(0xFF232330)
private val GoldGaboma = Color(0xFFC9A84C)
private val GoldBright = Color(0xFFE2C56A)
private val TurquoiseIA = Color(0xFF00D4AA)
private val GlassBorder = Color(0x33FFFFFF)
private val GlassSurface = Color(0x1FFFFFFF)
private val TextPrimary = Color(0xFFF6F2E8)
private val TextSecondary = Color(0xFFCAC4B5)
private val TextMuted = Color(0xFF8E8A80)
private val Success = Color(0xFF32D296)
private val Danger = Color(0xFFFF6B7A)
private val SourceCard = Color(0xFF11141C)
private val SourceBorder = Color(0x2FFFD66E)
private val SonarBlue = Color(0xFF4A8DFF)
private val LoxoGreen = Color(0xFF19C37D)
private val OnyxPurple = Color(0xFF9275FF)
private val PantherCrimson = Color(0xFFFF6E69)
private val NkyelPearl = Color(0xFFF0E8D8)

private val ImmersiveColorScheme = darkColorScheme(
    primary = GoldGaboma,
    onPrimary = AbyssBlack,
    secondary = TurquoiseIA,
    background = AbyssBlack,
    surface = Surface1,
    surfaceVariant = Surface3,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = Color(0xFF2A2A36),
    error = Danger
)

@Composable
fun ImmersiveGabomaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ImmersiveColorScheme,
        typography = androidx.compose.material3.Typography(
            displayLarge = TextStyle(fontSize = 34.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold, color = TextPrimary),
            displayMedium = TextStyle(fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold, color = TextPrimary),
            headlineMedium = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary),
            titleLarge = TextStyle(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary),
            bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.Normal, color = TextPrimary),
            bodyMedium = TextStyle(fontSize = 15.sp, lineHeight = 23.sp, fontWeight = FontWeight.Normal, color = TextPrimary),
            labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium, color = TextPrimary),
            labelSmall = TextStyle(fontSize = 11.sp, lineHeight = 15.sp, fontWeight = FontWeight.Medium, color = TextSecondary, letterSpacing = 0.65.sp)
        ),
        content = content
    )
}

enum class GabomaThemeMode { EQUATORIAL_GOLD, ABYSS, TURQUOISE_FOCUS }
enum class WritingTone { SOBRE, STRATEGIQUE, CHALEUREUX, DIRECTIF }
enum class GabomaModel(
    val label: String,
    val subtitle: String,
    val accent: Color,
    val accent2: Color
) {
    AURATA("AURATA", "Flash rapide", GoldGaboma, GoldBright),
    SONAR("SONAR", "Raisonnement fluide", SonarBlue, TurquoiseIA),
    LOXO("LOXO", "Recherche massive", LoxoGreen, TurquoiseIA),
    ONYX("ONYX", "Puissance", OnyxPurple, GoldGaboma),
    BLACK_PANTHER("BLACK PANTHER", "Agent universel", PantherCrimson, TurquoiseIA),
    NKYEL("NKYEL", "Souveraineté", NkyelPearl, GoldGaboma)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GabomaMobileApp() {
    GabomaTheme {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val snack = remember { SnackbarHostState() }
        var selectedModel by remember { mutableStateOf(GabomaModel.BLACK_PANTHER) }
        var themeMode by remember { mutableStateOf(GabomaThemeMode.EQUATORIAL_GOLD) }
        var writingTone by remember { mutableStateOf(WritingTone.STRATEGIQUE) }
        var showModelSheet by remember { mutableStateOf(false) }
        var showSettings by remember { mutableStateOf(false) }
        var activeArtifact by remember { mutableStateOf<ArtifactItem?>(null) }
        var prompt by remember { mutableStateOf("") }
        val messages = remember { mutableStateListOf(*demoMessages().toTypedArray()) }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                LAntreDrawer(
                    selectedModel = selectedModel,
                    onSelectModel = { showModelSheet = true },
                    onOpenSettings = { showSettings = true },
                    onNewPiste = {
                        messages.clear()
                        messages.add(
                            ChatMessage(
                                id = "welcome",
                                role = ChatRole.ASSISTANT,
                                content = "Bienvenue dans Gaboma AI. Donne-moi une piste, je te rends une réponse claire, structurée et sourcée.",
                                modelDisplayName = selectedModel.label
                            )
                        )
                        scope.launch { drawerState.close() }
                    }
                )
            },
            gesturesEnabled = true
        ) {
            Scaffold(
                containerColor = themedBackground(themeMode),
                snackbarHost = { SnackbarHost(snack) }
            ) { inner ->
                Box(modifier = Modifier.fillMaxSize().background(themedBackground(themeMode))) {
                    EquatorialBackdrop(themeMode = themeMode)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(inner)
                    ) {
                        GabomaTopBar(
                            model = selectedModel,
                            onMenu = { scope.launch { drawerState.open() } },
                            onModel = { showModelSheet = true },
                            onSettings = { showSettings = true }
                        )
                        ChatStream(
                            messages = messages,
                            currentModel = selectedModel,
                            onOpenArtifact = { activeArtifact = it },
                            modifier = Modifier.weight(1f)
                        )
                        InputBarGlass(
                            prompt = prompt,
                            onPromptChange = { prompt = it },
                            model = selectedModel,
                            onSend = {
                                if (prompt.isNotBlank()) {
                                    val user = ChatMessage(
                                        id = "u${System.currentTimeMillis()}",
                                        role = ChatRole.USER,
                                        content = prompt,
                                        modelDisplayName = selectedModel.label
                                    )
                                    val ai = ChatMessage(
                                        id = "a${System.currentTimeMillis()}",
                                        role = ChatRole.ASSISTANT,
                                        content = demoAiText(selectedModel).toString(),
                                        modelDisplayName = selectedModel.label,
                                        isStreaming = true,
                                        sources = demoSources(),
                                        artifact = demoArtifact()
                                    )
                                    messages.add(user)
                                    messages.add(ai)
                                    prompt = ""
                                }
                            },
                            onAttach = {
                                scope.launch { snack.showSnackbar("Pièce jointe prête à brancher côté moteur.") }
                            }
                        )
                    }

                    AnimatedVisibility(
                        visible = activeArtifact != null,
                        enter = fadeIn(tween(220)) + slideInVertically(tween(320)) { it / 4 },
                        exit = fadeOut(tween(180)) + slideOutVertically(tween(220)) { it / 5 }
                    ) {
                        activeArtifact?.let {
                            ArtifactPanel(
                                artifact = it,
                                onClose = { activeArtifact = null }
                            )
                        }
                    }
                }
            }
        }

        if (showModelSheet) {
            ModelPickerSheet(
                selected = selectedModel,
                onDismiss = { showModelSheet = false },
                onSelected = {
                    selectedModel = it
                    showModelSheet = false
                }
            )
        }

        if (showSettings) {
            SettingsSheet(
                themeMode = themeMode,
                writingTone = writingTone,
                selectedModel = selectedModel,
                onDismiss = { showSettings = false },
                onThemeMode = { themeMode = it },
                onWritingTone = { writingTone = it },
                onModel = { selectedModel = it }
            )
        }
    }
}

@Composable
private fun themedBackground(mode: GabomaThemeMode): Color = when (mode) {
    GabomaThemeMode.EQUATORIAL_GOLD -> AbyssBlack
    GabomaThemeMode.ABYSS -> Color(0xFF050507)
    GabomaThemeMode.TURQUOISE_FOCUS -> Color(0xFF071110)
}

@Composable
fun EquatorialBackdrop(themeMode: GabomaThemeMode) {
    val pulse by rememberInfiniteTransition(label = "bg").animateFloat(
        initialValue = 0.78f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(animation = tween(5200, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
        label = "pulse"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(color = themedBackground(themeMode))
        val gold = GoldGaboma.copy(alpha = 0.10f)
        val aqua = TurquoiseIA.copy(alpha = 0.08f)
        drawCircle(
            brush = Brush.radialGradient(listOf(gold, Color.Transparent)),
            radius = size.minDimension * 0.6f * pulse,
            center = Offset(size.width * 0.2f, size.height * 0.16f)
        )
        drawCircle(
            brush = Brush.radialGradient(listOf(aqua, Color.Transparent)),
            radius = size.minDimension * 0.56f,
            center = Offset(size.width * 0.84f, size.height * 0.73f)
        )
    }
}

@Composable
fun GabomaTopBar(
    model: GabomaModel,
    onMenu: () -> Unit,
    onModel: () -> Unit,
    onSettings: () -> Unit
) {
    Surface(
        color = Surface1.copy(alpha = 0.72f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassIconButton(onClick = onMenu) { Icon(Icons.Outlined.Menu, null, tint = TextPrimary) }
            Spacer(Modifier.width(8.dp))
            BrandBadge(model = model, modifier = Modifier.weight(1f).clickable { onModel() })
            Spacer(Modifier.width(8.dp))
            GlassIconButton(onClick = onSettings) { Icon(Icons.Filled.Settings, null, tint = TextPrimary) }
        }
    }
}

@Composable
fun BrandBadge(model: GabomaModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x12FFFFFF))
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ModelLuxuryIcon(model = model, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text("Gaboma AI".toString(), style = MaterialTheme.typography.titleLarge, maxLines = 1)
            Text(model.label.toString(), style = MaterialTheme.typography.labelSmall.copy(color = model.accent), maxLines = 1)
        }
    }
}

@Composable
fun GlassIconButton(onClick: () -> Unit, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color(0x14FFFFFF))
            .border(1.dp, GlassBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
fun ChatStream(
    messages: List<ChatMessage>,
    currentModel: GabomaModel,
    onOpenArtifact: (ArtifactItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(messages, key = { it.id }) { msg ->
            if (msg.role == ChatRole.USER) {
                UserBubble(msg)
            } else {
                AssistantAnswerCard(
                    message = msg,
                    model = currentModel,
                    onOpenArtifact = onOpenArtifact
                )
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
fun UserBubble(message: ChatMessage) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Surface(
            color = GoldGaboma.copy(alpha = 0.16f),
            shape = RoundedCornerShape(22.dp, 22.dp, 8.dp, 22.dp),
            modifier = Modifier.widthIn(max = 320.dp).border(1.dp, GoldGaboma.copy(alpha = 0.22f), RoundedCornerShape(22.dp, 22.dp, 8.dp, 22.dp))
        ) {
            Text(text = message.content.toString(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                color = TextPrimary
            )
        }
    }
}

@Composable
fun AssistantAnswerCard(
    message: ChatMessage,
    model: GabomaModel,
    onOpenArtifact: (ArtifactItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            color = Surface2.copy(alpha = 0.88f),
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorder, RoundedCornerShape(26.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ModelLuxuryIcon(model = model, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(message.modelDisplayName ?: "Gaboma AI".toString(), style = MaterialTheme.typography.labelLarge.copy(color = model.accent))
                        Text("Réponse enrichie • sources visibles".toString(), style = MaterialTheme.typography.labelSmall)
                    }
                    if (message.isStreaming) StreamingGlowDot(model.accent)
                }
                Spacer(Modifier.height(12.dp))
                StreamingText(text = message.content.toString(),
                    isStreaming = message.isStreaming
                )
                if (message.artifact != null) {
                    Spacer(Modifier.height(14.dp))
                    ArtifactMiniCard(message.artifact, onOpen = { onOpenArtifact(message.artifact) })
                }
            }
        }
        if (message.sources.isNotEmpty()) {
            SourceCapsulesRow(sources = message.sources)
        }
    }
}

@Composable
fun StreamingGlowDot(color: Color) {
    val scale by rememberInfiniteTransition(label = "stream").animateFloat(
        initialValue = 0.92f,
        targetValue = 1.16f,
        animationSpec = infiniteRepeatable(animation = tween(900), repeatMode = RepeatMode.Reverse),
        label = "scale"
    )
    Box(
        modifier = Modifier
            .size(10.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun StreamingText(text: String, isStreaming: Boolean) {
    var visibleWords by remember(text) { mutableIntStateOf(if (isStreaming) 0 else text.split(" ").size) }
    val words = remember(text) { text.split(" ") }
    LaunchedEffect(text, isStreaming) {
        if (isStreaming) {
            visibleWords = 0
            while (visibleWords < words.size) {
                delay(24)
                visibleWords += 1
            }
        }
    }
    val renderText = words.take(visibleWords.coerceAtMost(words.size)).joinToString(" ")
    Text(text = if (renderText.isBlank()) " " else renderText.toString(),
        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 25.sp),
        color = TextPrimary
    )
}

@Composable
fun ArtifactMiniCard(artifact: ArtifactItem, onOpen: () -> Unit) {
    Surface(
        color = Surface3,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .border(1.dp, GoldGaboma.copy(alpha = 0.20f), RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(GoldGaboma.copy(alpha = 0.25f), TurquoiseIA.copy(alpha = 0.18f))))
                    .border(1.dp, GlassBorder, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Description, contentDescription = null, tint = GoldBright)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Le Rendu".toString(), style = MaterialTheme.typography.labelSmall.copy(color = GoldGaboma))
                Text(artifact.title.toString(), style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(artifact.type.name.toString(), style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
            }
            Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = TextMuted, modifier = Modifier.graphicsLayer(rotationZ = 180f))
        }
    }
}

@Composable
fun SourceCapsulesRow(sources: List<SourceRef>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Sources".toString(), style = MaterialTheme.typography.labelSmall.copy(color = TextMuted), modifier = Modifier.padding(start = 4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(end = 12.dp)) {
            items(sources) { src ->
                SourceCapsule(src)
            }
        }
    }
}

@Composable
fun SourceCapsule(source: SourceRef) {
    Surface(
        color = SourceCard,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .width(206.dp)
            .heightIn(min = 112.dp)
            .border(1.dp, SourceBorder, RoundedCornerShape(18.dp))
            .clickable { }
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Source, contentDescription = null, tint = GoldGaboma, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text(source.host.toString(), style = MaterialTheme.typography.labelSmall.copy(color = GoldGaboma), maxLines = 1)
            }
            Text(source.title.toString(), style = MaterialTheme.typography.labelLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(source.snippet.toString(), style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary), maxLines = 3, overflow = TextOverflow.Ellipsis)
            Text(source.confidence.toString(), style = MaterialTheme.typography.labelSmall.copy(color = TurquoiseIA))
        }
    }
}

@Composable
fun InputBarGlass(
    prompt: String,
    onPromptChange: (String) -> Unit,
    model: GabomaModel,
    onSend: () -> Unit,
    onAttach: () -> Unit
) {
    val navPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Surface(
        color = Surface1.copy(alpha = 0.86f),
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .padding(bottom = navPadding.coerceAtLeast(2.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            GlassIconButton(onClick = onAttach) {
                Icon(Icons.Outlined.AttachFile, null, tint = TextPrimary)
            }
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(model.label.toString(), style = MaterialTheme.typography.labelSmall.copy(color = model.accent), modifier = Modifier.padding(start = 6.dp, bottom = 4.dp))
                OutlinedTextField(
                    value = prompt,
                    onValueChange = onPromptChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
                    placeholder = { Text("Écris ta piste…".toString(), color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = model.accent.copy(alpha = 0.8f),
                        unfocusedBorderColor = Color(0x30FFFFFF),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = GoldGaboma,
                        focusedContainerColor = Surface2,
                        unfocusedContainerColor = Surface2
                    ),
                    minLines = 1,
                    maxLines = 5
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(model.accent, model.accent2)))
                    .clickable(onClick = onSend),
                contentAlignment = Alignment.Center
            ) {
                Text("→".toString(), fontSize = 22.sp, color = AbyssBlack, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelPickerSheet(
    selected: GabomaModel,
    onDismiss: () -> Unit,
    onSelected: (GabomaModel) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Surface1,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = { SheetHandle() }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp)) {
            Text("Choisir l'intelligence".toString(), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(12.dp))
            GabomaModel.values().forEach { model ->
                val selectedBg = if (model == selected) model.accent.copy(alpha = 0.12f) else Color.Transparent
                Surface(
                    color = selectedBg,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onSelected(model) }
                        .border(1.dp, if (model == selected) model.accent.copy(alpha = 0.28f) else Color(0x15FFFFFF), RoundedCornerShape(20.dp))
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        ModelLuxuryIcon(model = model, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(model.label.toString(), style = MaterialTheme.typography.titleLarge)
                            Text(model.subtitle.toString(), style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                        }
                        if (model == selected) Icon(Icons.Filled.Check, null, tint = model.accent)
                    }
                }
            }
            Spacer(Modifier.height(26.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    themeMode: GabomaThemeMode,
    writingTone: WritingTone,
    selectedModel: GabomaModel,
    onDismiss: () -> Unit,
    onThemeMode: (GabomaThemeMode) -> Unit,
    onWritingTone: (WritingTone) -> Unit,
    onModel: (GabomaModel) -> Unit
) {
    var sovereignVault by remember { mutableStateOf(true) }
    var sourceCards by remember { mutableStateOf(true) }
    var adaptiveContrast by remember { mutableStateOf(true) }
    var loxoWeb by remember { mutableStateOf(true) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Surface1,
        dragHandle = { SheetHandle() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        LazyColumn(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item { Text("Réglages Gaboma AI".toString(), style = MaterialTheme.typography.headlineMedium) }
            item {
                SettingsSection("Thème") {
                    ThemeRow("Equatorial Gold", themeMode == GabomaThemeMode.EQUATORIAL_GOLD) { onThemeMode(GabomaThemeMode.EQUATORIAL_GOLD) }
                    ThemeRow("Abyss OLED", themeMode == GabomaThemeMode.ABYSS) { onThemeMode(GabomaThemeMode.ABYSS) }
                    ThemeRow("Turquoise Focus", themeMode == GabomaThemeMode.TURQUOISE_FOCUS) { onThemeMode(GabomaThemeMode.TURQUOISE_FOCUS) }
                }
            }
            item {
                SettingsSection("Ton de réponse") {
                    WritingTone.values().forEachIndexed { index, tone ->
                        ThemeRow(tone.name.replace('_', ' '), writingTone == tone) { onWritingTone(tone) }
                        if (index != WritingTone.values().lastIndex) GlassHairline()
                    }
                }
            }
            item {
                SettingsSection("Moteur actif") {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        ModelLuxuryIcon(selectedModel, Modifier.size(22.dp))
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(selectedModel.label.toString(), style = MaterialTheme.typography.titleLarge)
                            Text(selectedModel.subtitle.toString(), style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                        }
                    }
                }
            }
            item {
                SettingsSection("Affichage & lisibilité") {
                    ToggleRow("Contraste adaptatif", adaptiveContrast, { adaptiveContrast = it }, "Renforce le texte sur tous les fonds")
                    GlassHairline()
                    ToggleRow("Sources visibles en cartes", sourceCards, { sourceCards = it }, "Petits carrés de sources à la fin des réponses")
                }
            }
            item {
                SettingsSection("Souveraineté") {
                    ToggleRow("Coffre-Fort Souverain", sovereignVault, { sovereignVault = it }, "Isolation renforcée des documents")
                    GlassHairline()
                    ToggleRow("LOXO Web profond", loxoWeb, { loxoWeb = it }, "Recherche et RAG plus étendus")
                }
            }
            item {
                SettingsSection("Signature") {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        AndjSovereignIcon(modifier = Modifier.size(18.dp), color = GoldGaboma)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("BY ANDJ • SMARTANDJ TECH".toString(), style = MaterialTheme.typography.labelLarge)
                            Text("Gabon-first • Panafricain par design".toString(), style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = Surface2,
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, GlassBorder, RoundedCornerShape(22.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = {
            Text(title.toString(), style = MaterialTheme.typography.labelSmall.copy(color = GoldGaboma))
            content()
        })
    }
}

@Composable
fun ThemeRow(title: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable(onClick = onClick).padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(14.dp).clip(CircleShape).background(if (selected) GoldGaboma else Surface4).border(1.dp, if (selected) GoldGaboma else Color(0x20FFFFFF), CircleShape)
        )
        Spacer(Modifier.width(12.dp))
        Text(title.toString(), style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
    }
}

@Composable
fun ToggleRow(title: String, checked: Boolean, onChecked: (Boolean) -> Unit, subtitle: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title.toString(), style = MaterialTheme.typography.bodyLarge)
            Text(subtitle.toString(), style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
        }
        Switch(checked = checked, onCheckedChange = onChecked)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactPanel(artifact: ArtifactItem, onClose: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    ModalBottomSheet(
        onDismissRequest = onClose,
        containerColor = Surface1,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = { SheetHandle() }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(Brush.linearGradient(listOf(GoldGaboma.copy(alpha = 0.28f), TurquoiseIA.copy(alpha = 0.18f)))),
                    contentAlignment = Alignment.Center
                ) {
                    when (artifact.type) {
                        ArtifactType.PDF -> Icon(Icons.Outlined.PictureAsPdf, null, tint = GoldBright)
                        else -> Icon(Icons.Filled.Description, null, tint = GoldBright)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Le Rendu".toString(), style = MaterialTheme.typography.labelSmall.copy(color = GoldGaboma))
                    Text(artifact.title.toString(), style = MaterialTheme.typography.headlineMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                GlassIconButton(onClick = onClose) { Icon(Icons.Filled.Close, null, tint = TextPrimary) }
            }
            Spacer(Modifier.height(14.dp))
            ArtifactPreviewBody(artifact)
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionChip("Copier", Icons.Outlined.ContentCopy) { clipboard.setText(AnnotatedString(artifact.content)) }
                ActionChip("Partager".toString(), Icons.Outlined.Share) { }
                ActionChip("Télécharger", Icons.Outlined.Download) { }
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
fun ActionChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(
        color = Surface3,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.clickable(onClick = onClick).border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
    ) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = GoldGaboma, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(label.toString(), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun ArtifactPreviewBody(artifact: ArtifactItem) {
    Surface(
        color = Surface2,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().heightIn(min = 280.dp).border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
    ) {
        when (artifact.type) {
            ArtifactType.CODE -> CodePreview(artifact.content)
            ArtifactType.MARKDOWN, ArtifactType.TEXT, ArtifactType.HTML, ArtifactType.DOCX, ArtifactType.XLSX, ArtifactType.PPTX -> TextPreview(artifact)
            ArtifactType.PDF -> PdfPreviewPlaceholder(artifact)
        }
    }
}

@Composable
fun TextPreview(artifact: ArtifactItem) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(artifact.type.name.toString(), style = MaterialTheme.typography.labelSmall.copy(color = TurquoiseIA))
        Text(artifact.content.toString(), style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
        Text(artifact.footer.toString(), style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
    }
}

@Composable
fun CodePreview(code: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Surface4).padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("code".toString(), style = MaterialTheme.typography.labelSmall.copy(color = GoldGaboma))
        }
        Text(text = code.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, color = TextPrimary),
            modifier = Modifier.padding(16.dp).horizontalScroll(rememberScrollState())
        )
    }
}

@Composable
fun PdfPreviewPlaceholder(artifact: ArtifactItem) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("PDF".toString(), style = MaterialTheme.typography.labelSmall.copy(color = GoldGaboma))
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth().height(360.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(10) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (index % 3 == 0) 0.72f else 1f)
                            .height(if (index == 0) 22.dp else 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE6E6E6))
                    )
                }
            }
        }
        Text(artifact.footer.toString(), style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
    }
}

@Composable
fun SheetHandle() {
    Box(
        modifier = Modifier.padding(top = 10.dp, bottom = 8.dp).size(width = 42.dp, height = 4.dp).clip(RoundedCornerShape(8.dp)).background(Color(0x35FFFFFF))
    )
}

@Composable
fun LAntreDrawer(
    selectedModel: GabomaModel,
    onSelectModel: () -> Unit,
    onOpenSettings: () -> Unit,
    onNewPiste: () -> Unit
) {
    Surface(
        color = Surface1,
        modifier = Modifier.fillMaxHeight().width(318.dp),
        shadowElevation = 0.dp
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IbogaAiIcon(modifier = Modifier.size(26.dp), color = GoldGaboma)
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Gaboma AI".toString(), style = MaterialTheme.typography.titleLarge)
                    Text("The Equatorial Glass".toString(), style = MaterialTheme.typography.labelSmall.copy(color = GoldGaboma))
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(Color(0x18FFFFFF)).clickable(onClick = onNewPiste).padding(horizontal = 10.dp, vertical = 8.dp)
                ) { Text("Nouvelle Piste".toString(), style = MaterialTheme.typography.labelLarge, color = GoldGaboma) }
            }
            Spacer(Modifier.height(16.dp))
            GlassHairline()
            Spacer(Modifier.height(10.dp))
            DrawerMenuItem(icon = { EnPisteIcon(Modifier.size(16.dp), GoldGaboma) }, title = "En piste", subtitle = "Discussion active")
            DrawerMenuItem(icon = { ForetEveilleIcon(Modifier.size(16.dp), TurquoiseIA) }, title = "L'Antre", subtitle = "Bibliothèque & mémoire")
            DrawerMenuItem(icon = { SmartAndJTechIcon(Modifier.size(16.dp), GoldGaboma) }, title = "Le Rendu", subtitle = "Artifacts & documents")
            DrawerMenuItem(icon = { OnyxFaceIcon(Modifier.size(16.dp), OnyxPurple) }, title = "Énergie Quotidienne", subtitle = "Usage & puissance")
            DrawerMenuItem(icon = { LoxoIcon(Modifier.size(16.dp), LoxoGreen) }, title = "Extensions de Traque", subtitle = "Outils et web profond")
            DrawerMenuItem(icon = { JonathanDanielIcon(Modifier.size(16.dp), TextSecondary) }, title = "Administration", subtitle = "Nœuds, comptes, règles")
            Spacer(Modifier.height(8.dp))
            GlassHairline()
            Spacer(Modifier.height(8.dp))
            Surface(
                color = Surface2,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().clickable(onClick = onSelectModel).border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    ModelLuxuryIcon(selectedModel, Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(selectedModel.label.toString(), style = MaterialTheme.typography.labelLarge)
                        Text(selectedModel.subtitle.toString(), style = MaterialTheme.typography.labelSmall.copy(color = selectedModel.accent))
                    }
                    Text("Changer".toString(), style = MaterialTheme.typography.labelSmall.copy(color = GoldGaboma))
                }
            }
            Spacer(Modifier.weight(1f))
            GlassHairline()
            Spacer(Modifier.height(10.dp))
            Surface(
                color = Surface2,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenSettings).border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("NODE: LIBREVILLE-S-01".toString(), style = MaterialTheme.typography.labelSmall.copy(color = TurquoiseIA))
                    Text("BY ANDJ • SMARTANDJ TECH".toString(), style = MaterialTheme.typography.labelLarge)
                    Text("Gaboma-first · identité souveraine · interface premium".toString(), style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItem(icon: @Composable () -> Unit, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable { }.padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(26.dp), contentAlignment = Alignment.Center) { icon() }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title.toString(), style = MaterialTheme.typography.labelLarge)
            Text(subtitle.toString(), style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
        }
    }
}

@Composable
fun GlassHairline(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, Color(0x30FFFFFF), Color(0x16C9A84C), Color(0x30FFFFFF), Color.Transparent)
                )
            )
    )
}

@Composable
fun ModelLuxuryIcon(model: GabomaModel, modifier: Modifier = Modifier) {
    when (model) {
        GabomaModel.AURATA -> AurataIcon(modifier, GoldGaboma)
        GabomaModel.SONAR -> SonarIcon(modifier, SonarBlue)
        GabomaModel.LOXO -> LoxoIcon(modifier, LoxoGreen)
        GabomaModel.ONYX -> OnyxFaceIcon(modifier, OnyxPurple)
        GabomaModel.BLACK_PANTHER -> OnyxFaceIcon(modifier, PantherCrimson)
        GabomaModel.NKYEL -> ForetEveilleIcon(modifier, NkyelPearl)
    }
}

@Composable
fun StrokeIconBase(modifier: Modifier = Modifier, color: Color = TextPrimary, draw: androidx.compose.ui.graphics.drawscope.DrawScope.() -> Unit) {
    Canvas(modifier = modifier.requiredSize(15.dp)) { draw() }
}

@Composable
fun OnyxFaceIcon(modifier: Modifier = Modifier, color: Color = TextPrimary) = StrokeIconBase(modifier, color) {
    val s = size.minDimension / 15f
    fun p(x: Float, y: Float) = Offset(x * s, y * s)
    val path = Path().apply {
        moveTo(2.5f*s,5.5f*s); lineTo(2f*s,2.5f*s); lineTo(5f*s,4f*s)
        moveTo(12.5f*s,5.5f*s); lineTo(13f*s,2.5f*s); lineTo(10f*s,4f*s)
        moveTo(5f*s,4f*s); lineTo(10f*s,4f*s); lineTo(12.5f*s,6.5f*s); lineTo(11.5f*s,10.5f*s); lineTo(7.5f*s,13f*s); lineTo(3.5f*s,10.5f*s); lineTo(2.5f*s,6.5f*s); close()
        moveTo(4.5f*s,6.5f*s); lineTo(6f*s,7f*s)
        moveTo(10.5f*s,6.5f*s); lineTo(9f*s,7f*s)
        moveTo(6.5f*s,9f*s); lineTo(8.5f*s,9f*s); lineTo(7.5f*s,10f*s); close()
        moveTo(7.5f*s,10f*s); lineTo(7.5f*s,11f*s)
    }
    drawPath(path, color = color, style = Stroke(width = 1.2f * s, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

@Composable
fun SonarIcon(modifier: Modifier = Modifier, color: Color = TextPrimary) = StrokeIconBase(modifier, color) {
    val s = size.minDimension / 15f
    val path = Path().apply {
        moveTo(2f*s,11f*s); cubicTo(4f*s,7f*s,9f*s,2f*s,13f*s,4.5f*s)
        moveTo(13f*s,4.5f*s); cubicTo(13.5f*s,5f*s,12.5f*s,6.5f*s,11f*s,7.5f*s)
        moveTo(11f*s,7.5f*s); cubicTo(8f*s,9f*s,7.5f*s,7.5f*s,7f*s,8f*s)
        moveTo(7f*s,8f*s); cubicTo(5f*s,9.5f*s,3f*s,10.5f*s,2f*s,11f*s)
        moveTo(2f*s,11f*s); lineTo(1f*s,9.5f*s)
        moveTo(2f*s,11f*s); lineTo(1f*s,12.5f*s)
    }
    drawPath(path, color = color, style = Stroke(width = 1.2f * s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(color = color, radius = 0.5f*s, center = Offset(11.5f*s,5.5f*s))
}

@Composable
fun LoxoIcon(modifier: Modifier = Modifier, color: Color = TextPrimary) = StrokeIconBase(modifier, color) {
    val s = size.minDimension / 15f
    val path = Path().apply {
        moveTo(5f*s,4f*s); cubicTo(2f*s,4f*s,1f*s,6f*s,2f*s,9.5f*s)
        moveTo(2f*s,9.5f*s); cubicTo(2.5f*s,11f*s,4.5f*s,10.5f*s,5f*s,9f*s)
        moveTo(10f*s,4f*s); cubicTo(13f*s,4f*s,14f*s,6f*s,13f*s,9.5f*s)
        moveTo(13f*s,9.5f*s); cubicTo(12.5f*s,11f*s,10.5f*s,10.5f*s,10f*s,9f*s)
        moveTo(5f*s,4f*s); lineTo(10f*s,4f*s)
        moveTo(6.5f*s,9f*s); lineTo(6.5f*s,12f*s)
        moveTo(6.5f*s,12f*s); cubicTo(6.5f*s,13f*s,8.5f*s,13f*s,8.5f*s,12f*s)
        moveTo(8.5f*s,12f*s); lineTo(8.5f*s,9f*s)
        moveTo(5.5f*s,10f*s); lineTo(4.5f*s,11.5f*s)
        moveTo(9.5f*s,10f*s); lineTo(10.5f*s,11.5f*s)
    }
    drawPath(path, color = color, style = Stroke(width = 1.2f * s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(color, 0.45f*s, Offset(5.5f*s,6.5f*s))
    drawCircle(color, 0.45f*s, Offset(9.5f*s,6.5f*s))
}

@Composable
fun AurataIcon(modifier: Modifier = Modifier, color: Color = TextPrimary) = StrokeIconBase(modifier, color) {
    val s = size.minDimension / 15f
    val path = Path().apply {
        moveTo(9f*s,4f*s); lineTo(10f*s,2f*s); lineTo(11f*s,4f*s); lineTo(13f*s,5f*s); lineTo(11f*s,6f*s)
        moveTo(9f*s,4f*s); cubicTo(6f*s,5f*s,4f*s,6f*s,3f*s,8f*s)
        moveTo(3f*s,8f*s); cubicTo(1.5f*s,11f*s,3f*s,13f*s,4f*s,11f*s)
        moveTo(11f*s,6f*s); cubicTo(9f*s,10f*s,7f*s,13f*s,6f*s,13f*s)
        moveTo(6f*s,13f*s); lineTo(5f*s,9f*s)
    }
    drawPath(path, color = color, style = Stroke(width = 1.2f*s, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

@Composable
fun ModeGrisIcon(modifier: Modifier = Modifier, color: Color = TextPrimary) = StrokeIconBase(modifier, color) {
    val s = size.minDimension / 15f
    val path = Path().apply {
        moveTo(9f*s,2f*s); cubicTo(11f*s,2f*s,12f*s,3f*s,12f*s,4f*s)
        moveTo(12f*s,4f*s); cubicTo(13f*s,4f*s,13.5f*s,5f*s,12.5f*s,6f*s)
        moveTo(12.5f*s,6f*s); cubicTo(12f*s,6f*s,11f*s,5f*s,11f*s,4f*s)
        moveTo(9f*s,2f*s); cubicTo(7f*s,2f*s,6f*s,4f*s,6f*s,6f*s)
        moveTo(6f*s,6f*s); cubicTo(6f*s,9f*s,7f*s,10f*s,8f*s,10f*s)
        moveTo(8f*s,10f*s); lineTo(8f*s,13f*s)
        moveTo(11f*s,6f*s); cubicTo(11f*s,9f*s,10f*s,10f*s,8f*s,10f*s)
        moveTo(7f*s,6f*s); lineTo(9f*s,9f*s)
    }
    drawPath(path, color = color, style = Stroke(width = 1.2f*s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(color, 0.5f*s, Offset(10f*s,3.5f*s))
}

@Composable
fun EnPisteIcon(modifier: Modifier = Modifier, color: Color = TextPrimary) = StrokeIconBase(modifier, color) {
    val s = size.minDimension / 15f
    fun dot(x: Float, y: Float) = drawCircle(color, 0.6f*s, Offset(x*s, y*s))
    val path = Path().apply {
        moveTo(3f*s,10f*s); cubicTo(3f*s,9f*s,5f*s,9f*s,5f*s,10f*s)
        cubicTo(5f*s,11f*s,3f*s,11f*s,3f*s,10f*s)
        moveTo(9f*s,4f*s); cubicTo(9f*s,3f*s,11f*s,3f*s,11f*s,4f*s)
        cubicTo(11f*s,5f*s,9f*s,5f*s,9f*s,4f*s)
    }
    drawPath(path, color = color, style = Stroke(width = 1.2f*s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    dot(2.5f, 8.5f); dot(4f,7.5f); dot(5.5f,8.5f); dot(8.5f,2.5f); dot(10f,1.5f); dot(11.5f,2.5f)
}

@Composable
fun NkyelAiIcon(modifier: Modifier = Modifier, color: Color = TextPrimary) {
    androidx.compose.material3.Icon(
        painter = androidx.compose.ui.res.painterResource(id = com.smartandj.gabomagpt.R.drawable.ic_nkyel),
        contentDescription = "Ñkyel AI",
        tint = color,
        modifier = modifier
    )
}

@Composable
fun IbogaAiIcon(modifier: Modifier = Modifier, color: Color = TextPrimary) = NkyelAiIcon(modifier, color)

@Composable
fun ForetEveilleIcon(modifier: Modifier = Modifier, color: Color = TextPrimary) = StrokeIconBase(modifier, color) {
    val s = size.minDimension / 15f
    val path = Path().apply {
        moveTo(7.5f*s,1.5f*s); lineTo(2.5f*s,7.5f*s); lineTo(12.5f*s,7.5f*s); close()
        moveTo(7.5f*s,4.5f*s); lineTo(3.5f*s,10.5f*s); lineTo(11.5f*s,10.5f*s); close()
        moveTo(7.5f*s,10.5f*s); lineTo(7.5f*s,13.5f*s)
    }
    drawPath(path, color = color, style = Stroke(width = 1.2f*s, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

@Composable
fun AndjSovereignIcon(modifier: Modifier = Modifier, color: Color = TextPrimary) = StrokeIconBase(modifier, color) {
    val s = size.minDimension / 15f
    val path = Path().apply {
        moveTo(7.5f*s,2f*s); lineTo(2f*s,12f*s)
        moveTo(7.5f*s,2f*s); lineTo(13f*s,12f*s)
        moveTo(11f*s,3f*s); lineTo(11f*s,11.5f*s)
        cubicTo(11f*s,12.5f*s,8f*s,12.5f*s,8f*s,11.5f*s)
        moveTo(4.5f*s,7.5f*s); lineTo(11f*s,7.5f*s)
        moveTo(4.5f*s,7.5f*s); lineTo(4.5f*s,12f*s)
        moveTo(11f*s,7.5f*s); lineTo(11f*s,10f*s)
    }
    drawPath(path, color = color, style = Stroke(width = 1.2f*s, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

@Composable
fun JonathanDanielIcon(modifier: Modifier = Modifier, color: Color = TextPrimary) = StrokeIconBase(modifier, color) {
    val s = size.minDimension / 15f
    val path = Path().apply {
        moveTo(7.5f*s,2.5f*s); lineTo(12.5f*s,7.5f*s); lineTo(7.5f*s,12.5f*s); lineTo(2.5f*s,7.5f*s); close()
        moveTo(8f*s,2f*s); lineTo(8f*s,11f*s)
        cubicTo(8f*s,12.93f*s,6.43f*s,13f*s,4.5f*s,13f*s)
        moveTo(8f*s,4f*s); cubicTo(10f*s,4f*s,11f*s,6f*s,8f*s,10f*s)
    }
    drawPath(path, color = color, style = Stroke(width = 1.2f*s, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

@Composable
private fun SmartAndJTechIcon(modifier: Modifier = Modifier, color: Color = TextPrimary) = StrokeIconBase(modifier, color) {
    val s = size.minDimension / 15f
    val path = Path().apply {
        moveTo(9.5f*s,2.5f*s); lineTo(5.5f*s,6.5f*s); lineTo(9f*s,6.5f*s); lineTo(4.5f*s,11.5f*s)
        moveTo(1.5f*s,6f*s); lineTo(3.5f*s,6f*s)
        moveTo(1.5f*s,9f*s); lineTo(3.5f*s,9f*s)
        moveTo(11.5f*s,6f*s); lineTo(13.5f*s,6f*s)
        moveTo(11.5f*s,9f*s); lineTo(13.5f*s,9f*s)
    }
    drawPath(path, color = color, style = Stroke(width = 1.2f*s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(color, 1f*s, Offset(7.5f*s,7.5f*s))
}

fun demoMessages(): List<ChatMessage> = listOf(
    ChatMessage(
        id = "1",
        role = ChatRole.ASSISTANT,
        content = "Bienvenue dans Ñkyel AI. L'intelligence souveraine africaine de pointe. Chaque réponse est claire, élégante, structurée et ancrée dans des sources vérifiées.",
        modelDisplayName = GabomaModel.NKYEL.label,
        sources = demoSources(),
        artifact = demoArtifact(),
        isStreaming = false
    )
)

fun demoAiText(model: GabomaModel): String = when (model) {
    GabomaModel.AURATA -> "Ñkyel Chui a préparé une réponse concise, lumineuse et rapide, avec des points clés faciles à scanner et des sources visibles."
    GabomaModel.SONAR -> "Recherche Web déroule une synthèse ordonnée et ancrée dans les faits avec des citations vérifiées."
    GabomaModel.LOXO -> "Ñkyel Tai active la recherche documentaire et génère des livrables de haute précision dans Le Sanctuaire."
    GabomaModel.ONYX -> "OnyxGris répond avec une densité de raisonnement de pointe et une exécution multi-étapes orchestrée par DeerFlow."
    GabomaModel.BLACK_PANTHER -> "Blue Panther orchestre la mission comme un agent universel : synthèse, action, livrables exécutifs et souveraineté absolue."
    GabomaModel.NKYEL -> "Ñkyel AI privilégie une intelligence souveraine, avec une restitution noble, posée, et orientée gouvernance du savoir."
}

fun demoSources(): List<SourceRef> = listOf(
    SourceRef("Architecture Ñkyel AI", "SmartANDJ AI", "Gouvernance souveraine, modèles hybrides et persistance sécurisée Neon.", "Confiance élevée", confidence = 1.0f),
    SourceRef("Rendus & Artefacts Canoniques", "Sanctuaire", "Livrables exécutifs PDF, DOCX, PPTX et XLSX générés à la demande.", "Confiance élevée", confidence = 1.0f),
    SourceRef("Agent Autonome AG-UI", "Ñkyel Core", "Streaming d'événements SSE temps réel, raisonnement et citations vérifiées.", "Confiance élevée", confidence = 1.0f)
)

fun demoArtifact(): ArtifactItem = ArtifactItem(
    id = "demo_artifact",
    title = "Livrable Exécutif Ñkyel AI",
    type = ArtifactType.MARKDOWN,
    content = "# Le Sanctuaire · Rendus & Livrables\n\n- Résumé exécutif de mission\n- Décisions et points d'action\n- Sources vérifiées et citations\n- Exports natifs (DOCX, PPTX, PDF, XLSX)\n\nCette zone accueille les livrables d'intelligence souveraine générés par Ñkyel AI.",
    footer = "Généré par Ñkyel AI · SMARTANDJ AI TECHNOLOGIES"
)

/**
 * Ñkyel AI — Full Product Navigation Host
 * SmartANDJ AI Technologies · Founder: Daniel Jonathan ANDJ
 *
 * Auth-gated navigation matching the complete Next.js PWA structure.
 * Real Clerk profile + Neon credits & tier + Coil AsyncImage avatars.
 * No Ñkyel logo in sidebar (reserved for subtle chat badge adapted to theme).
 */
package com.smartandj.gabomagpt.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.smartandj.gabomagpt.presentation.auth.AuthScreen
import com.smartandj.gabomagpt.presentation.auth.AuthState
import com.smartandj.gabomagpt.presentation.auth.AuthViewModel
import com.smartandj.gabomagpt.presentation.chat.NkyelChatViewModel
import com.smartandj.gabomagpt.presentation.onboarding.OnboardingScreen
import com.smartandj.gabomagpt.presentation.onboarding.OnboardingViewModel
import com.smartandj.gabomagpt.presentation.onboarding.TermsOfServiceScreen
import com.smartandj.gabomagpt.presentation.onboarding.PrivacyPolicyScreen
import com.smartandj.gabomagpt.presentation.onboarding.AcceptableUsePolicyScreen
import com.smartandj.gabomagpt.presentation.screens.*
import com.smartandj.gabomagpt.presentation.theme.GabomaColors
import kotlinx.coroutines.launch

private data class NavItem(val route: String, val label: String, val icon: ImageVector)

private val sidebarItems = listOf(
    NavItem("home", "Nouvelle mission", Icons.Default.Add),
    NavItem("workspace", "Espace de travail", Icons.Default.GridView),
    NavItem("search", "Recherche", Icons.Default.Search),
    NavItem("studios", "Studios", Icons.Default.AutoAwesome),
    NavItem("projects", "Projets", Icons.Default.Folder),
    NavItem("connectors", "Connecteurs", Icons.Default.Extension),
    NavItem("programs", "Programmes", Icons.Default.Schedule),
    NavItem("sanctuary", "Sanctuary", Icons.Default.LibraryBooks),
)

private val bottomItems = listOf(
    NavItem("home", "Accueil", Icons.Default.Home),
    NavItem("workspace", "Workspace", Icons.Default.GridView),
    NavItem("studios", "Studios", Icons.Default.AutoAwesome),
    NavItem("sanctuary", "Sanctuary", Icons.Default.LibraryBooks),
)

@Composable
fun GabomaNavHost() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    when (val state = authState) {
        is AuthState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize().background(GabomaColors.BgBlackPanther),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GabomaColors.AccentBlackPanther)
            }
        }

        is AuthState.SignedOut -> AuthScreen(viewModel = authViewModel)

        is AuthState.SignedIn -> {
            val onboardingViewModel: OnboardingViewModel = hiltViewModel()
            val startDest = if (onboardingViewModel.isOnboardingComplete()) "home" else "onboarding"
            NkyelProductShell(
                startDest = startDest,
                userName = state.fullName ?: state.email?.substringBefore("@") ?: "Membre Ñkyel",
                userEmail = state.email,
                avatarUrl = state.avatarUrl,
                userId = state.userId,
                authViewModel = authViewModel,
            )
        }
    }
}

@Composable
private fun NkyelProductShell(
    startDest: String,
    userName: String,
    userEmail: String?,
    avatarUrl: String?,
    userId: String,
    authViewModel: AuthViewModel,
    chatViewModel: NkyelChatViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: startDest

    val conversations by chatViewModel.conversations.collectAsState()
    val currentConvId by chatViewModel.currentConversationId.collectAsState()
    val isStreaming by chatViewModel.isStreaming.collectAsState()
    val userProfile by authViewModel.userProfile.collectAsState()

    var missionSearchQuery by remember { mutableStateOf("") }

    val isOnboardingFlow = currentRoute in listOf("onboarding", "terms", "privacy", "acceptable-use")

    if (isOnboardingFlow) {
        // No shell during onboarding
        NavHost(navController = navController, startDestination = startDest) {
            composable("onboarding") {
                val onboardingViewModel: OnboardingViewModel = hiltViewModel()
                OnboardingScreen(
                    clerkFullName = userName,
                    onComplete = { navController.navigate("home") { popUpTo("onboarding") { inclusive = true } } },
                    onOpenTerms = { navController.navigate("terms") },
                    onOpenPrivacy = { navController.navigate("privacy") },
                )
            }
            composable("terms") { TermsOfServiceScreen(onBack = { navController.popBackStack() }) }
            composable("privacy") { PrivacyPolicyScreen(onBack = { navController.popBackStack() }) }
            composable("acceptable-use") { AcceptableUsePolicyScreen(onBack = { navController.popBackStack() }) }
        }
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(310.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
            ) {
                Column(Modifier.fillMaxSize()) {
                    // Top Drawer Header (NO Ñkyel logo here: "+ Nouvelle mission" action button first)
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                        Button(
                            onClick = {
                                chatViewModel.newChat()
                                scope.launch { drawerState.close() }
                                navController.navigate("home") {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GabomaColors.AccentBlackPanther,
                                contentColor = Color.Black,
                            ),
                        ) {
                            Icon(Icons.Default.Add, "Nouveau", Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Nouvelle mission", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }

                        Spacer(Modifier.height(12.dp))

                        // Quick search in missions
                        OutlinedTextField(
                            value = missionSearchQuery,
                            onValueChange = { missionSearchQuery = it },
                            placeholder = { Text("Rechercher...", fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, null, Modifier.size(16.dp)) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Main navigation links
                    LazyColumn(
                        modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                    ) {
                        items(sidebarItems) { item ->
                            NavigationDrawerItem(
                                label = { Text(item.label, fontSize = 14.sp) },
                                icon = { Icon(item.icon, item.label, Modifier.size(20.dp)) },
                                selected = currentRoute == item.route,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo("home") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(12.dp),
                            )
                        }

                        // Recent Missions Section
                        item {
                            Spacer(Modifier.height(10.dp))
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    "Missions récentes",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(Modifier.weight(1f))
                                if (conversations.isNotEmpty()) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                    ) {
                                        Text(
                                            "${conversations.size}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                        )
                                    }
                                }
                            }
                        }

                        val filteredConversations = conversations.filter {
                            it.title.contains(missionSearchQuery, ignoreCase = true)
                        }

                        if (filteredConversations.isEmpty()) {
                            item {
                                Text(
                                    "Aucune mission enregistrée",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                )
                            }
                        } else {
                            items(filteredConversations.take(12), key = { it.id }) { conv ->
                                val isSelected = conv.id == currentConvId
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 2.dp)
                                        .clickable {
                                            chatViewModel.selectConversation(conv.id)
                                            scope.launch { drawerState.close() }
                                            if (currentRoute != "chat") {
                                                navController.navigate("chat") { launchSingleTop = true }
                                            }
                                        },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) GabomaColors.AccentBlackPanther.copy(alpha = 0.15f) else Color.Transparent,
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected && isStreaming) Color(0xFF27856A)
                                                    else if (isSelected) GabomaColors.AccentBlackPanther
                                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                                )
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            conv.title.ifBlank { "Mission sans titre" },
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f),
                                        )
                                        IconButton(
                                            onClick = { chatViewModel.deleteConversation(conv.id) },
                                            modifier = Modifier.size(24.dp),
                                        ) {
                                            Icon(Icons.Default.Close, "Supprimer", Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Bottom Drawer Footer (Real Clerk Avatar + Name + Email + Credits + Settings)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch { drawerState.close() }
                                navController.navigate("settings") { launchSingleTop = true }
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (!avatarUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = "Avatar de $userName",
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, GabomaColors.AccentBlackPanther, CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(GabomaColors.AccentBlackPanther),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    userName.take(1).uppercase(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                )
                            }
                        }

                        Spacer(Modifier.width(10.dp))

                        Column(Modifier.weight(1f)) {
                            Text(
                                userName,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            userEmail?.let { email ->
                                Text(
                                    email,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            val credits = userProfile?.credits ?: 2480
                            Text(
                                "$credits crédits",
                                fontSize = 11.sp,
                                color = GabomaColors.AccentBlackPanther,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }

                        IconButton(
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate("settings") { launchSingleTop = true }
                            },
                            modifier = Modifier.size(32.dp),
                        ) {
                            Icon(Icons.Default.Settings, "Paramètres", Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                // TopBar with Menu hamburger button
                Surface(
                    tonalElevation = 1.dp,
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Ouvrir le menu")
                        }

                        val screenTitle = when (currentRoute) {
                            "home" -> "Ñkyel"
                            "chat" -> "Chat & Exécution"
                            "workspace" -> "Espace de travail"
                            "search" -> "Recherche"
                            "studios" -> "Studios"
                            "projects" -> "Projets"
                            "connectors" -> "Connecteurs"
                            "sanctuary" -> "Sanctuary"
                            "skills" -> "Compétences"
                            "settings" -> "Paramètres"
                            else -> "Ñkyel"
                        }

                        Text(
                            screenTitle,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f),
                        )

                        // Quick new chat button
                        IconButton(onClick = {
                            chatViewModel.newChat()
                            navController.navigate("home") {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                            }
                        }) {
                            Icon(Icons.Default.Add, "Nouvelle mission", tint = GabomaColors.AccentBlackPanther)
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                        )
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = startDest,
                modifier = Modifier.padding(padding),
            ) {
                composable("home") { NkyelHomeScreen(navController = navController, userName = userName, viewModel = chatViewModel) }
                composable("chat") { NkyelChatScreen(navController = navController, viewModel = chatViewModel) }
                composable("workspace") { NkyelWorkspaceScreen(navController = navController, viewModel = chatViewModel) }
                composable("search") { NkyelSearchScreen(navController = navController) }
                composable("studios") { NkyelStudiosScreen(navController = navController) }
                composable("projects") { NkyelProjectsScreen(navController = navController) }
                composable("connectors") { NkyelConnectorsScreen(navController = navController) }
                composable("sanctuary") { NkyelSanctuaryScreen(navController = navController) }
                composable("programs") { NkyelProgramsScreen(navController = navController) }
                composable("skills") { NkyelSkillsScreen(navController = navController) }
                composable("settings") {
                    NkyelSettingsScreen(
                        navController = navController,
                        userName = userName,
                        userEmail = userEmail,
                        avatarUrl = avatarUrl,
                        credits = userProfile?.credits ?: 2480,
                        tier = userProfile?.tier ?: "pro",
                        onSignOut = { authViewModel.signOut() },
                    )
                }
                composable("onboarding") {
                    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
                    OnboardingScreen(
                        clerkFullName = userName,
                        onComplete = { navController.navigate("home") { popUpTo("onboarding") { inclusive = true } } },
                        onOpenTerms = { navController.navigate("terms") },
                        onOpenPrivacy = { navController.navigate("privacy") },
                    )
                }
                composable("terms") { TermsOfServiceScreen(onBack = { navController.popBackStack() }) }
                composable("privacy") { PrivacyPolicyScreen(onBack = { navController.popBackStack() }) }
                composable("acceptable-use") { AcceptableUsePolicyScreen(onBack = { navController.popBackStack() }) }
            }
        }
    }
}

/**
 * Ñkyel AI — Navigation Destinations
 * SmartANDJ AI Technologies · Founder: Daniel Jonathan ANDJ
 *
 * Mirrors the Next.js PWA navigation structure exactly.
 */
package com.smartandj.gabomagpt.presentation.navigation

/** All Ñkyel product destinations */
sealed class NkyelRoute(val route: String, val label: String, val icon: String) {
    data object Home : NkyelRoute("home", "Accueil", "home")
    data object Chat : NkyelRoute("chat", "Chat", "chat")
    data object Workspace : NkyelRoute("workspace", "Espace de travail", "grid")
    data object Search : NkyelRoute("search", "Recherche", "search")
    data object Studios : NkyelRoute("studios", "Studios", "studio")
    data object Projects : NkyelRoute("projects", "Projets", "folder")
    data object Connectors : NkyelRoute("connectors", "Connecteurs", "plugin")
    data object Programs : NkyelRoute("programs", "Automatisations", "automation")
    data object Sanctuary : NkyelRoute("sanctuary", "Sanctuary", "library")
    data object Skills : NkyelRoute("skills", "Compétences", "skill")
    data object MyNkyel : NkyelRoute("my-nkyel", "Mon Ñkyel", "user")
    data object Settings : NkyelRoute("settings", "Paramètres", "settings")
    data object Onboarding : NkyelRoute("onboarding", "Bienvenue", "welcome")
    data object Terms : NkyelRoute("terms", "Conditions", "legal")
    data object Privacy : NkyelRoute("privacy", "Confidentialité", "shield")
    data object AcceptableUse : NkyelRoute("acceptable-use", "Utilisation", "legal")

    companion object {
        /** Sidebar navigation items (matching Web PWA) */
        val sidebarItems = listOf(Home, Workspace, Search, Studios, Projects, Connectors, Programs, Sanctuary)

        /** Bottom nav items for phone layout */
        val bottomNavItems = listOf(Home, Workspace, Studios, Sanctuary)
    }
}

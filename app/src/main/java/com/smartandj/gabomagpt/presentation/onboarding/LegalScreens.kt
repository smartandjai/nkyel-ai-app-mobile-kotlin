/* GabomaGPT · LegalScreens.kt · SmartANDJ AI Technologies
   Complete legal pages: Terms, Privacy Policy, Acceptable Use Policy
   Fondateur : Daniel Jonathan ANDJ */

package com.smartandj.gabomagpt.presentation.onboarding

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.presentation.theme.GabomaColors

// ═══════════════════════════════════════════════════════════════════════════
// TERMS OF SERVICE
// ═══════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceScreen(onBack: () -> Unit) {
    LegalPageShell(title = "Conditions Générales d'Utilisation", onBack = onBack) {
        LegalSection("1. Acceptation des Conditions") {
            "En accédant à Ñkyel AI ou en l'utilisant, vous acceptez d'être lié par les présentes Conditions Générales d'Utilisation (« CGU »). Si vous n'acceptez pas ces CGU, veuillez ne pas utiliser le Service. Ñkyel AI est édité par SmartANDJ AI Technologies, société fondée par Daniel Jonathan ANDJ."
        }
        LegalSection("2. Description du Service") {
            "Ñkyel AI est une plateforme d'intelligence artificielle conversationnelle et agentique souveraine offrant des fonctionnalités de chat, recherche, génération de documents (DOCX, PPTX, PDF), et exécution de missions autonomes. Le Service est disponible via application mobile (Android) et application web."
        }
        LegalSection("3. Inscription et Compte") {
            "Pour utiliser Ñkyel AI, vous devez créer un compte en fournissant des informations exactes. Vous êtes responsable de la sécurité de votre compte et de toutes les activités qui s'y déroulent. Vous devez avoir au moins 16 ans pour utiliser le Service."
        }
        LegalSection("4. Utilisation Acceptable") {
            "Vous vous engagez à utiliser Ñkyel AI de manière responsable et conforme aux lois applicables. Toute utilisation abusive, frauduleuse, ou contraire à notre Politique d'Utilisation Acceptable est interdite et peut entraîner la suspension ou la suppression de votre compte."
        }
        LegalSection("5. Propriété Intellectuelle") {
            "Le contenu que vous générez via Ñkyel AI vous appartient, sous réserve des droits préexistants des tiers. L'interface, le code source, les modèles d'IA, les marques, logos et le design de Ñkyel AI restent la propriété exclusive de SmartANDJ AI Technologies."
        }
        LegalSection("6. Abonnements et Paiements") {
            "Ñkyel AI propose des formules gratuites et payantes. Les paiements sont effectués via Mobile Money (Airtel Money, Moov Money), E-Billing, ou carte bancaire sur notre plateforme web. Les abonnements se renouvellent automatiquement sauf annulation."
        }
        LegalSection("7. Limitation de Responsabilité") {
            "Ñkyel AI est fourni « en l'état ». SmartANDJ AI Technologies ne garantit pas que les réponses générées par l'IA soient exemptes d'erreurs. Vous êtes responsable de vérifier les informations fournies. La responsabilité de SmartANDJ AI Technologies est limitée au montant payé pour le Service au cours des 12 derniers mois."
        }
        LegalSection("8. Résiliation") {
            "Vous pouvez supprimer votre compte à tout moment depuis les paramètres de l'application. SmartANDJ AI Technologies se réserve le droit de suspendre ou supprimer votre compte en cas de violation des CGU."
        }
        LegalSection("9. Modifications des CGU") {
            "SmartANDJ AI Technologies peut modifier ces CGU à tout moment. Les modifications significatives seront communiquées par notification dans l'application."
        }
        LegalSection("10. Droit Applicable") {
            "Les présentes CGU sont régies par le droit en vigueur. Tout litige sera soumis aux juridictions compétentes."
        }
        LegalSection("Contact") {
            "SmartANDJ AI Technologies\ncontact@nkyel.smartandjai.com"
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// PRIVACY POLICY
// ═══════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    LegalPageShell(title = "Politique de Confidentialité", onBack = onBack) {
        LegalSection("1. Responsable du Traitement") {
            "SmartANDJ AI Technologies est responsable du traitement de vos données personnelles dans le cadre de l'utilisation de Ñkyel AI."
        }
        LegalSection("2. Données Collectées") {
            "Nous collectons les données suivantes :\n• Informations d'identité : nom, adresse e-mail, photo de profil (via Clerk).\n• Données d'onboarding : nom d'affichage, préférences linguistiques.\n• Données d'utilisation : historique des conversations, modèles utilisés, fréquence d'utilisation.\n• Données techniques : adresse IP, type d'appareil, identifiants de session.\n• Données de paiement : traitées par nos prestataires sécurisés (E-Billing, opérateurs Mobile Money). Nous ne stockons jamais les numéros de carte bancaire."
        }
        LegalSection("3. Finalités du Traitement") {
            "Vos données sont utilisées pour :\n• Fournir et améliorer le Service Ñkyel AI.\n• Personnaliser votre expérience (langue, modèle, préférences).\n• Gérer votre compte et vos livrables.\n• Assurer la sécurité et prévenir les abus.\n• Si vous y consentez : entraîner et améliorer les modèles d'IA Ñkyel (données anonymisées)."
        }
        LegalSection("4. Base Légale du Traitement") {
            "• Exécution du contrat : pour fournir le Service.\n• Consentement : pour l'amélioration des modèles IA (opt-in uniquement, modifiable à tout moment dans les paramètres).\n• Intérêt légitime : pour la sécurité et la prévention des abus."
        }
        LegalSection("5. Partage des Données") {
            "Nous ne vendons jamais vos données personnelles. Nous pouvons partager des données avec nos sous-traitants techniques (hébergement cloud, authentification) dans le cadre strict de la fourniture du Service."
        }
        LegalSection("6. Conservation des Données") {
            "• Données de compte : conservées tant que votre compte est actif.\n• Historique de conversations : conservé tant que votre compte est actif. Vous pouvez supprimer des conversations individuellement à tout moment.\n• Données anonymisées pour l'entraînement IA : conservées indéfiniment sous forme anonymisée."
        }
        LegalSection("7. Vos Droits") {
            "Vous disposez d'un droit d'accès, de rectification, de suppression et de portabilité de vos données personnelles.\n\nPour exercer ces droits, contactez-nous à privacy@nkyel.smartandjai.com."
        }
        LegalSection("8. Sécurité") {
            "Nous mettons en œuvre des mesures techniques et organisationnelles appropriées pour protéger vos données : chiffrement en transit (TLS) et au repos, accès restreint aux données, audits de sécurité réguliers."
        }
        LegalSection("9. Transferts Internationaux") {
            "Certaines données peuvent être traitées via nos infrastructures cloud sécurisées avec un niveau de protection adéquat."
        }
        LegalSection("10. Modifications") {
            "Cette Politique peut être modifiée. Toute modification significative vous sera notifiée dans l'application."
        }
        LegalSection("Contact — Délégué à la Protection des Données") {
            "SmartANDJ AI Technologies\nprivacy@nkyel.smartandjai.com"
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// ACCEPTABLE USE POLICY
// ═══════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcceptableUsePolicyScreen(onBack: () -> Unit) {
    LegalPageShell(title = "Politique d'Utilisation Acceptable", onBack = onBack) {
        LegalSection("Introduction") {
            "Cette Politique d'Utilisation Acceptable définit les comportements attendus et interdits lors de l'utilisation de Ñkyel AI. Le non-respect de cette politique peut entraîner la suspension ou la suppression de votre compte."
        }
        LegalSection("Utilisations Interdites") {
            "Il est interdit d'utiliser Ñkyel AI pour :\n\n• Générer du contenu illégal, haineux, discriminatoire ou incitant à la violence.\n• Usurper l'identité d'une personne ou d'une organisation.\n• Harceler, menacer ou intimider d'autres personnes.\n• Générer de la désinformation ou des « fake news » dans l'intention de nuire.\n• Tenter de contourner les mesures de sécurité ou les filtres de contenu.\n• Utiliser le Service pour du spam, du phishing ou des activités frauduleuses.\n• Collecter ou stocker des données personnelles de tiers sans leur consentement.\n• Tenter d'extraire le code source des modèles d'IA souverains."
        }
        LegalSection("Contenu Généré") {
            "Vous êtes responsable de l'utilisation que vous faites du contenu généré par Ñkyel AI. Nous vous rappelons que :\n• Les réponses de l'IA peuvent contenir des inexactitudes — vérifiez toujours les informations critiques.\n• Le contenu généré ne constitue pas un conseil juridique, médical ou financier."
        }
        LegalSection("Signalement") {
            "Si vous constatez une utilisation abusive de Ñkyel AI ou un contenu inapproprié, merci de le signaler à abuse@nkyel.smartandjai.com."
        }
        LegalSection("Sanctions") {
            "En cas de violation de cette politique :\n• Premier avertissement : notification par e-mail.\n• Récidive : suspension temporaire du compte.\n• Violation grave : suppression définitive du compte."
        }
        LegalSection("Contact") {
            "SmartANDJ AI Technologies\nabuse@nkyel.smartandjai.com"
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// SHARED LEGAL SHELL
// ═══════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LegalPageShell(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val bg = GabomaColors.BgBlackPanther
    val textPrimary = GabomaColors.TextPrimary
    val textSecondary = GabomaColors.TextSecondary
    val accent = GabomaColors.AccentBlackPanther
    val border = GabomaColors.Divider

    Scaffold(
        containerColor = bg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary,
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = rememberVectorPainter(Icons.Filled.ArrowBack),
                            contentDescription = "Retour",
                            tint = textSecondary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bg),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            content = content,
        )
    }
}

@Composable
private fun LegalSection(title: String, content: () -> String) {
    val textPrimary = GabomaColors.TextPrimary
    val textSecondary = GabomaColors.TextSecondary
    val surface = GabomaColors.SurfaceBlackPanther
    val border = GabomaColors.Divider

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        color = surface,
        border = BorderStroke(1.dp, border),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary,
                ),
            )
            Text(
                text = content(),
                style = TextStyle(
                    fontSize = 13.sp,
                    color = textSecondary,
                    lineHeight = 20.sp,
                ),
            )
        }
    }
}

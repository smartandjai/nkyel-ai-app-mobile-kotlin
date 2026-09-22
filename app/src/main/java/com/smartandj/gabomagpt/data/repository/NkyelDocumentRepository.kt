package com.smartandj.gabomagpt.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

data class PdfPageBitmap(val page: Int, val bitmap: ImageBitmap, val width: Int, val height: Int)

sealed class ExportResult {
    data class Success(val uri: Uri, val fileName: String) : ExportResult()
    data class Error(val message: String) : ExportResult()
}

@Singleton
class NkyelDocumentRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) {

    // ── Rendu PDF natif via PdfRenderer ────────────────────────
    fun renderPdf(uri: Uri): Flow<PdfPageBitmap> = flow {
        val parcelFd: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(uri, "r")
        requireNotNull(parcelFd) { "Impossible d'ouvrir le fichier PDF" }

        val renderer = PdfRenderer(parcelFd)
        val displayWidth = context.resources.displayMetrics.widthPixels

        try {
            for (i in 0 until renderer.pageCount) {
                val page = renderer.openPage(i)
                val scale = displayWidth.toFloat() / page.width.toFloat()
                val bitmapWidth = displayWidth
                val bitmapHeight = (page.height * scale).toInt()

                val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
                bitmap.eraseColor(Color.WHITE)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()

                emit(PdfPageBitmap(page = i + 1, bitmap = bitmap.asImageBitmap(), width = bitmapWidth, height = bitmapHeight))
            }
        } finally {
            renderer.close()
            parcelFd.close()
        }
    }.flowOn(Dispatchers.IO)

    // ── Export Artifact → PDF natif Android ─────────────────────
    suspend fun exportToPdf(
        content: String,
        title: String
    ): ExportResult = withContext(Dispatchers.IO) {
        try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint().apply {
                color = Color.BLACK
                textSize = 14f
                isAntiAlias = true
            }
            val titlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 22f
                isFakeBoldText = true
                isAntiAlias = true
            }

            // Titre
            canvas.drawText(title, 40f, 60f, titlePaint)

            // Séparateur
            val linePaint = Paint().apply { color = Color.parseColor("#C9A84C"); strokeWidth = 1.5f }
            canvas.drawLine(40f, 72f, 555f, 72f, linePaint)

            // Corps du texte (wrap automatique)
            var yPos = 100f
            val maxWidth = 515f
            val words = content.split(" ")
            val line = StringBuilder()
            for (word in words) {
                val testLine = if (line.isEmpty()) word else "$line $word"
                if (paint.measureText(testLine) > maxWidth) {
                    canvas.drawText(line.toString(), 40f, yPos, paint)
                    yPos += 22f
                    line.clear()
                    line.append(word)
                    if (yPos > 800f) break // simple guard contre overflow
                } else {
                    line.clear(); line.append(testLine)
                }
            }
            if (line.isNotEmpty()) canvas.drawText(line.toString(), 40f, yPos, paint)

            // Footer signature
            val footerPaint = Paint().apply { color = Color.parseColor("#8E8A80"); textSize = 10f }
            canvas.drawText("Ñkyel AI · BY ANDJ · SMARTANDJ TECH", 40f, 820f, footerPaint)

            document.finishPage(page)

            val fileName = "${title.replace(" ", "_")}_nkyel.pdf"
            val file = File(context.cacheDir, fileName)
            document.writeTo(FileOutputStream(file))
            document.close()

            val contentUri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            ExportResult.Success(contentUri, fileName)
        } catch (e: Exception) {
            ExportResult.Error(e.message ?: "Erreur export PDF")
        }
    }

    // ── Export Artifact → TXT ────────────────────────────────────
    suspend fun exportToTxt(content: String, title: String): ExportResult =
        withContext(Dispatchers.IO) {
            try {
                val fileName = "${title.replace(" ", "_")}_nkyel.txt"
                val file = File(context.cacheDir, fileName)
                file.writeText(
                    buildString {
                        appendLine("# $title")
                        appendLine("Ñkyel AI · SMARTANDJ AI TECHNOLOGIES")
                        appendLine("─".repeat(48))
                        appendLine()
                        append(content)
                    }
                )
                val contentUri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                ExportResult.Success(contentUri, fileName)
            } catch (e: Exception) {
                ExportResult.Error(e.message ?: "Erreur export TXT")
            }
        }

    // ── Téléchargement & Ouverture de Livrable Natif (DOCX, PPTX, PDF, XLSX) ──
    suspend fun downloadAndOpenArtifact(
        artifactId: String,
        title: String,
        type: String,
        downloadUrl: String? = null
    ): ExportResult = withContext(Dispatchers.IO) {
        try {
            val extension = when (type.lowercase()) {
                "docx", "word" -> "docx"
                "pptx", "presentation", "slides" -> "pptx"
                "xlsx", "excel", "spreadsheet" -> "xlsx"
                "pdf" -> "pdf"
                else -> type.lowercase()
            }

            val mimeType = when (extension) {
                "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
                "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                "pdf" -> "application/pdf"
                else -> "application/octet-stream"
            }

            val targetUrl = downloadUrl?.takeIf { it.startsWith("http") }
                ?: "${com.smartandj.gabomagpt.data.remote.NkyelNetworkConfig.ARTIFACTS_URL}/$artifactId/export?format=$extension"

            val fileName = "${title.replace(" ", "_").take(40)}_nkyel.$extension"
            val file = File(context.cacheDir, fileName)

            val requestBuilder = okhttp3.Request.Builder().url(targetUrl)
            try {
                val token = com.smartandj.gabomagpt.data.remote.ClerkTokenProvider.getValidSessionToken()
                if (!token.isNullOrBlank()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
            } catch (_: Exception) {}

            val response = okHttpClient.newCall(requestBuilder.build()).execute()
            if (!response.isSuccessful) {
                return@withContext ExportResult.Error("Échec téléchargement : code HTTP ${response.code}")
            }

            response.body?.byteStream()?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            val contentUri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            // Ouvrir avec l'application native installée (Word, PowerPoint, PDF Viewer, etc.)
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, mimeType)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)

            ExportResult.Success(contentUri, fileName)
        } catch (e: Exception) {
            ExportResult.Error("Impossible d'ouvrir le document : ${e.message}")
        }
    }

    // ── Lecture d'un document RAG (texte brut depuis fichier) ────
    suspend fun extractTextFromFile(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText() ?: ""
        } catch (e: Exception) {
            "Erreur de lecture du fichier : ${e.message}"
        }
    }

    // ── Nettoyage du cache ───────────────────────────────────────
    suspend fun clearExportCache() = withContext(Dispatchers.IO) {
        context.cacheDir.listFiles()?.forEach { file ->
            if (file.name.contains("_nkyel.") || file.name.contains("_gaboma.")) {
                file.delete()
            }
        }
    }
}

typealias GabomaDocumentRepository = NkyelDocumentRepository

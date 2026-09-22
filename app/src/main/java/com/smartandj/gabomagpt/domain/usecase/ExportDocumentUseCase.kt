package com.smartandj.gabomagpt.domain.usecase

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ExportDocumentUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun exportToPdf(title: String, content: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = pdfDocument.startPage(pageInfo)
            
            val canvas: Canvas = page.canvas
            val paint = Paint()
            paint.color = Color.BLACK
            paint.textSize = 14f

            var yPos = 50f
            content.lines().forEach { line ->
                if (yPos > 800f) {
                    // Missing real pagination for simplicity
                }
                canvas.drawText(line, 50f, yPos, paint)
                yPos += 20f
            }

            pdfDocument.finishPage(page)

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "${title.replace(" ", "_")}.pdf")
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exportToDocx(title: String, content: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            val document = XWPFDocument()
            val paragraph = document.createParagraph()
            val run = paragraph.createRun()
            
            content.lines().forEach { line ->
                run.setText(line)
                run.addBreak()
            }

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "${title.replace(" ", "_")}.docx")
            
            FileOutputStream(file).use { out ->
                document.write(out)
            }
            document.close()
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

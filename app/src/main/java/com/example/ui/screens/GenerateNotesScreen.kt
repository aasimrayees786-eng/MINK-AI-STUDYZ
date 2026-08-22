package com.example.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SampleChaptersData
import com.example.ui.ChapterAIViewModel
import com.example.ui.theme.AudioWaveActive
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.HighDensityAccentDeep
import com.example.ui.theme.HighDensityAccentLight
import com.example.ui.theme.HighDensityBorder
import com.example.ui.theme.HighDensityDarkContainer
import com.example.ui.theme.HighDensityOnPrimaryContainer
import com.example.ui.theme.HighDensityOnSurfaceVariant
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.HighDensityPrimaryContainer
import com.example.ui.theme.HighDensityProgressTrack
import com.example.ui.theme.HighDensitySecondaryContainer
import com.example.ui.theme.HighDensitySurfaceVariant
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealSecondaryDark

private val SUBJECT_LIST = listOf(
    "Biology", "Physics", "Chemistry", "Computer Science",
    "World History", "Mathematics", "Economics", "Literature"
)

private val DEPTH_LEVELS = listOf("Quick Summary", "Comprehensive", "Exam Deep Dive")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenerateNotesScreen(
    viewModel: ChapterAIViewModel,
    onOpenVoiceSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val chapterText by viewModel.chapterInputText.collectAsState()
    val subject by viewModel.chapterSubject.collectAsState()
    val depth by viewModel.noteDepth.collectAsState()
    val isGenerating by viewModel.isGeneratingNote.collectAsState()
    val progressMessage by viewModel.generationProgressMessage.collectAsState()
    val errorMsg by viewModel.errorMessage.collectAsState()
    val currentLang by viewModel.voiceManager.currentLanguage.collectAsState()
    val currentPersona by viewModel.voiceManager.currentPersona.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val activeNote by viewModel.activeNote.collectAsState()

    val uploadedFileName by viewModel.uploadedDocumentName.collectAsState()
    val uploadedFileSize by viewModel.uploadedDocumentSize.collectAsState()

    val wordCount = if (chapterText.isBlank()) 0 else chapterText.split("\\s+".toRegex()).filter { it.isNotBlank() }.size

    // File Picker for PDF / Document Upload
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                var fileName = "Uploaded_Chapter.pdf"
                var fileSize = "1.2 MB"
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use { c ->
                    if (c.moveToFirst()) {
                        val nameIdx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeIdx = c.getColumnIndex(OpenableColumns.SIZE)
                        if (nameIdx != -1) {
                            val n = c.getString(nameIdx)
                            if (!n.isNullOrBlank()) fileName = n
                        }
                        if (sizeIdx != -1) {
                            val bytes = c.getLong(sizeIdx)
                            fileSize = if (bytes > 1024 * 1024) "${bytes / (1024 * 1024)} MB" else "${bytes / 1024} KB"
                        }
                    }
                }

                // Detect subject from file name if possible
                val lowerName = fileName.lowercase()
                when {
                    lowerName.contains("physic") -> viewModel.chapterSubject.value = "Physics"
                    lowerName.contains("chem") -> viewModel.chapterSubject.value = "Chemistry"
                    lowerName.contains("math") || lowerName.contains("calc") || lowerName.contains("algebra") -> viewModel.chapterSubject.value = "Mathematics"
                    lowerName.contains("bio") || lowerName.contains("cell") || lowerName.contains("gene") -> viewModel.chapterSubject.value = "Biology"
                    lowerName.contains("hist") -> viewModel.chapterSubject.value = "World History"
                    lowerName.contains("code") || lowerName.contains("cs") || lowerName.contains("comp") -> viewModel.chapterSubject.value = "Computer Science"
                }

                // Try reading text content if accessible text stream
                var extractedText = ""
                try {
                    val stream = context.contentResolver.openInputStream(uri)
                    extractedText = stream?.bufferedReader()?.use { it.readText() } ?: ""
                } catch (e: Exception) {
                    extractedText = ""
                }

                val cleanText = if (extractedText.isNotBlank() && extractedText.length > 50 && !extractedText.contains("%PDF")) {
                    extractedText.take(8000)
                } else {
                    // Clean chapter document reference for Gemini
                    "Document: $fileName\nSize: $fileSize\nSubject: ${viewModel.chapterSubject.value}\n\nChapter Document uploaded for AI Notes Maker. Extract Short Notes, Key Points, Definitions, and Important Formulas."
                }

                val mimeType = context.contentResolver.getType(uri) ?: "application/pdf"
                viewModel.attachDocument(fileName, fileSize, mimeType, cleanText)
            } catch (e: Exception) {
                viewModel.attachDocument("Uploaded_Chapter.pdf", "PDF", "application/pdf", "")
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // AI Notes Maker Hero Header Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = HighDensityPrimaryContainer),
            border = BorderStroke(1.dp, HighDensityOnPrimaryContainer.copy(alpha = 0.15f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("generate_hero_banner")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MINK STUDY AI",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = HighDensityOnPrimaryContainer.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = HighDensityOnPrimaryContainer
                    ) {
                        Text(
                            text = "AI NOTES MAKER",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Upload a Chapter / PDF",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = HighDensityOnPrimaryContainer,
                    fontSize = 22.sp,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Instantly synthesize Short Notes, Key Points, Definitions & Important Formulas with AI.",
                    style = MaterialTheme.typography.bodySmall,
                    color = HighDensityOnPrimaryContainer.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Generated Output Capabilities Badges
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "📝 Short Notes",
                        "🎯 Key Points",
                        "📖 Definitions",
                        "📐 Important Formulas"
                    ).forEach { badge ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, HighDensityOnPrimaryContainer.copy(alpha = 0.15f))
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = HighDensityOnPrimaryContainer,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Upload PDF / Document Interactive Section
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (uploadedFileName != null) TealSecondaryDark.copy(alpha = 0.08f) else HighDensitySurfaceVariant
            ),
            border = BorderStroke(
                1.5.dp,
                if (uploadedFileName != null) TealSecondaryDark else HighDensityBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("upload_chapter_pdf_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (uploadedFileName == null) {
                    // Not yet uploaded: Upload Dropzone UI
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(HighDensityPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.UploadFile,
                                contentDescription = "Upload Document",
                                tint = HighDensityPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Upload Chapter / PDF Document",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Select any textbook chapter, notes, syllabus, or PDF",
                                style = MaterialTheme.typography.bodySmall,
                                color = HighDensityOnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { documentPickerLauncher.launch("*/*") },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HighDensityPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("choose_pdf_button")
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Browse & Upload PDF / File", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else {
                    // Uploaded File Status View
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(TealSecondaryDark.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PictureAsPdf,
                                    contentDescription = "PDF",
                                    tint = TealSecondaryDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = uploadedFileName ?: "Document.pdf",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Attached",
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "Attached • ${uploadedFileSize ?: "1.2 MB"} • Ready for AI Notes Maker",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TealSecondaryDark,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { documentPickerLauncher.launch("*/*") },
                                modifier = Modifier.size(32.dp).testTag("replace_uploaded_doc")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Replace", tint = TealSecondaryDark, modifier = Modifier.size(20.dp))
                            }

                            IconButton(
                                onClick = { viewModel.clearUploadedDocument() },
                                modifier = Modifier.size(32.dp).testTag("remove_uploaded_doc")
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = "Remove", tint = ErrorRed, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }

        // Quick Sample Chapters (1-tap testing)
        Column {
            Text(
                text = "⚡ Or Select a Sample Chapter",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SampleChaptersData.PRESETS) { preset ->
                    val isSelected = subject == preset.subject && chapterText == preset.excerpt
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) HighDensitySecondaryContainer else HighDensitySurfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) HighDensityPrimary else HighDensityBorder),
                        modifier = Modifier
                            .clickable { viewModel.loadSamplePreset(preset) }
                            .testTag("sample_preset_${preset.subject}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = preset.icon, fontSize = 16.sp)
                            Column {
                                Text(
                                    text = preset.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = preset.subject,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HighDensityOnSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Subject Selector Chips
        Column {
            Text(
                text = "Subject Domain",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SUBJECT_LIST.forEach { s ->
                    val isSelected = s.equals(subject, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) HighDensityPrimary else HighDensitySurfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) HighDensityPrimary else HighDensityBorder),
                        modifier = Modifier
                            .clickable { viewModel.chapterSubject.value = s }
                            .testTag("subject_chip_$s")
                    ) {
                        Text(
                            text = s,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Note Depth Selector
        Column {
            Text(
                text = "Summary & Note Depth",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DEPTH_LEVELS.forEach { d ->
                    val isSelected = d == depth
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) HighDensitySecondaryContainer else HighDensitySurfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) HighDensityPrimary else HighDensityBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.noteDepth.value = d }
                            .testTag("depth_button_$d")
                    ) {
                        Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = d,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) HighDensityOnPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Chapter Input TextField
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chapter Text Content / Excerpt",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
                if (chapterText.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            viewModel.chapterInputText.value = ""
                            viewModel.clearUploadedDocument()
                        },
                        modifier = Modifier.size(24.dp).testTag("clear_chapter_input")
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = chapterText,
                onValueChange = { viewModel.chapterInputText.value = it },
                placeholder = {
                    Text(
                        "Upload a PDF or paste chapter excerpt to generate Short Notes, Key Points, Definitions, and Important Formulas...",
                        color = HighDensityOnSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .testTag("chapter_input_field"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HighDensityPrimary,
                    unfocusedBorderColor = HighDensityBorder,
                    focusedContainerColor = HighDensitySurfaceVariant.copy(alpha = 0.6f),
                    unfocusedContainerColor = HighDensitySurfaceVariant.copy(alpha = 0.3f)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${chapterText.length} chars • $wordCount words",
                    style = MaterialTheme.typography.labelSmall,
                    color = HighDensityOnSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }

        // Error message if any
        AnimatedVisibility(visible = errorMsg != null) {
            errorMsg?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = ErrorRed.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, ErrorRed),
                    modifier = Modifier.fillMaxWidth().testTag("generation_error_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                        Text(msg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        // Multi-stage Loading Indicator
        AnimatedVisibility(visible = isGenerating) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = HighDensityPrimaryContainer),
                modifier = Modifier.fillMaxWidth().testTag("generating_progress_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = HighDensityPrimary,
                            strokeWidth = 2.5.dp
                        )
                        Column {
                            Text(
                                text = "Generating AI Notes...",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = HighDensityOnPrimaryContainer
                            )
                            Text(
                                text = progressMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = HighDensityOnPrimaryContainer.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                        color = HighDensityPrimary,
                        trackColor = HighDensityProgressTrack
                    )
                }
            }
        }

        // Action Button: Generate Notes
        Button(
            onClick = { viewModel.generateNotes() },
            enabled = !isGenerating && chapterText.isNotBlank(),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HighDensityPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_notes_button")
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isGenerating) "Synthesizing Notes & Formulas..." else "✨ Generate AI Notes (Notes, Points, Definitions, Formulas)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChapterNote
import com.example.data.model.ConceptItem
import com.example.data.model.DefinitionItem
import com.example.data.model.FormulaItem
import com.example.data.model.ImprovementGuideItem
import com.example.data.model.VocabItem
import com.example.ui.ChapterAIViewModel
import com.example.ui.NoteDetailTab
import com.example.ui.components.AudioLessonBar
import com.example.ui.components.FlashcardViewer
import com.example.ui.components.QuizAssessmentCard
import com.example.ui.theme.AmberTertiaryDark
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.HighlightGold
import com.example.ui.theme.IndigoPrimaryDark
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealSecondaryDark

@Composable
fun NoteDetailScreen(
    viewModel: ChapterAIViewModel,
    note: ChapterNote,
    onBack: () -> Unit,
    onOpenVoiceSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedDetailTab.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val amplitudes by viewModel.voiceManager.visualizerAmplitudes.collectAsState()
    val currentLang by viewModel.voiceManager.currentLanguage.collectAsState()
    val currentPersona by viewModel.voiceManager.currentPersona.collectAsState()

    // Flashcard & Quiz states
    val flashcards = viewModel.parseFlashcards(note.flashcardsJson)
    val flashcardIndex by viewModel.currentFlashcardIndex.collectAsState()
    val isFlashcardFlipped by viewModel.isFlashcardFlipped.collectAsState()

    val quizQuestions = viewModel.parseQuiz(note.quizJson)
    val quizAnswers by viewModel.currentQuizAnswers.collectAsState()
    val isQuizSubmitted by viewModel.isQuizSubmitted.collectAsState()
    val quizScoreResult by viewModel.quizScoreResult.collectAsState()

    val concepts = viewModel.parseConcepts(note.conceptsJson)
    val takeaways = viewModel.parseTakeaways(note.keyTakeawaysJson)
    val vocabulary = viewModel.parseVocabulary(note.vocabularyJson)
    val formulas = viewModel.parseFormulas(note.importantFormulasJson)
    val definitions = viewModel.parseDefinitions(note.vocabularyJson)
    val improvementGuide = viewModel.parseImprovementGuide(note.improvementGuideJson)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("detail_back_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = note.subject.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 11.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.toggleFavorite(note) },
                    modifier = Modifier.testTag("detail_favorite_button")
                ) {
                    Icon(
                        imageVector = if (note.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (note.isFavorite) HighlightGold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { viewModel.deleteNote(note) },
                    modifier = Modifier.testTag("detail_delete_button")
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Note",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // Note Title & Source File Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
                if (note.sourceFileName.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = "📄 ${note.sourceFileName}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Persistent Spoken Audio Lesson Bar
        AudioLessonBar(
            isSpeaking = isSpeaking,
            amplitudes = amplitudes,
            currentLanguage = currentLang,
            currentPersona = currentPersona,
            onPlayPause = {
                if (isSpeaking) {
                    viewModel.stopSpeaking()
                } else {
                    val textToSpeak = when (selectedTab) {
                        NoteDetailTab.SHORT_NOTES -> "${note.title}. ${note.summary}"
                        NoteDetailTab.KEY_POINTS -> "Key points for ${note.title}: ${takeaways.joinToString(". ")}"
                        NoteDetailTab.DEFINITIONS -> vocabulary.joinToString(". ") { "${it.term}: ${it.definition}" }
                        NoteDetailTab.FORMULAS -> formulas.joinToString(". ") { "${it.name}: equation ${it.formula}. ${it.explanation}" }
                        NoteDetailTab.IMPROVEMENT -> "Student improvement roadmap for ${note.title}. Common weak spots: ${improvementGuide.commonWeakSpots.joinToString(", ")}. Strategy: ${improvementGuide.revisionStrategy.joinToString(", ")}"
                        else -> note.summary
                    }
                    viewModel.speakText(textToSpeak)
                }
            },
            onStop = { viewModel.stopSpeaking() },
            onOpenVoiceSettings = onOpenVoiceSettings,
            titleText = "Listen in ${currentLang.displayName}",
            subtitleText = "Spoken by ${currentPersona.name}"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Scrollable Tabs for All 4 Core Study Components + Flashcards & Quiz
        ScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            edgePadding = 0.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.fillMaxWidth().testTag("detail_tab_row")
        ) {
            NoteDetailTab.values().forEach { tab ->
                val countBadge = when (tab) {
                    NoteDetailTab.KEY_POINTS -> if (takeaways.isNotEmpty()) " (${takeaways.size})" else ""
                    NoteDetailTab.DEFINITIONS -> if (vocabulary.isNotEmpty()) " (${vocabulary.size})" else ""
                    NoteDetailTab.FORMULAS -> if (formulas.isNotEmpty()) " (${formulas.size})" else ""
                    NoteDetailTab.FLASHCARDS -> if (flashcards.isNotEmpty()) " (${flashcards.size})" else ""
                    NoteDetailTab.QUIZ -> if (quizQuestions.isNotEmpty()) " (${quizQuestions.size})" else ""
                    else -> ""
                }

                Tab(
                    selected = selectedTab == tab,
                    onClick = { viewModel.selectDetailTab(tab) },
                    text = {
                        Text(
                            text = "${tab.label}$countBadge",
                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.testTag("detail_tab_${tab.name}")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Active Tab Content
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            when (selectedTab) {
                NoteDetailTab.SHORT_NOTES -> {
                    ShortNotesTabContent(
                        note = note,
                        takeaways = takeaways,
                        concepts = concepts,
                        onSpeakSummary = { viewModel.speakText("${note.title}. ${note.summary}") }
                    )
                }
                NoteDetailTab.KEY_POINTS -> {
                    KeyPointsTabContent(
                        takeaways = takeaways,
                        onSpeakKeyPoints = {
                            val text = "Key Points for ${note.title}: " + takeaways.joinToString(". ")
                            viewModel.speakText(text)
                        }
                    )
                }
                NoteDetailTab.DEFINITIONS -> {
                    DefinitionsTabContent(
                        definitions = if (definitions.isNotEmpty()) definitions else vocabulary.map { DefinitionItem(it.term, it.definition, it.formulaOrExample) },
                        onSpeakDefinition = { item ->
                            viewModel.speakText("${item.term}: ${item.definition}. Context: ${item.contextOrExample}")
                        }
                    )
                }
                NoteDetailTab.FORMULAS -> {
                    ImportantFormulasTabContent(
                        formulas = formulas,
                        onSpeakFormula = { item ->
                            viewModel.speakText("${item.name}. Formula: ${item.formula}. Explanation: ${item.explanation}. Example: ${item.example}")
                        }
                    )
                }
                NoteDetailTab.FLASHCARDS -> {
                    Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        FlashcardViewer(
                            flashcards = flashcards,
                            currentIndex = flashcardIndex,
                            isFlipped = isFlashcardFlipped,
                            onFlip = { viewModel.flipFlashcard() },
                            onNext = { viewModel.nextFlashcard(flashcards.size) },
                            onPrev = { viewModel.prevFlashcard(flashcards.size) },
                            onSpeakCard = { viewModel.speakText(it) },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
                NoteDetailTab.QUIZ -> {
                    Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        QuizAssessmentCard(
                            quizQuestions = quizQuestions,
                            selectedAnswers = quizAnswers,
                            isSubmitted = isQuizSubmitted,
                            scoreResult = quizScoreResult,
                            onSelectOption = { qIdx, optIdx -> viewModel.selectQuizOption(qIdx, optIdx) },
                            onSubmitQuiz = { viewModel.submitQuiz(quizQuestions, note.id) },
                            onResetQuiz = { viewModel.resetQuizState() },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
                NoteDetailTab.IMPROVEMENT -> {
                    ImprovementGuideTabContent(
                        guide = improvementGuide,
                        onSpeakGuide = {
                            val text = "Improvement Guide. Common mistakes to avoid: ${improvementGuide.commonWeakSpots.joinToString(". ")}. Memory Tricks: ${improvementGuide.mnemonicTricks.joinToString(". ")}. Exam advice: ${improvementGuide.examReadinessTips}"
                            viewModel.speakText(text)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShortNotesTabContent(
    note: ChapterNote,
    takeaways: List<String>,
    concepts: List<ConceptItem>,
    onSpeakSummary: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Executive Summary Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth().testTag("short_notes_card")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Executive Short Notes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onSpeakSummary,
                        modifier = Modifier.size(32.dp).testTag("speak_short_notes_button")
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = note.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp
                )
            }
        }

        // Key Concepts Breakdown
        if (concepts.isNotEmpty()) {
            Text(
                text = "💡 Core Concepts & Mechanisms",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            concepts.forEach { concept ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = concept.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = concept.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )
                        if (concept.keyPoints.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            concept.keyPoints.forEach { pt ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("•", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = pt,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun KeyPointsTabContent(
    takeaways: List<String>,
    onSpeakKeyPoints: () -> Unit
) {
    if (takeaways.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No key points available.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth().testTag("key_points_card")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.FormatListNumbered,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "High-Yield Key Points (${takeaways.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onSpeakKeyPoints,
                        modifier = Modifier.size(32.dp).testTag("speak_key_points_button")
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                takeaways.forEachIndexed { idx, point ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp).padding(top = 2.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${idx + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = point,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DefinitionsTabContent(
    definitions: List<DefinitionItem>,
    onSpeakDefinition: (DefinitionItem) -> Unit
) {
    if (definitions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No definitions found in this chapter.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(definitions) { item ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth().testTag("definition_card_${item.term}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "DEFINED TERM",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 9.sp
                            )
                        }

                        IconButton(
                            onClick = { onSpeakDefinition(item) },
                            modifier = Modifier.size(28.dp).testTag("speak_definition_button")
                        ) {
                            Icon(
                                Icons.Default.VolumeUp,
                                contentDescription = "Listen",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.term,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.definition,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )

                    if (item.contextOrExample.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("💡", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.contextOrExample,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImportantFormulasTabContent(
    formulas: List<FormulaItem>,
    onSpeakFormula: (FormulaItem) -> Unit
) {
    if (formulas.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Functions,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "No mathematical formulas or equations recorded for this chapter.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(formulas) { formula ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.5.dp, TealSecondaryDark.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth().testTag("formula_card_${formula.name}")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header with name & voice button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TealSecondaryDark.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "FORMULA / LAW",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TealSecondaryDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 9.sp
                            )
                        }

                        IconButton(
                            onClick = { onSpeakFormula(formula) },
                            modifier = Modifier.size(28.dp).testTag("speak_formula_button")
                        ) {
                            Icon(
                                Icons.Default.VolumeUp,
                                contentDescription = "Listen",
                                tint = TealSecondaryDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = formula.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Formula Highlight Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.5.dp, TealSecondaryDark.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = formula.formula,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = TealSecondaryDark,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Explanation
                    Text(
                        text = formula.explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )

                    // Variables breakdown
                    if (formula.variables.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "📊 Variables & Units:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = formula.variables,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // Practical Calculation Example
                    if (formula.example.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AmberTertiaryDark.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, AmberTertiaryDark.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "🔢 Example Application:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberTertiaryDark,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = formula.example,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImprovementGuideTabContent(
    guide: ImprovementGuideItem,
    onSpeakGuide: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AmberTertiaryDark.copy(alpha = 0.12f)),
            border = BorderStroke(1.5.dp, AmberTertiaryDark.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth().testTag("improvement_hero_banner")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AmberTertiaryDark.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = AmberTertiaryDark, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI Student Improvement Roadmap",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Tailored study strategy to master this specific chapter with active recall & mnemonics.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = onSpeakGuide,
                    modifier = Modifier.size(32.dp).testTag("speak_improvement_button")
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = AmberTertiaryDark)
                }
            }
        }

        // 1. Common Weak Spots (Where students lose marks)
        if (guide.commonWeakSpots.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().testTag("weak_spots_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Common Student Weak Spots & Traps",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    guide.commonWeakSpots.forEach { spot ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("⚠️", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = spot,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 2. Mnemonic Memory Tricks
        if (guide.mnemonicTricks.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, TealSecondaryDark.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().testTag("mnemonics_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = TealSecondaryDark, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Mnemonic Shortcuts & Memory Hacks",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    guide.mnemonicTricks.forEach { trick ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = TealSecondaryDark.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, TealSecondaryDark.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "🧠 $trick",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(12.dp),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // 3. Spaced Repetition Revision Strategy
        if (guide.revisionStrategy.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, IndigoPrimaryDark.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().testTag("revision_strategy_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Timeline, contentDescription = null, tint = IndigoPrimaryDark, modifier = Modifier.size(20.dp))
                        Text(
                            text = "3-Stage Retention Timeline",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    guide.revisionStrategy.forEachIndexed { i, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = IndigoPrimaryDark,
                                modifier = Modifier.size(20.dp).padding(top = 2.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${i + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 4. Exam Readiness Tips
        if (guide.examReadinessTips.isNotBlank()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth().testTag("exam_tips_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎓 Exam Rubric & Test Day Tips",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = guide.examReadinessTips,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}


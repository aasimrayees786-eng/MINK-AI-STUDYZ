package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tutor_messages")
data class TutorMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chapterNoteId: Long? = null,
    val chapterTitle: String = "",
    val sender: String, // "USER" or "AI"
    val messageText: String,
    val spokenAudioScript: String = "", // Clean text optimized for TTS
    val languageCode: String = "en",
    val personaId: String = "sophia",
    val timestamp: Long = System.currentTimeMillis()
) {
    val isUser: Boolean get() = sender.equals("USER", ignoreCase = true)
    val senderName: String get() = if (isUser) "You" else "AI Tutor"
    val personaIcon: String get() = if (isUser) "👤" else "🎓"
}

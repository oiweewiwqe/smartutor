// app/src/main/java/com/example/smarttutorr/data/repository/NoteRepository.kt
package com.example.smarttutorr.data.repository

import com.example.smarttutorr.data.model.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class NoteRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

    suspend fun createNote(title: String, content: String): String {
        val note = Note(title = title, content = content)
        val noteRef = db.collection("users").document(userId).collection("notes").document()
        noteRef.set(note).await()
        return noteRef.id
    }

    suspend fun updateNote(noteId: String, title: String, content: String) {
        val note = Note(id = noteId, title = title, content = content, updatedAt = com.google.firebase.Timestamp.now())
        db.collection("users").document(userId).collection("notes").document(noteId)
            .set(note).await()
    }

    suspend fun deleteNote(noteId: String) {
        db.collection("users").document(userId).collection("notes").document(noteId)
            .delete().await()
    }

    suspend fun getNotes(): List<Note> {
        val snapshot = db.collection("users").document(userId).collection("notes")
            .orderBy("updatedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()
        return snapshot.toObjects(Note::class.java)
    }
}
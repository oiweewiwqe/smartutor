// app/src/main/java/com/example/smarttutorr/ui/main/notes/NoteEditorScreen.kt
package com.example.smarttutorr.ui.main.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smarttutorr.data.repository.NoteRepository
import kotlinx.coroutines.launch
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: String? = null,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val noteRepository = NoteRepository()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == null) "Новая заметка" else "Редактировать") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // ✅ Правильная иконка для Material 3
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isSaving = true
                        coroutineScope.launch {
                            if (noteId == null) {
                                noteRepository.createNote(title, content)
                            } else {
                                noteRepository.updateNote(noteId, title, content)
                            }
                            onBack()
                        }
                    }) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(Icons.Filled.Done, contentDescription = "Сохранить")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Заголовок") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
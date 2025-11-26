// app/src/main/java/com/example/smarttutorr/ui/auth/AuthViewModel.kt
package com.example.smarttutorr.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Простое состояние — Compose сам отслеживает изменения
    var uiState by mutableStateOf(AuthUiState())
        private set

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(error = "Заполните email и пароль")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                uiState = uiState.copy(isLoading = false)
                if (task.isSuccessful) {
                    uiState = uiState.copy(isSuccess = true)
                } else {
                    uiState = uiState.copy(
                        error = "Ошибка: ${task.exception?.message?.take(100)}"
                    )
                }
            }
    }

    fun register(email: String, password: String, name: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            uiState = uiState.copy(error = "Заполните все поля")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser!!
                    firestore.collection("users").document(user.uid).set(
                        mapOf(
                            "name" to name,
                            "email" to email,
                            "createdAt" to com.google.firebase.Timestamp.now()
                        )
                    ).addOnCompleteListener { writeTask ->
                        uiState = uiState.copy(
                            isLoading = false,
                            isSuccess = writeTask.isSuccessful,
                            error = if (!writeTask.isSuccessful) "Не удалось сохранить профиль" else null
                        )
                    }
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = "Регистрация не удалась: ${task.exception?.message?.take(100)}"
                    )
                }
            }
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
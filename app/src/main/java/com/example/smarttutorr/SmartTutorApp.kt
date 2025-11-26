// app/src/main/java/com/example/smarttutorr/SmartTutorApp.kt
package com.example.smarttutorr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.smarttutorr.ui.auth.AuthScreen
import com.example.smarttutorr.ui.main.MainScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SmartTutorApp() {
    var currentUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            currentUser = auth.currentUser
            isLoading = false
        }
    }

    if (isLoading) {
        androidx.compose.material3.CircularProgressIndicator()
    } else {
        if (currentUser == null) {
            AuthScreen(onNavigateToMain = {
                currentUser = FirebaseAuth.getInstance().currentUser
            })
        } else {
            MainScreen()
        }
    }
}
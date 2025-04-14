package com.example.adminapp

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
/*
@Composable
fun JoinedClassesScreen(studentId: String) {
    val db = Firebase.firestore
    var joinedCards by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val context = LocalContext.current
    var showJoinDialog by remember { mutableStateOf(false) }

    LaunchedEffect(studentId) {
        db.collection("students")
            .document(studentId)
            .collection("joinedClasses")
            .get()
            .addOnSuccessListener { result ->
                joinedCards = result.documents.mapNotNull { it.data }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    Scaffold(
        topBar = {
            StudentTopBar()
        },
        floatingActionButton = {
            JoinClassFab{showJoinDialog = true}
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            if (joinedCards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateText()
                }
            } else{
                LazyColumn {
                    items(joinedCards) { card ->
                        val cardData: Map<String, String> = card.mapValues { it.value.toString() }
                        ClassCard(data = cardData, onClick = {
                            val subjectCode = cardData["subjectCode"] ?: return@ClassCard
                            markAttendance(studentId, subjectCode) { success ->
                                Toast.makeText(
                                    context,
                                    if (success) "Attendance marked!" else "Failed to mark attendance",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    }
                }
            }
        }
    }

    if (showJoinDialog) {

        //..........................................
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            confirmButton = {},
            dismissButton = {},
            text = {
                var subjectCode by remember { mutableStateOf("") }
                Column {
                    OutlinedTextField(
                        value = subjectCode,
                        onValueChange = { subjectCode = it },
                        label = { Text("Enter Subject Code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        db.collection("subjectCards").document(subjectCode)
                            .get()
                            .addOnSuccessListener { doc ->
                                if (doc.exists()) {
                                    val cardData = doc.data!!
                                    db.collection("students")
                                        .document(studentId)
                                        .collection("joinedClasses")
                                        .document(subjectCode)
                                        .set(cardData)
                                    Toast.makeText(context, "Class joined successfully!", Toast.LENGTH_SHORT).show()
                                    showJoinDialog = false
                                } else {
                                    Toast.makeText(context, "Invalid subject code", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to join class", Toast.LENGTH_SHORT).show()
                            }
                    }) {
                        Text("Join Class")
                    }
                }
            },
            title = { Text("Join a Class") }
        )
    }
}*/
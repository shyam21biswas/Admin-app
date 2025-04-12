package com.example.adminapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
/*
@Composable
fun StudentCardListScreen(subjectCode: String) {
    val db = Firebase.firestore
    var cardDetails by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

        LaunchedEffect(subjectCode) {
            db.collectionGroup("classes")
                .whereEqualTo("subjectCode", subjectCode)
                .get()
                .addOnSuccessListener { snapshot ->
                    cardDetails = snapshot.documents.mapNotNull { it.data }
                }
        }

    Column (modifier = Modifier.fillMaxSize()){

        Text(text = "Student Card List", style = MaterialTheme.typography.headlineMedium)

        LazyColumn() {
            items(cardDetails) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📘 Subject: ${card["subjectName"]}")
                        Text("👨‍🏫 Teacher: ${card["teacherName"]}")
                        Text("📌 Subject Code: ${card["subjectCode"]}")
                        Text("🌍 Latitude: ${card["latitude"]}")
                        Text("🌍 Longitude: ${card["longitude"]}")
                        Text("📏 Threshold Distance: ${card["thresholdDistance"]} m")
                    }
                }
            }
        }
    }
}
*/
/*
@Composable
fun StudentCardListScreen() {
    val db = Firebase.firestore
    var cardDetails by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    // Fetch cards from all teachers
    LaunchedEffect(Unit) {
        db.collectionGroup("classes")
            .get()
            .addOnSuccessListener { snapshot ->
                val data = snapshot.documents.mapNotNull { it.data }
                cardDetails = if (data.isNotEmpty()) data else listOf(
                    mapOf(
                        "subjectName" to "Sample Subject",
                        "teacherName" to "Demo Teacher",
                        "subjectCode" to "DEMO123",
                        "latitude" to 0.0,
                        "longitude" to 0.0,
                        "thresholdDistance" to 100
                    )
                )
            }
            .addOnFailureListener {
                // On error, show dummy card
                cardDetails = listOf(
                    mapOf(
                        "subjectName" to "Sample Subject",
                        "teacherName" to "Demo Teacher",
                        "subjectCode" to "DEMO123",
                        "latitude" to 0.0,
                        "longitude" to 0.0,
                        "thresholdDistance" to 100
                    )
                )
            }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(cardDetails) { card ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📘 Subject: ${card["subjectName"]}")
                    Text("👨‍🏫 Teacher: ${card["teacherName"]}")
                    Text("📌 Subject Code: ${card["subjectCode"]}")
                    Text("🌍 Latitude: ${card["latitude"]}")
                    Text("🌍 Longitude: ${card["longitude"]}")
                    Text("📏 Threshold Distance: ${card["thresholdDistance"]} m")
                }
            }
        }
    }
}
*/

/*
//working  fine............................
@Composable
fun StudentCardBySubjectCode(teacherId: String, subjectCode: String) {
    val db = Firebase.firestore
    var card by remember { mutableStateOf<Map<String, Any>?>(null) }

    LaunchedEffect(subjectCode) {
        db.collection("teachers")
            .document(teacherId)
            .collection("classes")
            .document(subjectCode)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    card = document.data
                }
            }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        if (card != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📘 Subject: ${card!!["subjectName"]}")
                    Text("👨‍🏫 Teacher: ${card!!["teacherName"]}")
                    Text("📌 Subject Code: ${card!!["subjectCode"]}")
                    Text("🌍 Latitude: ${card!!["latitude"]}")
                    Text("🌍 Longitude: ${card!!["longitude"]}")
                    Text("📏 Threshold Distance: ${card!!["thresholdDistance"]} m")
                }
            }
        } else {
            Text("No class found for subject code: $subjectCode")
        }
    }
}
*/


fun getCardBySubjectCode(subjectCode: String, onResult: (Map<String, Any>?) -> Unit) {
    val db = Firebase.firestore
    db.collection("subjectCards")
        .document(subjectCode)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                onResult(document.data)
            } else {
                onResult(null)
            }
        }
}


@Composable
fun FetchCardBySubjectCodeScreen() {
    var subjectCode by remember { mutableStateOf("") }
    var cardData by remember { mutableStateOf<Map<String, Any>?>(null) }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = subjectCode,
            onValueChange = { subjectCode = it },
            label = { Text("Enter Subject Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            getCardBySubjectCode(subjectCode) {
                cardData = it
            }
        }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Fetch Card")
        }

        Spacer(modifier = Modifier.height(16.dp))

        cardData?.let { card ->
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📘 Subject: ${card["subjectName"]}")
                    Text("👨‍🏫 Teacher: ${card["teacherName"]}")
                    Text("📌 Subject Code: ${card["subjectCode"]}")
                    Text("🌍 Latitude: ${card["latitude"]}")
                    Text("🌍 Longitude: ${card["longitude"]}")
                    Text("📏 Distance: ${card["thresholdDistance"]} m")
                }
            }
        } ?: Text("No card found", Modifier.padding(top = 8.dp))
    }
}

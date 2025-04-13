package com.example.adminapp

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp
import com.google.firebase.firestore.SetOptions

/*
@Composable
fun StudentApp() {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("student_prefs", Context.MODE_PRIVATE)
    val navController = rememberNavController()

    // State to track if the student ID is stored
    var studentId by remember { mutableStateOf(sharedPrefs.getString("studentId", null)) }

    NavHost(
        navController = navController,
        startDestination = if (studentId == null) "studentLogin" else "joinClass"
    ) {
        composable("studentLogin") {
            StudentLoginScreen { id ->
                sharedPrefs.edit().putString("studentId", id).apply()
                studentId = id
                navController.navigate("joinClass") {
                    popUpTo("studentLogin") { inclusive = true }
                }
            }
        }

        composable("joinClass") {
            studentId?.let {
                JoinClassScreen(it)
            }
        }
    }
}



@Composable
fun StudentLoginScreen(onLogin: (String) -> Unit) {
    var studentId by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("Enter Student ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (studentId.isNotBlank()) onLogin(studentId)
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Login")
        }
    }
}

@Composable
fun JoinClassScreen(studentId: String) {
    val context = LocalContext.current
    val db = Firebase.firestore
    var subjectCode by remember { mutableStateOf("") }
    var joinedCards by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    // Fetch locally cached cards
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("joined_cards", Context.MODE_PRIVATE)
        val codes = prefs.getStringSet("codes", emptySet()) ?: emptySet()

        val cards = mutableListOf<Map<String, Any>>()
        codes.forEach { code ->
            db.collection("subjectCards").document(code).get().addOnSuccessListener {
                it.data?.let { data -> cards.add(data) }
                joinedCards = cards.toList()
            }
        }
    }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = subjectCode,
            onValueChange = { subjectCode = it },
            label = { Text("Enter Subject Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                //if (subjectCode.isBlank())  Toast.makeText(context, "Please enter a subject code", Toast.LENGTH_SHORT).show()
                val docRef = db.collection("subjectCards").document(subjectCode)
                docRef.get().addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val card = doc.data!!
                        // Save to local cache
                        val prefs = context.getSharedPreferences("joined_cards", Context.MODE_PRIVATE)
                        val codes = prefs.getStringSet("codes", mutableSetOf())!!.toMutableSet()
                        codes.add(subjectCode)
                        prefs.edit().putStringSet("codes", codes).apply()

                        // Save to Firestore under student
                        db.collection("students").document(studentId)
                            .collection("joinedClasses").document(subjectCode).set(card)

                        joinedCards = joinedCards + card
                        subjectCode = ""
                    } else {
                        Toast.makeText(context, "Card not found", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Join Class")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (joinedCards.isNotEmpty()) {
            LazyColumn {
                items(joinedCards) { card ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("📘 Subject: ${card["subjectName"]}")
                            Text("👨‍🏫 Teacher: ${card["teacherName"]}")
                            Text("📌 Subject Code: ${card["subjectCode"]}")
                            Text("🌍 Latitude: ${card["latitude"]}")
                            Text("🌍 Longitude: ${card["longitude"]}")
                            Text("📏 Threshold: ${card["thresholdDistance"]} m")
                        }
                    }
                }
            }
        } else {
            Text("No joined classes yet.", Modifier.padding(16.dp))
        }
    }
}

*/

@Composable
fun StudentApp() {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("student_prefs", Context.MODE_PRIVATE)
    val navController = rememberNavController()

    var studentId by remember { mutableStateOf(sharedPrefs.getString("studentId", null)) }

    NavHost(
        navController = navController,
        startDestination = if (studentId == null) "studentLogin" else "joinedClasses"
    ) {
        composable("studentLogin") {
            StudentLoginScreen { id ->
                sharedPrefs.edit().putString("studentId", id).apply()
                studentId = id
                navController.navigate("joinedClasses") {
                    popUpTo("studentLogin") { inclusive = true }
                }
            }
        }

        composable("joinedClasses") {
            studentId?.let {
                JoinedClassesScreen(it,navController)
            }
        }

        composable("joinClass") {
            studentId?.let {
                JoinClassScreen(it,navController)
            }




        }
    }
}

@Composable
fun StudentLoginScreen(onLogin: (String) -> Unit) {
    var studentId by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("Enter Student ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (studentId.isNotBlank()) onLogin(studentId)
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Login")
        }
    }
}

@Composable


fun JoinedClassesScreen(studentId: String,navController: NavHostController) {
    val db = Firebase.firestore
    var joinedCards by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val context = LocalContext.current
    //val navController = rememberNavController()

    LaunchedEffect(studentId) {
        db.collection("students")
            .document(studentId)
            .collection("joinedClasses")
            .get()
            .addOnSuccessListener { result ->
                joinedCards = result.documents.mapNotNull { it.data }
            }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Joined Classes", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(joinedCards) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            markAttendance(studentId, card["subjectCode"].toString()) { success ->
                                Toast.makeText(context, if (success) "Attendance marked!" else "Failed", Toast.LENGTH_SHORT).show()
                            }
                        },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(" Subject: ${card["subjectName"]}")
                        Text("👨‍🏫 Teacher: ${card["teacherName"]}")
                        Text(" Subject Code: ${card["subjectCode"]}")
                        //Text("🌍 Latitude: ${card["latitude"]}")
                        //Text("🌍 Longitude: ${card["longitude"]}")
                        //Text("📏 Distance: ${card["thresholdDistance"]} m")
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Navigate to join class screen
            navController.navigate("joinClass")
        }) {
            Text("Join New Class")
        }
    }
}
@Composable
fun JoinClassScreen(studentId: String,navController: NavHostController) {
    var subjectCode by remember { mutableStateOf("") }
    val db = Firebase.firestore
    var status by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(Modifier.padding(16.dp)) {
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
                        status = "Class joined successfully!"
                        Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                }
        }) {
            Text("Join Class")
        }

        if (status.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(status)
        }
    }
}

/*
fun markAttendance(studentId: String, subjectCode: String, onResult: (Boolean) -> Unit) {
    val db = Firebase.firestore
    val attendanceData = mapOf(
        "timestamp" to FieldValue.serverTimestamp()
    )

    db.collection("students")
        .document(studentId)
        .collection("attendance")
        .document(subjectCode)
        .collection("logs")
        .add(attendanceData)
        .addOnSuccessListener { onResult(true) }
        .addOnFailureListener { onResult(false) }
}

*/
fun markAttendance(studentId: String, subjectCode: String, onResult: (Boolean) -> Unit) {
    val db = Firebase.firestore
    val timestamp = Timestamp.now() // or use FieldValue.serverTimestamp() in a map

    val docRef = db.collection("subjectAttendance").document(subjectCode)

    db.runTransaction { transaction ->
        val snapshot = transaction.get(docRef)

        val logs = snapshot.get("logs") as? Map<String, List<Timestamp>> ?: emptyMap()
        val updatedList = logs[studentId]?.toMutableList() ?: mutableListOf()
        updatedList.add(timestamp)

        val updatedLogs = HashMap(logs)
        updatedLogs[studentId] = updatedList

        transaction.set(docRef, mapOf("logs" to updatedLogs), SetOptions.merge())
    }.addOnSuccessListener {
        onResult(true)
    }.addOnFailureListener {
        it.printStackTrace()
        onResult(false)
    }
}



/*
//workinh
fun markAttendance(studentId: String, subjectCode: String, onResult: (Boolean) -> Unit) {
    val db = Firebase.firestore
    val attendanceData = mapOf("timestamp" to FieldValue.serverTimestamp())

    val studentPath = db.collection("students")
        .document(studentId)
        .collection("attendance")
        .document(subjectCode)
        .collection("logs")

    val subjectPath = db.collection("subjectAttendance")
        .document(subjectCode)
        .collection("students")
        .document(studentId)
        .collection("logs")

    val batch = db.batch()
    batch.set(studentPath.document(), attendanceData)
    batch.set(subjectPath.document(), attendanceData)

    batch.commit()
        .addOnSuccessListener { onResult(true) }
        .addOnFailureListener { onResult(false) }
}

/*
fun markAttendance(studentId: String, subjectCode: String, onResult: (Boolean) -> Unit) {
    val db = Firebase.firestore
    val timestamp = Timestamp.now()
    val attendanceData = mapOf("timestamp" to timestamp)

    // Path 1: Student-specific logs
    val studentPath = db.collection("students")
        .document(studentId)
        .collection("attendance")
        .document(subjectCode)
        .collection("logs")

    // Path 2: Subject-specific logs
    val subjectPath = db.collection("subjectAttendance")
        .document(subjectCode)
        .collection("students")
        .document(studentId)
        .collection("logs")

    // Path 3: Update summary logs inside subjectAttendance
    val summaryPath = db.collection("subjectAttendance")
        .document(subjectCode)

    // Run batch for logs
    val batch = db.batch()
    batch.set(studentPath.document(), attendanceData)
    batch.set(subjectPath.document(), attendanceData)

    // Commit batch
    batch.commit()
        .addOnSuccessListener {
            // Now update summary logs field
            summaryPath.update("logs.$studentId", FieldValue.arrayUnion(timestamp))
                .addOnSuccessListener { onResult(true) }
                .addOnFailureListener { onResult(false) }
        }
        .addOnFailureListener {
            onResult(false)
        }
}
*/

//having doubts...............
/*
@Composable
fun StudentApp() {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("student_prefs", Context.MODE_PRIVATE)
    val navController = rememberNavController()

    var studentId by remember { mutableStateOf(sharedPrefs.getString("studentId", null)) }

    NavHost(
        navController = navController,
        startDestination = if (studentId == null) "studentLogin" else "joinedClasses"
    ) {
        composable("studentLogin") {
            StudentLoginScreen { id ->
                sharedPrefs.edit().putString("studentId", id).apply()
                studentId = id
                navController.navigate("joinedClasses") {
                    popUpTo("studentLogin") { inclusive = true }
                }
            }
        }

        composable("joinedClasses") {
            studentId?.let {
                JoinedClassesScreen(it, navController)
            }
        }

        composable("joinClass") {
            studentId?.let {
                JoinClassScreen(it, navController)
            }
        }
    }
}

@Composable
fun StudentLoginScreen(onLogin: (String) -> Unit) {
    var studentId by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("Enter Student ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (studentId.isNotBlank()) onLogin(studentId)
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Login")
        }
    }
}

@Composable
fun JoinedClassesScreen(studentId: String, navController: NavHostController) {
    val db = Firebase.firestore
    var joinedCards by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(studentId) {
        db.collection("students")
            .document(studentId)
            .collection("joinedClasses")
            .get()
            .addOnSuccessListener { result ->
                joinedCards = result.documents.mapNotNull { it.data }
            }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Joined Classes", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(joinedCards) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            // When card clicked, mark attendance for the student in the specific class
                            markAttendance(studentId, card["subjectCode"].toString()) { success ->
                                Toast.makeText(context, if (success) "Attendance marked!" else "Failed", Toast.LENGTH_SHORT).show()
                            }
                        },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(" Subject: ${card["subjectName"]}")
                        Text("👨‍🏫 Teacher: ${card["teacherName"]}")
                        Text(" Subject Code: ${card["subjectCode"]}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Navigate to join class screen
            navController.navigate("joinClass")
        }) {
            Text("Join New Class")
        }
    }
}

@Composable
fun JoinClassScreen(studentId: String, navController: NavHostController) {
    var subjectCode by remember { mutableStateOf("") }
    val db = Firebase.firestore
    var status by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(Modifier.padding(16.dp)) {
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
                        status = "Class joined successfully!"
                        Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Subject not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error joining class", Toast.LENGTH_SHORT).show()
                }
        }) {
            Text("Join Class")
        }

        if (status.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(status)
        }
    }
}

// Mark attendance function
fun markAttendance(studentId: String, subjectCode: String, onResult: (Boolean) -> Unit) {
    val db = Firebase.firestore
    val timestamp = Timestamp.now()
    val attendanceData = mapOf("timestamp" to timestamp)

    // Path 1: Student-specific logs
    val studentPath = db.collection("students")
        .document(studentId)
        .collection("attendance")
        .document(subjectCode)
        .collection("logs")

    // Path 2: Subject-specific logs
    val subjectPath = db.collection("subjectAttendance")
        .document(subjectCode)
        .collection("students")
        .document(studentId)
        .collection("logs")

    // Path 3: Update summary logs inside subjectAttendance
    val summaryPath = db.collection("subjectAttendance")
        .document(subjectCode)

    // Run batch for logs
    val batch = db.batch()
    batch.set(studentPath.document(), attendanceData)
    batch.set(subjectPath.document(), attendanceData)

    // Commit batch
    batch.commit()
        .addOnSuccessListener {
            // Now update summary logs field
            summaryPath.update("logs.$studentId", FieldValue.arrayUnion(timestamp))
                .addOnSuccessListener { onResult(true) }
                .addOnFailureListener { onResult(false) }
        }
        .addOnFailureListener {
            onResult(false)
        }
}



*/
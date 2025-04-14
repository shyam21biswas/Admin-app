package com.example.adminapp
package com.example.adminapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
                JoinedClassesScreen(it)
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
            .addOnFailureListener { e -> e.printStackTrace() }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Joined Classes", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            navController.navigate("joinClass")
        }) {
            Text("Join New Class")
        }
    }
}
*/


@Composable
fun ClassCard(data: Map<String, String>, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "scaleAnimation"
    )

    val gradientList = listOf(
        listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2)),
        listOf(Color(0xFFFFCDD2), Color(0xFFFFAB91)),
        listOf(Color(0xFFDCEDC8), Color(0xFFC5E1A5))
    )
    val chosenGradient = remember { gradientList.random() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                        onClick() // 👈 Trigger attendance
                    }
                )
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.verticalGradient(colors = chosenGradient))
                .padding(24.dp)
        ) {
            Column(Modifier.fillMaxSize()) {
                Text(
                    text = data["subjectName"] ?: "",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF000000)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Subject Code: ${data["subjectCode"]}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.DarkGray
                )
                Text(
                    text = "Teacher: ${data["teacherName"]}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinedClassesScreen(studentId: String) {
    val db = Firebase.firestore
    var joinedCards by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val context = LocalContext.current
    var showJoinDialog by remember { mutableStateOf(false) }
    //var joinedCards by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var showJoinSheet by remember { mutableStateOf(false) }
    var subjectCode by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
            JoinClassFab{showJoinSheet = true}
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

    if (showJoinSheet) {
        JoinClassModalSheet(
            subjectCode = subjectCode,
            onSubjectCodeChange = { subjectCode = it },
            onJoinClick = {
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
                            showJoinSheet = false
                            subjectCode = ""

                            //new to handel data  not on time
                            // 🔁 Refresh the joined cards here:
                            db.collection("students")
                                .document(studentId)
                                .collection("joinedClasses")
                                .get()
                                .addOnSuccessListener { result ->
                                    joinedCards = result.documents.mapNotNull { it.data }
                                }

                        } else {
                            Toast.makeText(context, "Invalid subject code", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to join class", Toast.LENGTH_SHORT).show()
                    }
            },
            onDismiss = { showJoinSheet = false },
            sheetState = sheetState
        )
    }
}

@Composable
fun EmptyStateText() {
    Text("No class joined yet", style = MaterialTheme.typography.titleMedium)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text("Student Home", style = MaterialTheme.typography.titleLarge)
        }
    )
}
@Composable
fun JoinClassFab(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(Icons.Default.Add, contentDescription = "Join Class")
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun JoinClassModalSheet(
    subjectCode: String,
    onSubjectCodeChange: (String) -> Unit,
    onJoinClick: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets.ime
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Join a Class", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = subjectCode,
                onValueChange = onSubjectCodeChange,
                label = { Text("Enter Subject Code") },
                shape = RoundedCornerShape(12.dp)

            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onJoinClick,

                ) {
                Text("Join Class")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
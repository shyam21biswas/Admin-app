package com.example.adminapp
// Firebase and Firestore
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

// Jetpack Compose
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

// Kotlin
import java.util.Date
/*
fun fetchSubjectAttendance(
    subjectCode: String,
    onResult: (Map<String, List<Timestamp>>) -> Unit
) {
    val db = Firebase.firestore
    val resultMap = mutableMapOf<String, List<Timestamp>>()

    db.collection("subjectAttendance")
        .document(subjectCode)
        .collection("students")
        .get()
        .addOnSuccessListener { studentDocs ->
            val tasks = mutableListOf<Task<QuerySnapshot>>()

            for (doc in studentDocs) {
                val studentId = doc.id
                val logsRef = doc.reference.collection("logs")
                val task = logsRef.get().addOnSuccessListener { logs ->
                    val timestamps = logs.mapNotNull { it.getTimestamp("timestamp") }
                    resultMap[studentId] = timestamps
                }
                tasks.add(task)
            }

            Tasks.whenAllComplete(tasks)
                .addOnSuccessListener {
                    onResult(resultMap)
                }
        }
}
*/
/*
fun fetchSubjectAttendance(
    subjectCode: String,
    onResult: (Map<String, List<Timestamp>>) -> Unit
) {
    val db = Firebase.firestore
    val resultMap = mutableMapOf<String, List<Timestamp>>()

    db.collection("subjectAttendance")
        .document("7979")
        .collection("students")
        .get()
        .addOnSuccessListener { studentDocs ->
            if (studentDocs.isEmpty) {
                Log.d("FetchAttendance", "No students found for subject: $subjectCode")
            }

            val tasks = mutableListOf<Task<QuerySnapshot>>()

            for (doc in studentDocs) {
                val studentId = doc.id
                Log.d("FetchAttendance", "Found student: $studentId")
                val logsRef = doc.reference.collection("logs")
                val task = logsRef.get().addOnSuccessListener { logs ->
                    val timestamps = logs.mapNotNull { it.getTimestamp("timestamp") }
                    Log.d("FetchAttendance", "Timestamps for $studentId: $timestamps")
                    resultMap[studentId] = timestamps
                }
                tasks.add(task)
            }

            Tasks.whenAllComplete(tasks)
                .addOnSuccessListener {
                    Log.d("FetchAttendance", "All tasks completed. Result map: $resultMap")
                    onResult(resultMap)
                }
        }
        .addOnFailureListener {
            Log.e("FetchAttendance", "Error fetching students", it)
        }
}*/

/*
fun fetchSubjectAttendance(subjectCode: String, onResult: (Map<String, List<Timestamp>>) -> Unit) {
    val db = Firebase.firestore
    val resultMap = mutableMapOf<String, List<Timestamp>>()

    db.collection("subjectAttendance")
        .document(subjectCode)
        .collection("students")
        .get()
        .addOnSuccessListener { studentDocs ->
            val tasks = mutableListOf<Task<QuerySnapshot>>()

            for (doc in studentDocs) {
                val studentId = doc.id
                val logsRef = doc.reference.collection("logs")
                val task = logsRef.get().addOnSuccessListener { logs ->
                    val timestamps = logs.mapNotNull { it.getTimestamp("timestamp") }
                    resultMap[studentId] = timestamps
                }
                tasks.add(task)
            }

            Tasks.whenAllComplete(tasks)
                .addOnSuccessListener {
                    onResult(resultMap)
                }
        }
}
*/

/*@Composable
fun SubjectAttendanceScreen(subjectCode: String) {


        var attendanceMap by remember { mutableStateOf<Map<String, List<Timestamp>>>(emptyMap()) }

        LaunchedEffect(subjectCode) {
            fetchSubjectAttendance(subjectCode) {
                attendanceMap = it
            }
        }

        LazyColumn {
            attendanceMap.forEach { (studentId, timestamps) ->
                item {
                    Card(Modifier.padding(8.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("👨 Student ID: $studentId")
                            timestamps.forEach {
                                Text("📅 ${it.toDate()}")
                            }
                        }
                    }
                }
            }
        }
    }*/
/*
@Composable
fun SubjectAttendanceScreen(subjectCode: String) {
    var attendanceMap by remember { mutableStateOf<Map<String, List<Timestamp>>>(emptyMap()) }
    Column(modifier = Modifier.fillMaxSize()) {
        LaunchedEffect(subjectCode) {
            fetchSubjectAttendance(subjectCode) {
                attendanceMap = it
            }
        }

        LazyColumn {
            attendanceMap.forEach { (studentId, timestamps) ->
                item {
                    Card(Modifier.padding(8.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("👨 Student ID: $studentId")
                            timestamps.forEach {
                                Text("📅 ${it.toDate()}")
                            }
                        }
                    }
                }
            }
        }
    }

}*/
/*
@Composable
fun SubjectAttendanceScreen(subjectCode: String) {
    var attendanceMap by remember { mutableStateOf<Map<String, List<Timestamp>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

   /* LaunchedEffect(subjectCode) {
        fetchSubjectAttendance(subjectCode) {
            attendanceMap = it
            isLoading = false
        }
    }*/
    LaunchedEffect(Unit) {
        fetchSubjectAttendance("7979") {  // Replace with your real subject code
            attendanceMap = it
            isLoading = false
        }
    }

    when {
        isLoading -> {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
        attendanceMap.isEmpty() -> {
            Text("No attendance found for subject code: $subjectCode", modifier = Modifier.padding(16.dp))
        }
        else -> {
            LazyColumn {
                attendanceMap.forEach { (studentId, timestamps) ->
                    item {
                        Card(Modifier.padding(8.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Text("👨 Student ID: $studentId")
                                timestamps.forEach {
                                    Text("📅 ${it.toDate()}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

*/

/*
@Composable
fun AttendanceFetchApp() {
    var subjectCode by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    Column(Modifier.padding(16.dp)) {
        if (!submitted) {
            OutlinedTextField(
                value = subjectCode,
                onValueChange = { subjectCode = it },
                label = { Text("Enter Subject Code") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(onClick = {
                if (subjectCode.isNotBlank()) submitted = true
            }) {
                Text("Fetch Attendance")
            }
        } else {
            SubjectAttendanceScreen(subjectCode)
        }
    }
}
fun fetchSubjectAttendance(subjectCode: String, onResult: (Map<String, List<Timestamp>>) -> Unit) {
    val db = Firebase.firestore
    val resultMap = mutableMapOf<String, List<Timestamp>>()

    Log.d("ATTENDANCE", "Fetching attendance for subject: $subjectCode")

    db.collection("subjectAttendance")
        .document(subjectCode)
        .collection("students")
        .get()
        .addOnSuccessListener { studentDocs ->
            if (studentDocs.isEmpty) {
                Log.d("ATTENDANCE", "No student documents found under this subject.")
            }

            val tasks = mutableListOf<Task<QuerySnapshot>>()

            for (doc in studentDocs) {
                val studentId = doc.id
                val logsRef = doc.reference.collection("logs")
                val task = logsRef.get().addOnSuccessListener { logs ->
                    if (logs.isEmpty) {
                        Log.d("ATTENDANCE", "No logs for student: $studentId")
                    }

                    val timestamps = logs.mapNotNull {
                        val ts = it.getTimestamp("timestamp")
                        Log.d("ATTENDANCE", "Student $studentId → Timestamp found: $ts")
                        ts
                    }

                    resultMap[studentId] = timestamps
                }
                tasks.add(task)
            }

            Tasks.whenAllComplete(tasks)
                .addOnSuccessListener {
                    Log.d("ATTENDANCE", "Final result: $resultMap")
                    onResult(resultMap)
                }
        }
        .addOnFailureListener {
            Log.e("ATTENDANCE", "Failed to fetch attendance: ${it.message}")
        }
}

@Composable
fun SubjectAttendanceScreen(subjectCode: String) {
    var attendanceMap by remember { mutableStateOf<Map<String, List<Timestamp>>>(emptyMap()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(subjectCode) {
        loading = true
        fetchSubjectAttendance(subjectCode) {
            attendanceMap = it
            loading = false
        }
    }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (attendanceMap.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("😕 No attendance found for this subject.")
        }
    } else {
        LazyColumn {
            attendanceMap.forEach { (studentId, timestamps) ->
                item {
                    Card(Modifier.padding(8.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("👨 Student ID: $studentId")
                            timestamps.forEach {
                                Text("📅 ${it.toDate()}")
                            }
                        }
                    }
                }
            }
        }
    }
}


fun debugSingleStudent() {
    val db = Firebase.firestore


    db.collection("subjectAttendance")
        .document("7979")
        .collection("students")
        .document("22cd3033")  // Replace with actual student ID
        .collection("logs")
        .get()
        .addOnSuccessListener { docs ->
            for (doc in docs) {
                val ts = doc.getTimestamp("timestamp")
                //Toast.makeText(this, "Timestamp: $ts", Toast.LENGTH_SHORT).show()
                Log.d("DEBUG_SINGLE", "Timestamp: $ts")
            }
        }
        .addOnFailureListener {
            Log.e("DEBUG_SINGLE", "Failed to get logs", it)
        }
}*/
// Required imports

/*
// Function to fetch attendance data
fun fetchSubjectAttendance(
    subjectCode: String,
    onResult: (Map<String, List<Timestamp>>) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val resultMap = mutableMapOf<String, List<Timestamp>>()

    db.collection("subjectAttendance")
        .document(subjectCode)
        .collection("students")
        .get()
        .addOnSuccessListener { studentDocs ->
            val tasks = mutableListOf<Task<QuerySnapshot>>()

            for (doc in studentDocs) {
                val studentId = doc.id
                val logsRef = doc.reference.collection("logs")
                val task = logsRef.get().addOnSuccessListener { logs ->
                    val timestamps = logs.mapNotNull {
                        Log.d("CHECK_DOC", it.data.toString())
                        val nestedMap = it.data["timestamp"] as? Map<*, *>
                        nestedMap?.get("timestamp") as? Timestamp
                        // If timestamp is directly stored:
                        // it.getTimestamp("timestamp")
                    }
                    if (timestamps.isNotEmpty()) {
                        resultMap[studentId] = timestamps
                    }
                }
                tasks.add(task)
            }

            Tasks.whenAllComplete(tasks)
                .addOnSuccessListener {
                    onResult(resultMap)
                }
        }
}

// UI Screen: Input + Attendance display
@Composable
fun AttendanceFetcherScreen() {
    var subjectCode by remember { mutableStateOf("") }
    var attendanceMap by remember { mutableStateOf<Map<String, List<Timestamp>>>(emptyMap()) }
    var fetchClicked by remember { mutableStateOf(false) }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = subjectCode,
            onValueChange = { subjectCode = it },
            label = { Text("Enter Subject Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            fetchClicked = true
            fetchSubjectAttendance(subjectCode) {
                attendanceMap = it
            }
        }) {
            Text("Fetch Attendance")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (fetchClicked) {
            if (attendanceMap.isEmpty()) {
                Text("No attendance found or timestamp format is incorrect.")
            } else {
                LazyColumn {
                    attendanceMap.forEach { (studentId, timestamps) ->
                        item {
                            Card(Modifier.padding(8.dp)) {
                                Column(Modifier.padding(16.dp)) {
                                    Text("👨 Student ID: $studentId")
                                    timestamps.forEach {
                                        Text("📅 ${it.toDate()}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}*/

/*
// working.......................................
@Composable
fun SubjectAttendanceScreen(subjectCode: String, studentId: String) {
    var attendanceMap by remember { mutableStateOf<Map<String, List<Timestamp>>>(emptyMap()) }

    LaunchedEffect(subjectCode) {
        fetchSubjectAttendance(subjectCode, studentId) { result ->
            attendanceMap = result
        }
    }

    LazyColumn {
        items(attendanceMap.keys.toList()) { studentId ->
            Card(Modifier.padding(8.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("👨 Student ID: $studentId")
                    attendanceMap[studentId]?.forEach {
                        Text("📅 ${it.toDate()}")
                    }
                }
            }
        }
    }
}

fun fetchSubjectAttendance(subjectCode: String, studentId: String, onResult: (Map<String, List<Timestamp>>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val resultMap = mutableMapOf<String, List<Timestamp>>()

    db.collection("subjectAttendance")
        .document(subjectCode)
        .collection("students")
        .document(studentId)  // Replace with actual student ID
        .collection("logs")
        .get()
        .addOnSuccessListener { logs ->
            if (logs.isEmpty) {
                Log.d("DEBUG_SINGLE", "No logs found for this student")
            } else {
                logs.forEach { log ->
                    val ts = log.getTimestamp("timestamp")
                    if (ts != null) {
                        val studentId = log.id  // Student ID
                        if (resultMap.containsKey(studentId)) {
                            resultMap[studentId] = resultMap[studentId]?.plus(ts) ?: listOf(ts)
                        } else {
                            resultMap[studentId] = listOf(ts)
                        }
                    }
                }
            }
            onResult(resultMap) // Return the result
        }
        .addOnFailureListener {
            Log.e("DEBUG_SINGLE", "Failed to get logs", it)
            onResult(resultMap) // Return empty result on failure
        }
}*/


/*
@Composable
fun AttendanceInputScreen() {
    // State variables to hold user input for subjectCode
    var subjectCode by remember { mutableStateOf("7979") }
    var isFetching by remember { mutableStateOf(true) }

    // Trigger to fetch attendance data
    if (isFetching) {
        isFetching = false
        SubjectAttendanceScreen(subjectCode = subjectCode)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Enter Subject Code:")
        TextField(
            value = subjectCode,
            onValueChange = { subjectCode = it },
            label = { Text("Subject Code") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        Button(
            onClick = { isFetching = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fetch Attendance")
        }
    }
}


@Composable
fun SubjectAttendanceScreen(subjectCode: String) {
    var attendanceMap by remember { mutableStateOf<Map<String, List<Timestamp>>>(emptyMap()) }

    LaunchedEffect(subjectCode) {
        fetchSubjectAttendance(subjectCode) { result ->
            attendanceMap = result
        }
    }

    LazyColumn {
        items(attendanceMap.keys.toList()) { studentId ->
            Card(Modifier.padding(8.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("👨 Student ID: $studentId")
                    attendanceMap[studentId]?.forEach {
                        Text("📅 ${it.toDate()}")
                    }
                }
            }
        }
    }
}

fun fetchSubjectAttendance(subjectCode: String, onResult: (Map<String, List<Timestamp>>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val resultMap = mutableMapOf<String, List<Timestamp>>()

    // Fetch all students under the given subjectCode
    db.collection("subjectAttendance")
        .document(subjectCode)
        .collection("students")
        .get()
        .addOnSuccessListener { studentDocs ->
            val tasks = mutableListOf<Task<QuerySnapshot>>()

            // Fetch attendance (logs) for each student
            studentDocs.forEach { studentDoc ->
                val studentId = studentDoc.id
                val logsRef = studentDoc.reference.collection("logs")
                val task = logsRef.get().addOnSuccessListener { logs ->
                    val timestamps = logs.mapNotNull { it.getTimestamp("timestamp") }
                    resultMap[studentId] = timestamps
                }
                tasks.add(task)
            }

            // After fetching logs for all students, return the result
            Tasks.whenAllComplete(tasks)
                .addOnSuccessListener {
                    onResult(resultMap)
                }
        }
        .addOnFailureListener {
            Log.e("FETCH_ATTENDANCE", "Failed to fetch students or attendance", it)
            onResult(resultMap) // Return empty map on failure
        }
}
*/

//

fun fetchAttendanceSummary(subjectCode: String, onResult: (Map<String, List<Timestamp>>) -> Unit) {
    val db = Firebase.firestore

    db.collection("subjectAttendance")
        .document(subjectCode)
        .get()
        .addOnSuccessListener { document ->
            val logsMap = document.get("logs") as? Map<*, *>
            if (logsMap != null) {
                val resultMap = mutableMapOf<String, List<Timestamp>>()

                for ((key, value) in logsMap) {
                    val studentId = key as? String
                    val timestampList = (value as? List<*>)?.mapNotNull { it as? Timestamp }

                    if (studentId != null && timestampList != null) {
                        resultMap[studentId] = timestampList
                    }
                }

                onResult(resultMap)
            } else {
                onResult(emptyMap())
            }
        }
        .addOnFailureListener {
            onResult(emptyMap())
        }
}

@Composable
fun AttendanceSummaryScreen(subjectCode: String) {
    var attendanceMap by remember { mutableStateOf<Map<String, List<Timestamp>>>(emptyMap()) }

    LaunchedEffect(subjectCode) {
        fetchAttendanceSummary(subjectCode) {
            attendanceMap = it
        }
    }

    LazyColumn {
        attendanceMap.forEach { (studentId, timestamps) ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("👨 Student ID: $studentId", style = MaterialTheme.typography.titleMedium)
                        timestamps.forEach { ts ->
                            Text("📅 ${ts.toDate()}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

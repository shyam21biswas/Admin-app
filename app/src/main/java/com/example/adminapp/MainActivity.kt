package com.example.adminapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.adminapp.ui.theme.AdminAppTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Initialize Firebase
        enableEdgeToEdge()
        setContent {
            AdminAppTheme {

               // TeacherApp()
                StudentApp()
               // AttendanceSummaryScreen("DA")



            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AdminPrefs", Context.MODE_PRIVATE)
    var teacherId by remember { mutableStateOf(sharedPreferences.getString("teacherId", "") ?: "") }

    if (teacherId.isEmpty()) {
        TeacherLoginScreen(onLogin = { id ->
            teacherId = id
            sharedPreferences.edit().putString("teacherId", id).apply()
        })
    } else {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Teacher Dashboard") })
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "createClass",
                modifier = Modifier.padding(padding)
            ) {
                composable("createClass") { CreateClassScreen(teacherId, navController) }
                composable("luv") { UpdateLocationScreen(teacherId) }
            }
        }
    }
}

@Composable
fun TeacherLoginScreen(onLogin: (String) -> Unit) {
    var inputId by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = inputId,
            onValueChange = { inputId = it },
            label = { Text("Enter Teacher ID") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { if (inputId.isNotBlank()) onLogin(inputId) }) {
            Text("Login")
        }
    }
}


@Composable
fun CreateClassScreen(teacherId: String, navController: NavController) {
    val db = Firebase.firestore
    var subjectName by remember { mutableStateOf("") }
    var subjectCode by remember { mutableStateOf("") }
    var teacherName by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(value = teacherName, onValueChange = { teacherName = it }, label = { Text("Teacher Name") })
        OutlinedTextField(value = subjectName, onValueChange = { subjectName = it }, label = { Text("Subject Name") })
        OutlinedTextField(value = subjectCode, onValueChange = { subjectCode = it }, label = { Text("Subject Code") })

        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val data = mapOf(
                "teacherName" to teacherName,
                "subjectName" to subjectName,
                "subjectCode" to subjectCode,
                "latitude" to 0.0,
                "longitude" to 0.0,
                "thresholdDistance" to 0
            )

            db.collection("teachers").document(teacherId)
                .collection("classes")
                .document(subjectCode)
                .set(data)

            db.collection("subjectCards")
                .document(subjectCode)
                .set(data) // same data as saved under teachers/teacherId/classes/...
        }) {
            Text("Create Class")
        }




        Button(onClick = { navController.navigate("luv") }) {
            Text("update")
        }
    }
}

@Composable
fun UpdateLocationScreen(teacherId: String) {
    val db = Firebase.firestore
    var subjectCodes by remember { mutableStateOf(listOf<String>()) }
    var selectedCode by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        db.collection("teachers").document(teacherId)
            .collection("classes")
            .get()
            .addOnSuccessListener { snapshot ->
                subjectCodes = snapshot.documents.map { it.id }
            }
    }

    Column(Modifier.padding(16.dp)) {
        DropdownMenuBox(subjectCodes, selectedCode) { selectedCode = it }

        OutlinedTextField(value = latitude, onValueChange = { latitude = it }, label = { Text("Latitude") })
        OutlinedTextField(value = longitude, onValueChange = { longitude = it }, label = { Text("Longitude") })
        OutlinedTextField(value = distance, onValueChange = { distance = it }, label = { Text("Threshold Distance") })

        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val updates = mapOf(
                "latitude" to latitude.toDoubleOrNull(),
                "longitude" to longitude.toDoubleOrNull(),
                "thresholdDistance" to distance.toIntOrNull()
            )
            db.collection("teachers").document(teacherId)
                .collection("classes")
                .document(selectedCode)
                .update(updates)
            db.collection("subjectCards")
                .document(selectedCode)
                .update(updates) // same data as saved under teachers/teacherId/classes/...
        }) {
            Text("Update")
        }
    }
}

@Composable
fun DropdownMenuBox(options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().clickable { expanded = true },
            readOnly = true,
            label = { Text("Select Subject Code") },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {
                DropdownMenuItem(text = { Text(it) }, onClick = {
                    onSelected(it)
                    expanded = false
                })
            }
        }
    }
}

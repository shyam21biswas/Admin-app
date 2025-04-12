package com.example.adminapp

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun BottomNavScreen() {
    val navController = rememberNavController()
    Scaffold(

        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding),
            enterTransition = {  fadeIn() },
            exitTransition = {  fadeOut() },
            popEnterTransition = { slideInHorizontally() },
            popExitTransition = { slideOutHorizontally() }

        ) {
            composable("home") { HomeScreen() }
            composable("ride") { RideScreen() }

        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(

        BottomNavItem("Ride", R.drawable.baseline_group_add_24, "ride"),
        BottomNavItem("Home", R.drawable.baseline_tips_and_updates_24, "home"),

    )
    NavigationBar {
        val currentRoute = currentRoute(navController)
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
fun HomeScreen() {
    Text(text = "🏠 Home Screen")




}

@Composable
fun RideScreen() { Text(text = "🚗 Ride Screen")

}
// we have connect api to show a lazy column.....

@Composable
fun ProfileScreen() {
    Text(text = "👤 Profile Screen")
    Spacer(modifier = Modifier.padding(60.dp))

}
data class BottomNavItem(val label: String, val icon: Int, val route: String)

fun currentRoute(navController: NavController): String? {
    return navController.currentBackStackEntry?.destination?.route
}



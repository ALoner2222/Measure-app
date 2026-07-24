package com.alinco.measureapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alinco.measureapp.ui.ARMeasureScreen
import com.alinco.measureapp.ui.CalculatorScreen
import com.alinco.measureapp.ui.ConverterScreen
import com.alinco.measureapp.ui.theme.MeasureAppTheme

sealed class Screen(val route: String, val label: String) {
    object Converter : Screen("converter", "Convert")
    object Calculator : Screen("calculator", "Calculate")
    object ArMeasure : Screen("ar_measure", "AR Ruler")
}

val screens = listOf(Screen.Converter, Screen.Calculator, Screen.ArMeasure)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeasureAppTheme {
                MeasureApp()
            }
        }
    }
}

@Composable
fun MeasureApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Measure") })
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    selected = currentRoute == Screen.Converter.route,
                    onClick = { navigate(navController, Screen.Converter.route) },
                    icon = { Icon(Icons.Filled.Straighten, contentDescription = null) },
                    label = { Text(Screen.Converter.label) }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.Calculator.route,
                    onClick = { navigate(navController, Screen.Calculator.route) },
                    icon = { Icon(Icons.Filled.Calculate, contentDescription = null) },
                    label = { Text(Screen.Calculator.label) }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.ArMeasure.route,
                    onClick = { navigate(navController, Screen.ArMeasure.route) },
                    icon = { Icon(Icons.Filled.CameraAlt, contentDescription = null) },
                    label = { Text(Screen.ArMeasure.label) }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Converter.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Converter.route) { ConverterScreen() }
            composable(Screen.Calculator.route) { CalculatorScreen() }
            composable(Screen.ArMeasure.route) { ARMeasureScreen() }
        }
    }
}

private fun navigate(navController: androidx.navigation.NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

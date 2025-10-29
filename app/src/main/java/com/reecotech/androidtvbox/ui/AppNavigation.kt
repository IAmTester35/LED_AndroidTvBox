package com.reecotech.androidtvbox.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "activation") {
        composable("activation") {
            ActivationScreen(onActivated = { navController.navigate("display") })
        }
        composable("display") {
            DisplayScreen()
        }
    }
}

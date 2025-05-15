package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.HomeScreen
import com.example.myapplication.screens.NewsDetailScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }

        composable("newsDetail/{newsId}") { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("newsId")
            NewsDetailScreen(newsId = newsId, navController = navController)
        }



    }
}



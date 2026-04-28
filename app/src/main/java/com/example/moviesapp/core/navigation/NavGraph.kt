package com.example.moviesapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.moviesapp.features.movies.presentation.details.MovieDetailsScreen
import com.example.moviesapp.features.movies.presentation.nowplaying.NowPlayingScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.NowPlaying.route
    ) {
        composable(route = Screen.NowPlaying.route) {
            NowPlayingScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Screen.Details.createRoute(movieId))
                }
            )
        }
        composable(
            route = Screen.Details.route,
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType }
            )
        ) {
            MovieDetailsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

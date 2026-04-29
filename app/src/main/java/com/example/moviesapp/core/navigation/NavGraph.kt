package com.example.moviesapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moviesapp.features.movies.domain.model.Movie
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
                onMovieClick = { movie ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("movie", movie)
                    navController.navigate(Screen.Details.route)
                }
            )
        }

        composable(route = Screen.Details.route) {
            val movie = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Movie>("movie")

            if (movie != null) {
                MovieDetailsScreen(
                    movie = movie,
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
}

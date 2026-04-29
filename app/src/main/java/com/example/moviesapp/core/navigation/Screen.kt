package com.example.moviesapp.core.navigation

sealed class Screen(val route: String) {
    object NowPlaying : Screen("now_playing")
    object Details : Screen("details")
}

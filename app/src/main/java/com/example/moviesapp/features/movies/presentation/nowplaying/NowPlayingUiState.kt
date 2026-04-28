package com.example.moviesapp.features.movies.presentation.nowplaying

import com.example.moviesapp.features.movies.domain.model.Movie

data class NowPlayingUiState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null
)

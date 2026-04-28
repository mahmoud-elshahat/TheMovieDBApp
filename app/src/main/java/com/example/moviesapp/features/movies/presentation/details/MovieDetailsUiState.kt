package com.example.moviesapp.features.movies.presentation.details

import com.example.moviesapp.features.movies.domain.model.Movie

sealed interface MovieDetailsUiState {
    object Loading : MovieDetailsUiState
    data class Success(val movie: Movie) : MovieDetailsUiState
    data class Error(val message: String) : MovieDetailsUiState
}

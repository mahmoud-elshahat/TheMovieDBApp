package com.example.moviesapp.features.movies.presentation.details

import androidx.annotation.StringRes
import com.example.moviesapp.features.movies.domain.model.Movie

sealed interface MovieDetailsUiState {
    data object Loading : MovieDetailsUiState
    data class Success(val movie: Movie) : MovieDetailsUiState
    data class Error(@StringRes val messageRes: Int) : MovieDetailsUiState
}

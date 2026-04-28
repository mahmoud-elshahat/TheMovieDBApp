package com.example.moviesapp.features.movies.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.features.movies.domain.usecase.GetMovieDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        val movieId: Int? = savedStateHandle["movieId"]
        if (movieId != null) {
            fetchMovieDetails(movieId)
        } else {
            _uiState.value = MovieDetailsUiState.Error("Movie ID not found")
        }
    }

    private fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = MovieDetailsUiState.Loading
            val movie = getMovieDetailsUseCase(movieId)
            if (movie != null) {
                _uiState.value = MovieDetailsUiState.Success(movie)
            } else {
                _uiState.value = MovieDetailsUiState.Error("Movie details not found")
            }
        }
    }
}

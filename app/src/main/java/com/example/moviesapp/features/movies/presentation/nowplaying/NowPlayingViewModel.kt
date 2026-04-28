package com.example.moviesapp.features.movies.presentation.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.features.movies.domain.usecase.GetMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<NowPlayingUiState> = combine(
        getMoviesUseCase(),
        _searchQuery,
        _isLoading,
        _errorMessage
    ) { movies, query, loading, error ->
        val filteredMovies = if (query.isBlank()) {
            movies
        } else {
            movies.filter { it.title.contains(query, ignoreCase = true) }
        }
        NowPlayingUiState(
            isLoading = loading,
            movies = filteredMovies,
            searchQuery = query,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NowPlayingUiState(isLoading = true)
    )

    init {
        fetchInitialMovies()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun fetchInitialMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = getMoviesUseCase.fetchInitialData()
            if (result.isFailure) {
                // Only show error if we have no cached data
                if (uiState.value.movies.isEmpty()) {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Unknown error"
                }
            }
            _isLoading.value = false
        }
    }
}

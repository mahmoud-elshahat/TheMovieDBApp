    package com.example.moviesapp.features.movies.domain.usecase

import com.example.moviesapp.features.movies.domain.model.Movie
import com.example.moviesapp.features.movies.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(): Flow<List<Movie>> {
        return repository.getNowPlayingMovies()
    }

    suspend fun fetchInitialData(): Result<Unit> {
        return repository.fetchAndCacheNowPlayingMovies(page = 1)
    }
}

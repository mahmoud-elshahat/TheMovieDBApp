package com.example.moviesapp.features.movies.domain.repository

import com.example.moviesapp.features.movies.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getNowPlayingMovies(): Flow<List<Movie>>
    suspend fun fetchAndCacheNowPlayingMovies(page: Int): Result<Unit>
    suspend fun getLastFetchedPage(): Int
    suspend fun clearCache()
}

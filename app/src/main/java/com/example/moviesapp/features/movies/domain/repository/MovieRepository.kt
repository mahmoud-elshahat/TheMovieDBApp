package com.example.moviesapp.features.movies.domain.repository

import androidx.paging.PagingData
import com.example.moviesapp.features.movies.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getNowPlayingMoviesPaged(): Flow<PagingData<Movie>>
    fun searchCachedMovies(query: String): Flow<PagingData<Movie>>
    suspend fun getMovieById(movieId: Int): Movie?
}

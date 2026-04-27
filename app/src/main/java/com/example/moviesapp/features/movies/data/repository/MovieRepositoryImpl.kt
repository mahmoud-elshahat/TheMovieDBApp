package com.example.moviesapp.features.movies.data.repository

import com.example.moviesapp.BuildConfig
import com.example.moviesapp.features.movies.data.local.MovieDao
import com.example.moviesapp.features.movies.data.mapper.toMovie
import com.example.moviesapp.features.movies.data.mapper.toMovieEntity
import com.example.moviesapp.features.movies.data.remote.MovieApiService
import com.example.moviesapp.features.movies.domain.model.Movie
import com.example.moviesapp.features.movies.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService,
    private val movieDao: MovieDao
) : MovieRepository {

    override fun getNowPlayingMovies(): Flow<List<Movie>> {
        return movieDao.getAllMovies().map { entities ->
            entities.map { it.toMovie() }
        }
    }

    override suspend fun fetchAndCacheNowPlayingMovies(page: Int): Result<Unit> {
        return try {
            val response = apiService.getNowPlaying(
                apiKey = BuildConfig.TMDB_API_KEY,
                page = page
            )
            val entities = response.results.map { it.toMovieEntity(page) }
            movieDao.insertMovies(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLastFetchedPage(): Int {
        return movieDao.getLastFetchedPage() ?: 0
    }

    override suspend fun clearCache() {
        movieDao.clearMovies()
    }
}

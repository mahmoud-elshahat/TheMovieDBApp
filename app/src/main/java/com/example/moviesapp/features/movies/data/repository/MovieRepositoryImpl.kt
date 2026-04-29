package com.example.moviesapp.features.movies.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.moviesapp.features.movies.data.local.MovieDao
import com.example.moviesapp.features.movies.data.mapper.toMovie
import com.example.moviesapp.features.movies.data.paging.MovieRemoteMediator
import com.example.moviesapp.features.movies.domain.model.Movie
import com.example.moviesapp.features.movies.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class MovieRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val remoteMediator: MovieRemoteMediator
) : MovieRepository {

    override fun getNowPlayingMoviesPaged(): Flow<PagingData<Movie>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = { movieDao.pagingSource() }
        ).flow.map { pagingData -> pagingData.map { it.toMovie() } }

    override fun searchCachedMovies(query: String): Flow<PagingData<Movie>> =
        movieDao.observeByTitle("%${query.trim()}%")
            .map { entities ->
                PagingData.from(entities.map { it.toMovie() })
            }

    private companion object {
        const val PAGE_SIZE = 20
    }
}

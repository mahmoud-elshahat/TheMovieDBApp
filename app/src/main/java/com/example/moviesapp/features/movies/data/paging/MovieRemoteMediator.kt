package com.example.moviesapp.features.movies.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.moviesapp.BuildConfig
import com.example.moviesapp.features.movies.data.local.AppDatabase
import com.example.moviesapp.features.movies.data.local.model.MovieEntity
import com.example.moviesapp.features.movies.data.mapper.toMovieEntity
import com.example.moviesapp.features.movies.data.remote.MovieApiService
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator @Inject constructor(
    private val database: AppDatabase,
    private val apiService: MovieApiService
) : RemoteMediator<Int, MovieEntity>() {

    private val movieDao = database.movieDao()

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> STARTING_PAGE
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> (movieDao.getMaxPage() ?: STARTING_PAGE) + 1
        }

        return try {
            val response = apiService.getNowPlaying(BuildConfig.TMDB_API_KEY, page)

            database.withTransaction {
                if (loadType == LoadType.REFRESH) movieDao.clearAll()
                movieDao.insertMovies(response.results.map { it.toMovieEntity(page) })
            }

            MediatorResult.Success(endOfPaginationReached = page >= response.totalPages)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private companion object {
        const val STARTING_PAGE = 1
    }
}

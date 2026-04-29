package com.example.moviesapp.core.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moviesapp.BuildConfig
import com.example.moviesapp.core.cache.CachePreferences
import com.example.moviesapp.features.movies.data.local.MovieDao
import com.example.moviesapp.features.movies.data.mapper.toMovieEntity
import com.example.moviesapp.features.movies.data.remote.MovieApiService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import java.io.IOException

@HiltWorker
class CacheSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: MovieApiService,
    private val movieDao: MovieDao,
    private val prefs: CachePreferences
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val probe = apiService.getNowPlaying(BuildConfig.TMDB_API_KEY, page = 1)
            val now = System.currentTimeMillis()

            val invalidated = if (shouldInvalidate(probe.totalResults, now)) {
                movieDao.clearAll()
                prefs.lastFullRefreshAt = now
                true
            } else {
                false
            }
            prefs.lastTotalResults = probe.totalResults

            val startPage = if (invalidated) {
                movieDao.insertMovies(probe.results.map { it.toMovieEntity(1) })
                2
            } else {
                (movieDao.getMaxPage() ?: 0) + 1
            }

            var currentPage = startPage
            var totalPages = probe.totalPages

            while (currentPage <= totalPages) {
                val response = apiService.getNowPlaying(BuildConfig.TMDB_API_KEY, page = currentPage)
                movieDao.insertMovies(response.results.map { it.toMovieEntity(currentPage) })
                totalPages = response.totalPages
                currentPage++
            }

            Result.success()

        } catch (_: IOException) {
            Result.retry()
        } catch (e: HttpException) {
            if (e.code() in 500..599) Result.retry() else Result.failure()
        } catch (_: Exception) {
            Result.failure()
        }
    }

    private fun shouldInvalidate(currentTotal: Int, now: Long): Boolean {
        val totalsChanged = prefs.hasObservedTotal() && prefs.lastTotalResults != currentTotal
        val cacheExpired = now - prefs.lastFullRefreshAt >= REFRESH_INTERVAL_MS
        return totalsChanged || cacheExpired
    }

    private companion object {
        const val REFRESH_INTERVAL_MS = 24L * 60L * 60L * 1000L
    }
}

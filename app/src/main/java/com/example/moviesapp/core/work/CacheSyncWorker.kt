package com.example.moviesapp.core.work

import android.content.Context
import android.util.Log
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
import java.util.concurrent.TimeUnit

@HiltWorker
class CacheSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: MovieApiService,
    private val movieDao: MovieDao,
    private val prefs: CachePreferences
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Worker started")
        return try {
            // Step 1 — probe page 1 to read total_results and total_pages
            Log.d(TAG, "Probing page 1 to check total_results...")
            val probe = apiService.getNowPlaying(BuildConfig.TMDB_API_KEY, page = 1)
            val now = System.currentTimeMillis()
            Log.d(TAG, "Page 1 fetched — totalResults=${probe.totalResults}, totalPages=${probe.totalPages}")

            // Step 2 — decide whether to invalidate the cache
            val invalidated = if (shouldInvalidate(probe.totalResults, now)) {
                Log.d(TAG, "Cache invalidated — clearing DB")
                movieDao.clearAll()
                prefs.lastFullRefreshAt = now
                true
            } else {
                Log.d(TAG, "Cache still valid — skipping invalidation")
                false
            }
            prefs.lastTotalResults = probe.totalResults

            // Step 3 — proactively fetch all pages
            val startPage = if (invalidated) {
                Log.d(TAG, "Inserting page 1 from probe response")
                movieDao.insertMovies(probe.results.map { it.toMovieEntity(1) })
                2
            } else {
                val maxPage = (movieDao.getMaxPage() ?: 0) + 1
                Log.d(TAG, "Resuming from page $maxPage")
                maxPage
            }

            var currentPage = startPage
            var totalPages = probe.totalPages

            if (currentPage > totalPages) {
                Log.d(TAG, "Cache already complete — nothing to fetch")
            }

            while (currentPage <= totalPages) {
                Log.d(TAG, "Fetching page $currentPage / $totalPages")
                val response = apiService.getNowPlaying(BuildConfig.TMDB_API_KEY, page = currentPage)
                movieDao.insertMovies(response.results.map { it.toMovieEntity(currentPage) })
                totalPages = response.totalPages
                currentPage++
            }

            val totalMovies = movieDao.getMaxPage()
            Log.d(TAG, "Worker finished successfully — max page in DB: $totalMovies")
            Result.success()

        } catch (e: IOException) {
            Log.e(TAG, "Network failure on — retrying. Error: ${e.message}")
            Result.retry()
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error ${e.code()} — retrying. Error: ${e.message}")
            Result.retry()
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error — retrying. Error: ${e.message}", e)
            Result.retry()
        }
    }

    private fun shouldInvalidate(currentTotal: Int, now: Long): Boolean {
        val totalsChanged = prefs.hasObservedTotal() && prefs.lastTotalResults != currentTotal
        val cacheExpired = now - prefs.lastFullRefreshAt >= REFRESH_INTERVAL_MS
        Log.d(TAG, "shouldInvalidate — totalsChanged=$totalsChanged, cacheExpired=$cacheExpired")
        return totalsChanged || cacheExpired
    }

    private companion object {
        const val TAG = "CacheSyncWorker"
        val REFRESH_INTERVAL_MS = TimeUnit.HOURS.toMillis(24)
    }
}

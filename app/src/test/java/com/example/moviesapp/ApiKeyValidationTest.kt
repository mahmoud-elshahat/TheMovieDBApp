package com.example.moviesapp

import com.example.moviesapp.features.movies.data.remote.MovieApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiKeyValidationTest {

    private val apiService: MovieApiService =
        Retrofit.Builder().baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(MovieApiService::class.java)

    @Test
    fun apiKey_isValid_whenRequestSucceeds() = runTest {
        val response = apiService.getNowPlaying(
            apiKey = BuildConfig.TMDB_API_KEY, page = 1
        )

        assertTrue(
            response.results.isNotEmpty()
        )
        assertTrue(
            response.totalPages > 0
        )
    }
}

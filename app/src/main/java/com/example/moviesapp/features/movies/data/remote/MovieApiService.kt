package com.example.moviesapp.features.movies.data.remote

import com.example.moviesapp.features.movies.data.remote.model.NowPlayingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {
    @GET("movie/now_playing")
    suspend fun getNowPlaying(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): NowPlayingResponse

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
    }
}

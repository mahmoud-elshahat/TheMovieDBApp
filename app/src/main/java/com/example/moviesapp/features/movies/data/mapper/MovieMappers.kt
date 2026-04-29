package com.example.moviesapp.features.movies.data.mapper

import com.example.moviesapp.features.movies.data.local.model.MovieEntity
import com.example.moviesapp.features.movies.data.remote.model.MovieDto
import com.example.moviesapp.features.movies.domain.model.Movie

private const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500"
private const val BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w780"

fun MovieDto.toMovieEntity(page: Int): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        page = page
    )
}

fun MovieEntity.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterPath?.let { "$POSTER_BASE_URL$it" },
        backdropUrl = backdropPath?.let { "$BACKDROP_BASE_URL$it" },
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

package com.example.moviesapp.features.movies.domain.model

import java.io.Serializable

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int
) : Serializable

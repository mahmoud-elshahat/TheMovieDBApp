package com.example.moviesapp.features.movies.domain.usecase

import com.example.moviesapp.features.movies.domain.model.Movie
import com.example.moviesapp.features.movies.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Movie? {
        return repository.getMovieById(movieId)
    }
}

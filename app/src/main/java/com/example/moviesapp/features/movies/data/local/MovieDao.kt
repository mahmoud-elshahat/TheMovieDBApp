package com.example.moviesapp.features.movies.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviesapp.features.movies.data.local.model.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies ORDER BY page ASC, id ASC")
    fun pagingSource(): PagingSource<Int, MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: Int): MovieEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("DELETE FROM movies")
    suspend fun clearAll()

    @Query("SELECT MAX(page) FROM movies")
    suspend fun getMaxPage(): Int?

    @Query("SELECT * FROM movies WHERE title LIKE :query ORDER BY page ASC, id ASC")
    fun observeByTitle(query: String): Flow<List<MovieEntity>>
}

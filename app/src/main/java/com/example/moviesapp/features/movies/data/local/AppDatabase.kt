package com.example.moviesapp.features.movies.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.moviesapp.features.movies.data.local.model.MovieEntity

@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}

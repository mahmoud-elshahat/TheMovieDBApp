package com.example.moviesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.moviesapp.core.navigation.NavGraph
import com.example.moviesapp.core.work.CacheSyncWorker
import com.example.moviesapp.ui.theme.MoviesAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (BuildConfig.DEBUG) {
            triggerWorkerForTesting()
        }

        setContent {
            MoviesAppTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }

    private fun triggerWorkerForTesting() {
        val request = OneTimeWorkRequestBuilder<CacheSyncWorker>().build()
        WorkManager.getInstance(this).enqueue(request)
    }
}

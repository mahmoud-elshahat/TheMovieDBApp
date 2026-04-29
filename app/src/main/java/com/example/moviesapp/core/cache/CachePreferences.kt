package com.example.moviesapp.core.cache

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachePreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var lastTotalResults: Int
        get() = prefs.getInt(KEY_TOTAL_RESULTS, UNSET_TOTAL)
        set(value) = prefs.edit { putInt(KEY_TOTAL_RESULTS, value) }

    var lastFullRefreshAt: Long
        get() = prefs.getLong(KEY_LAST_REFRESH, 0L)
        set(value) = prefs.edit { putLong(KEY_LAST_REFRESH, value) }

    fun hasObservedTotal(): Boolean = lastTotalResults != UNSET_TOTAL

    private companion object {
        const val PREFS_NAME = "cache_prefs"
        const val KEY_TOTAL_RESULTS = "last_total_results"
        const val KEY_LAST_REFRESH = "last_refresh_at"
        const val UNSET_TOTAL = -1
    }
}

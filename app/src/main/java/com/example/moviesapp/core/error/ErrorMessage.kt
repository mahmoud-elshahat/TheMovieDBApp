package com.example.moviesapp.core.error

import androidx.annotation.StringRes
import com.example.moviesapp.R
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@StringRes
fun Throwable.toUserMessageRes(): Int = when (this) {
    is UnknownHostException, is ConnectException, is SocketTimeoutException -> R.string.error_no_connection

    is IOException -> R.string.error_no_connection
    is HttpException -> R.string.error_server
    else -> R.string.error_generic
}

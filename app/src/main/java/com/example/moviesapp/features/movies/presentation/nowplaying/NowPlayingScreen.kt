package com.example.moviesapp.features.movies.presentation.nowplaying

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.moviesapp.R
import com.example.moviesapp.core.error.toUserMessageRes
import com.example.moviesapp.features.movies.domain.model.Movie
import com.example.moviesapp.features.movies.presentation.components.MovieItem

private const val GRID_COLUMNS = 2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: NowPlayingViewModel = hiltViewModel()
) {
    val movies = viewModel.movies.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.now_playing_title)) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = { Text(stringResource(R.string.search_movies_hint)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            MoviesPagingGrid(
                movies = movies,
                onMovieClick = onMovieClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun MoviesPagingGrid(
    movies: LazyPagingItems<Movie>,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    when (val refresh = movies.loadState.refresh) {
        is LoadState.Loading -> FullScreenLoader(modifier)
        is LoadState.Error -> FullScreenError(
            message = stringResource(refresh.error.toUserMessageRes()),
            onRetry = movies::retry,
            modifier = modifier
        )
        else -> LazyVerticalGrid(
            columns = GridCells.Fixed(GRID_COLUMNS),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
        ) {
            items(
                count = movies.itemCount,
                key = movies.itemKey { it.id }
            ) { index ->
                movies[index]?.let { movie ->
                    MovieItem(movie = movie, onMovieClick = onMovieClick)
                }
            }
            appendLoadStateItem(movies)
        }
    }
}

private fun LazyGridScope.appendLoadStateItem(movies: LazyPagingItems<Movie>) {
    when (val append = movies.loadState.append) {
        is LoadState.Loading -> fullSpanItem { InlineLoader() }
        is LoadState.Error -> fullSpanItem {
            InlineError(
                message = stringResource(append.error.toUserMessageRes()),
                onRetry = movies::retry
            )
        }
        else -> Unit
    }
}

private fun LazyGridScope.fullSpanItem(content: @Composable () -> Unit) {
    item(span = { GridItemSpan(GRID_COLUMNS) }) { content() }
}

@Composable
private fun FullScreenLoader(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun FullScreenError(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.padding(16.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = message, color = MaterialTheme.colorScheme.error)
            Button(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
        }
    }
}

@Composable
private fun InlineLoader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator() }
}

@Composable
private fun InlineError(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = message, color = MaterialTheme.colorScheme.error)
            Button(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
        }
    }
}

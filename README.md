# 🎬 MoviesApp

An Android application that displays now-playing movies from [The Movie Database (TMDB)](https://www.themoviedb.org/) API. Built with a focus on clean code, offline-first architecture, and modern Android development practices.

---

## Features

- Browse currently playing movies in a responsive 2-column grid
- Search movies by title with debounced live filtering
- View movie details including backdrop, rating, vote count, release date, and overview
- Full offline support — cached data is available without a network connection
- Background cache sync every 6 hours via WorkManager
- Smart cache invalidation when the total movie count changes

---

## Architecture

The project follows **Clean Architecture** principles combined with **MVVM**, organized in a **feature-based** package structure.

### Layer responsibilities

```
Presentation  →  Domain  ←  Data
```

| Layer | Responsibility |
|---|---|
| **Presentation** | Jetpack Compose UI, ViewModels, UI state |
| **Domain** | Business models, repository interfaces, use cases |
| **Data** | Repository implementations, Room database, Retrofit API, mappers |

The dependency rule is strictly enforced — inner layers have no knowledge of outer layers. The `Domain` layer is pure Kotlin with no Android or framework dependencies.

### Feature-based structure

```
com.example.moviesapp
│
├── core/
│   ├── cache/          # CachePreferences (SharedPreferences wrapper)
│   ├── error/          # Error → string resource mapping
│   ├── navigation/     # NavGraph, Screen routes
│   └── work/           # CacheSyncWorker, CacheWorkScheduler
│
├── di/
│   ├── DatabaseModule
│   ├── NetworkModule
│   └── RepositoryModule
│
├── features/
│   └── movies/
│       ├── data/
│       │   ├── local/          # Room database, DAO, MovieEntity
│       │   ├── mapper/         # DTO ↔ Entity ↔ Domain mappers
│       │   ├── paging/         # MovieRemoteMediator (Paging 3)
│       │   ├── remote/         # Retrofit API service, DTOs
│       │   └── repository/     # MovieRepositoryImpl
│       │
│       ├── domain/
│       │   ├── model/          # Movie (domain model)
│       │   └── repository/     # MovieRepository interface
│       │
│       └── presentation/
│           ├── components/     # MovieItem composable
│           ├── details/        # MovieDetailsScreen
│           └── nowplaying/     # NowPlayingScreen, NowPlayingViewModel
│
├── ui/theme/                   # Color, Typography, Theme
├── MainActivity.kt
└── MoviesApplication.kt
```

### Caching strategy

The app uses a **RemoteMediator + Room** setup from Paging 3 to implement an offline-first approach:

1. On first launch, `MovieRemoteMediator` fetches movies from TMDB and stores them in Room.
2. All subsequent UI reads come from the local database — the network is only hit when more pages are needed or a refresh is triggered.
3. A periodic `CacheSyncWorker` runs every **6 hours** (when the device is connected and battery is not low) to refresh the cache in the background.
4. The cache is invalidated and rebuilt from scratch when:
   - The total movie count on TMDB changes (new or removed titles).
   - 24 hours have passed since the last full refresh.

---

## Tech Stack

| Category | Library | Version |
|---|---|---|
| UI | Jetpack Compose + Material 3 | BOM 2024.10.01 |
| Architecture | ViewModel + StateFlow | Lifecycle 2.10.0 |
| DI | Hilt | 2.51.1 |
| Networking | Retrofit + OkHttp | 2.11.0 / 4.12.0 |
| JSON | Gson | bundled with Retrofit |
| Local DB | Room | 2.6.1 |
| Paging | Paging 3 + Room integration | 3.4.2 |
| Background work | WorkManager + Hilt integration | 2.11.2 / 1.2.0 |
| Image loading | Coil | 2.7.0 |
| Navigation | Navigation Compose | 2.8.3 |
| Language | Kotlin | 2.0.21 |
| Build | KSP | 2.0.21-1.0.25 |

---

## API Key Security

The TMDB API key is **never hardcoded** in source files or committed to version control.

It is injected at build time via `BuildConfig` using a property defined in your local `local.properties` file, which is excluded from Git via `.gitignore`.

### How it works

**`local.properties`** (not committed):
```properties
TMDB_API_KEY=your_api_key_here
```

**`app/build.gradle.kts`**:
```kotlin
val tmdbApiKey = properties["TMDB_API_KEY"]?.toString() ?: ""
buildConfigField("String", "TMDB_API_KEY", "\"$tmdbApiKey\"")
```

**Usage in code**:
```kotlin
apiService.getNowPlaying(BuildConfig.TMDB_API_KEY, page = 1)
```

The key is embedded in the compiled binary at build time and never appears in any tracked source file.

> ⚠️ Make sure `local.properties` is listed in your `.gitignore` before pushing — Android Studio adds it by default.

### Network logging

HTTP request/response logging is only enabled in **debug builds**:

```kotlin
level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE
```

No sensitive data or API responses are logged in production.

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17+
- A free TMDB API key — register at [themoviedb.org](https://www.themoviedb.org/settings/api)

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/MoviesApp.git
   cd MoviesApp
   ```

2. Add your API key to `local.properties` (create the file if it doesn't exist):
   ```properties
   TMDB_API_KEY=your_api_key_here
   ```

3. Open the project in Android Studio and let Gradle sync.

4. Run on a device or emulator — API level 26 (Android 8.0) or higher.

---

## License

This project is for educational and portfolio purposes.

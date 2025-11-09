# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Flovatar is an Android mobile application for managing Flow blockchain NFT avatars. The app includes avatar browsing, mini-games (including a "Where's Waldo" style game), leaderboards, and Flow blockchain authentication via the FCL (Flow Client Library) module.

**Package:** `com.flovatar.mobileapp`
**Min SDK:** 21
**Target SDK:** 31
**Language:** Kotlin

## Project Structure

This is a multi-module Gradle project:

- **app**: Main Android application module
  - **activity**: Activities for different screens (Login, Main, Games, Leaderboard, etc.)
  - **fragment**: Reusable fragments (LeaderBoard, etc.)
  - **viewmodel**: ViewModels following MVVM pattern (AvatarsListViewModel, WaldoViewModel, LeaderboardViewModel)
  - **adapter**: RecyclerView adapters for avatar lists
  - **model**: Data models (AvatarModel, LeaderboardItem, etc.)
  - **api**: Retrofit API service and client
  - **utils**: Utility classes (PrefUtils, AvatarUtils)
  - **eventbus**: EventBus events for app-wide communication (LogoutEvent, SaveListEvent, HideProgressEvent)
  - **view**: Custom views and UI components
  - **dialogs**: Custom dialog implementations

- **fcl**: Flow Client Library Android module (authentication with Flow blockchain)
  - Custom library for Flow blockchain authentication
  - Supports multiple providers (Dapper, Blocto, etc.)
  - Uses Custom Tabs for authentication flows

## Key Technologies

- **Architecture**: MVVM with ViewModels and LiveData
- **Networking**: Retrofit with RxJava3 for API calls
- **Image Loading**: Glide and Coil (for SVG support)
- **DI Pattern**: Lazy initialization and companion object singletons
- **Event Bus**: GreenRobot EventBus for inter-component communication
- **Navigation**: Android Navigation Component with fragment-ktx
- **UI**: ViewBinding enabled, Material Design, Shimmer loading effects
- **Analytics**: Firebase Crashlytics and Analytics

## API Configuration

The app connects to Flovatar backend services:
- **Production**: `https://flovatar.com/`
- **Staging**: `https://test.flovatar.com/`

API client is configured in `app/src/main/java/com/flovatar/mobileapp/api/RetrofitClient.kt` with three builder methods:
- `buildService()`: Production with Gson converter
- `buildServiceNoConverter()`: Production without converter (for raw responses)
- `buildStageService()`: Staging environment

## Common Development Commands

### Build and Run
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug build on connected device
./gradlew installDebug
```

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run tests for specific module
./gradlew :app:test
./gradlew :fcl:test
```

### Code Quality
```bash
# Run lint checks
./gradlew lint

# Generate lint report
./gradlew lintDebug
```

### Module-Specific Commands
```bash
# Build only the FCL library
./gradlew :fcl:build

# Build only the app module
./gradlew :app:build
```

## Important Architecture Notes

### MVVM Pattern
- Activities/Fragments observe ViewModels via LiveData
- ViewModels handle business logic and API calls using RxJava3
- Models are simple data classes for API responses and local state

### Flow Authentication (FCL Module)
- The FCL module handles Flow blockchain authentication
- Supports multiple wallet providers (Dapper, Blocto)
- Uses Custom Tabs for OAuth-like authentication flows
- Authentication state managed through polling mechanism

### Event-Driven Communication
- EventBus is used for cross-component communication
- Key events: `LogoutEvent`, `SaveListEvent`, `HideProgressEvent`
- Components subscribe with `@Subscribe` annotation
- Register/unregister in activity/fragment lifecycle methods

### Pagination Pattern
- `PaginationArgs` class tracks pagination state
- Custom scroll listeners (`CarouselRecyclerViewOnScrollListener`, `RecyclerViewOnScrollListener`) trigger data loading
- ViewModels handle incremental data fetching

### Base Classes
- `BaseActivity<VB : ViewBinding>`: All activities extend this for ViewBinding setup
- `BaseFragment`: Base class for fragments with common functionality

## Development Workflow

1. **Adding New Screens**: Create Activity in `activity/` package, declare in `AndroidManifest.xml`, create ViewModel if needed
2. **API Changes**: Update `ApiService.kt` interface, modify/add models in `model/`, update ViewModel to call new endpoint
3. **New Features**: Follow MVVM pattern - create Model, ViewModel, Activity/Fragment
4. **FCL Authentication**: Work in the `:fcl` module for blockchain-related changes

## Key Entry Points

- **Launch Activity**: `LoginActivity` (declared as MAIN launcher)
- **Main Screen**: `MainActivity` - displays avatar carousel and navigation
- **FCL Authentication**: `org.onflow.fcl.android.auth.FCL` class in fcl module

## Security Notes

**WARNING**: The `app/build.gradle` file contains hardcoded signing credentials (keystore password, key password). These should be moved to a secure location (e.g., `local.properties` or environment variables) before committing to public repositories.

# Todo App - Project Context

## Project Overview
Android TODO application built with Kotlin, Jetpack Compose, and Room database.
This project serves as a tutorial for using Claude Code with GitHub Actions on a self-hosted runner.

## Tech Stack
- **Language:** Kotlin 2.0
- **UI:** Jetpack Compose with Material 3
- **Database:** Room 2.6.1 with KSP
- **Architecture:** MVVM with manual dependency injection
- **Min SDK:** 26 (Android 8.0)
- **Target/Compile SDK:** 34

## Project Structure
```
app/src/main/java/com/example/todoapp/
├── TodoApp.kt                  # Application class (DI init)
├── MainActivity.kt             # Entry point
├── data/
│   ├── local/
│   │   ├── TodoEntity.kt       # Room entity
│   │   ├── TodoDao.kt          # Room DAO
│   │   └── TodoDatabase.kt     # Room database singleton
│   └── repository/
│       └── TodoRepository.kt   # Data repository
├── di/
│   └── AppContainer.kt         # Manual DI container
└── ui/
    ├── theme/                   # Material 3 theme
    └── todo/
        ├── TodoScreen.kt       # Main UI
        └── TodoViewModel.kt    # ViewModel with state
```

## Build Commands
- Build: `./gradlew assembleDebug`
- Test: `./gradlew testDebugUnitTest`
- Lint: `./gradlew lintDebug`
- Clean: `./gradlew clean`

## Code Conventions
- Kotlin coding conventions (https://kotlinlang.org/docs/coding-conventions.html)
- Compose functions are PascalCase
- State hoisting pattern: UI state flows down, events flow up
- Repository pattern for data access
- No Hilt/Dagger — manual DI via AppContainer
- KSP (not KAPT) for annotation processing

## Review Guidelines
- Check for proper coroutine usage (viewModelScope, suspend functions)
- Ensure Room queries use Flow for reactive updates
- Verify Compose recomposition efficiency (stable parameters, remember)
- Look for potential memory leaks (context references, coroutine scope)
- Check null safety and proper error handling

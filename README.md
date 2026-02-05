# WeatherApp
# Weather App

An Android weather app that shows current weather data using the OpenWeatherMap API. Built with Kotlin and Jetpack Compose.

## What it does

- Shows weather for your current location (asks for permission first)
- Search weather by city name
- Remembers your last search and loads it automatically next time
- Clean UI with Material Design 3

## Project Structure

The app uses Clean Architecture with three main layers:

```
app/
├── data/           # API calls, local storage, repositories
├── domain/         # Business logic (use cases, models)
├── presentation/   # UI (Compose screens, ViewModels)
└── di/             # Dependency injection setup
```

**Why this structure?** It keeps things organized and testable. The UI doesn't know about API details, and business logic doesn't care about Android framework stuff.

## Tech Stack

- **Kotlin** (main language) + **Java** (PreferencesHelper for demo purposes)
- **Jetpack Compose** for UI
- **Hilt** for dependency injection
- **Retrofit + OkHttp** for networking
- **Coroutines + Flow** for async operations
- **ViewModel + StateFlow** for state management
- **Coil** for loading weather icons
- **Room** (configured but not actively used yet)

**Testing:**
- JUnit, MockK, Turbine for unit tests
- Truth for assertions
- MockWebServer for API testing

## Setup

You'll need an API key from OpenWeatherMap (it's free).

1. Go to https://openweathermap.org/api and sign up
2. Get your API key
3. Open `local.properties` in the project root
4. Add this line:
   ```
   WEATHER_API_KEY=your_actual_key_here
   ```
5. Open in Android Studio and run

**Requirements:**
- Android Studio Hedgehog or newer
- JDK 11+
- Min SDK 24, Target SDK 36

## How it works

When you open the app:
1. It asks for location permission
2. If you allow it → fetches weather for your location
3. If you deny it → tries to load the last city you searched
4. You can search any city manually
5. Your last search gets saved automatically

The weather data shows temperature, conditions, humidity, and wind speed.

## Running Tests

```bash
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # UI tests
```

Tests cover use cases, ViewModel logic, and repository behavior.

## Architecture Patterns

- **MVVM**: ViewModel holds UI state, screen observes it
- **Repository Pattern**: Single source of truth for data
- **Use Cases**: Each feature has its own use case (single responsibility)
- **Unidirectional Data Flow**: State flows down, events flow up

## API Details

Uses OpenWeatherMap API v2.5:
- Search by city: `/weather?q={city}&appid={key}&units=metric`
- Search by coords: `/weather?lat={lat}&lon={lon}&appid={key}&units=metric`

The base URL is in `build.gradle.kts` and can be changed in `gradle.properties` if needed.

## Notes

- API key is injected at build time via BuildConfig
- OkHttp logging is enabled for debugging (turn it off for production)
- Location permission handling includes a fallback to last searched city
- Error messages show via Snackbar

## Files to check out

- `WeatherScreen.kt` - Main UI with permission flow
- `WeatherViewModel.kt` - State management
- `NetworkModule.kt` - Hilt DI setup
- `PreferencesHelper.java` - Saves last searched city (Java example)
- `GetWeatherByCityUseCase.kt` - Business logic for city search

---

Built with Kotlin and Jetpack Compose


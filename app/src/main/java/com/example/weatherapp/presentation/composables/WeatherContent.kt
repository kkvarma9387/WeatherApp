package com.example.weatherapp.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weatherapp.R
import com.example.weatherapp.domain.model.Weather

// Minimum length for city name to prevent single-character searches
private const val MIN_CITY_NAME_LENGTH = 2

/**
 * Main content composable for the weather screen.
 * 
 * Provides a search interface for city-based weather lookup and displays results.
 * Uses rememberSaveable to preserve state across configuration changes (rotation, etc.).
 * 
 * @param weather Current weather data to display, null if no search performed yet
 * @param onSearchCity Callback invoked when user searches for a city
 * @param modifier Modifier for customizing layout
 */
@Composable
fun WeatherContent(
    weather: Weather?,
    onSearchCity: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // State survives configuration changes (e.g., screen rotation)
    var city by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val errorEmptyCity = stringResource(R.string.error_empty_city)
    val errorMinLength = stringResource(R.string.error_min_length, MIN_CITY_NAME_LENGTH)
    val errorInvalidChars = stringResource(R.string.error_invalid_chars)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SearchSection(
            city = city,
            errorMessage = errorMessage,
            onCityChange = { newCity ->
                city = newCity
                // Clear error when user starts typing
                errorMessage = null
            },
            onSearchClick = {
                // Defensive validation before making API call
                val validationError = validateCityInput(
                    city = city,
                    errorEmptyCity = errorEmptyCity,
                    errorMinLength = errorMinLength,
                    errorInvalidChars = errorInvalidChars
                )

                if (validationError != null) {
                    errorMessage = validationError
                    keyboardController?.hide()
                } else {
                    errorMessage = null
                    keyboardController?.hide()
                    onSearchCity(city.trim())
                }
            }
        )

        // Only display weather card when data is available
        weather?.let {
            Spacer(modifier = Modifier.height(8.dp))
            WeatherDisplayCard(weather = it)
        }
    }
}

/**
 * Search input section with text field and search button.
 * 
 * Handles keyboard IME actions for better UX (user can press "Search" on keyboard).
 * Displays inline validation errors below the text field.
 */
@Composable
private fun SearchSection(
    city: String,
    errorMessage: String?,
    onCityChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.weather_search),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = city,
            onValueChange = onCityChange,
            label = { Text(stringResource(R.string.enter_us_city)) },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null,
            supportingText = errorMessage?.let {
                {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            // Keyboard action: hide keyboard and trigger search when user presses "Done"
            onDone = {
                keyboardController?.hide()
                onSearchClick()
            }
        )

        Button(
            onClick = onSearchClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.search_weather))
        }
    }
}

@Composable
private fun WeatherDisplayCard(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CityHeader(cityName = weather.city)

            WeatherIcon(iconUrl = weather.iconUrl)

            Temperature(temperature = weather.temperature)

            WeatherDescription(description = weather.description)
        }
    }
}

@Composable
private fun CityHeader(
    cityName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = cityName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun WeatherIcon(
    iconUrl: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = iconUrl,
        contentDescription = stringResource(R.string.weather_icon),
        modifier = modifier.size(120.dp)
    )
}

@Composable
private fun Temperature(
    temperature: Double,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.temperature_celsius, temperature),
        style = MaterialTheme.typography.displayLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
private fun WeatherDescription(
    description: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = description.capitalize(),
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

/**
 * Validates city input before making API request.
 * 
 * Defensive programming: Prevents unnecessary API calls with invalid input.
 * Provides user-friendly error messages for each validation failure.
 * 
 * @return Error message string if validation fails, null if input is valid
 */
private fun validateCityInput(
    city: String,
    errorEmptyCity: String,
    errorMinLength: String,
    errorInvalidChars: String
): String? {
    val trimmedCity = city.trim()

    return when {
        trimmedCity.isEmpty() -> errorEmptyCity
        trimmedCity.length < MIN_CITY_NAME_LENGTH -> errorMinLength
        // Only allow letters and spaces (prevents SQL injection, special char issues)
        !trimmedCity.all { it.isLetter() || it.isWhitespace() } -> errorInvalidChars
        else -> null
    }
}

private fun String.capitalize(): String {
    return this.replaceFirstChar { char ->
        if (char.isLowerCase()) char.titlecase() else char.toString()
    }
}

// Extension function to handle keyboard actions
@Composable
private fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null,
    singleLine: Boolean = false,
    onDone: () -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        isError = isError,
        supportingText = supportingText,
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onDone() }
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun WeatherContentPreview() {
    MaterialTheme {
        WeatherContent(
            weather = Weather(
                city = "New York",
                temperature = 22.5,
                description = "partly cloudy",
                iconUrl = "https://openweathermap.org/img/wn/02d@2x.png"
            ),
            onSearchCity = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherContentEmptyPreview() {
    MaterialTheme {
        WeatherContent(
            weather = null,
            onSearchCity = {}
        )
    }
}
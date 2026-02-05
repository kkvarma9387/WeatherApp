package com.example.weatherapp.di
import android.content.Context
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.local.PreferencesHelper
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Hilt module for providing network-related and data layer dependencies.
 * 
 * This module is installed in SingletonComponent to ensure dependencies
 * live for the entire application lifecycle, which is appropriate for:
 * - Network clients (expensive to create, should be reused)
 * - Repository instances (maintain consistent data state)
 * - SharedPreferences wrappers (single source of truth for preferences)
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    /**
     * Provides OkHttpClient with logging interceptor for debugging network requests.
     * 
     * The logging interceptor is set to BODY level to log complete request/response data.
     * In production builds, this should be disabled or set to NONE for performance and security.
     * 
     * @return Configured OkHttpClient instance
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    /**
     * Provides Retrofit instance configured for OpenWeatherMap API.
     * 
     * Uses Gson for JSON serialization/deserialization and the provided OkHttpClient.
     * Base URL is loaded from BuildConfig to keep API endpoints configurable.
     * 
     * @param client OkHttpClient instance for making network requests
     * @return Configured Retrofit instance
     */
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    /**
     * Provides WeatherApi interface implementation via Retrofit.
     * 
     * @param retrofit Retrofit instance for creating API service
     * @return WeatherApi implementation
     */
    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): WeatherApi =
        retrofit.create(WeatherApi::class.java)

    /**
     * Provides WeatherRepository implementation.
     * 
     * The repository pattern abstracts data sources from the domain layer,
     * making it easier to swap implementations or add caching layers later.
     * 
     * @param api WeatherApi for fetching weather data
     * @return WeatherRepository implementation
     */
    @Provides
    @Singleton
    fun provideRepo(api: WeatherApi): WeatherRepository =
        WeatherRepositoryImpl(api)
    
    /**
     * Provides PreferencesHelper for managing SharedPreferences operations.
     * 
     * This Java class handles persistence of the last searched city,
     * satisfying the requirement for Java+Kotlin combination and
     * enabling auto-load of last city on app launch.
     * 
     * @param context Application context for SharedPreferences access
     * @return PreferencesHelper instance
     */
    @Provides
    @Singleton
    fun providePreferencesHelper(@ApplicationContext context: Context): PreferencesHelper =
        PreferencesHelper(context)
}
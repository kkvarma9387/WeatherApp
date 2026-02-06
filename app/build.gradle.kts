import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.compose.compiler) // ✅ NEW required
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt) // enable when you want detekt
}

android {
    namespace = "com.example.weatherapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.weatherapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        val properties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")

        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }
        buildConfigField(
            "String",
            "BASE_URL",
            "\"${project.findProperty("BASE_URL") ?: "https://api.openweathermap.org/data/2.5/"}\""
        )

        buildConfigField(
            "String",
            "WEATHER_API_KEY",
            "\"${properties.getProperty("WEATHER_API_KEY") ?: ""}\""
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.room.ktx)
    implementation(libs.junit.ktx)
    implementation(libs.androidx.ui.test.junit4)
    implementation(libs.androidx.compose.ui.ui.test.junit4)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.play.services.location)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.coil.compose)
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

// Unit testing
    testImplementation(libs.junit)
    // testImplementation("io.mockk:mockk:1.13.5")
    testImplementation(libs.kotlinx.coroutines.test) // update to your coroutines version

    // Retrofit/OkHttp for integration tests (MockWebServer)
    testImplementation(libs.mockwebserver)

    // Android Instrumentation / Hilt / Compose UI tests
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.dagger.hilt.android.testing)

    // Jetpack Compose testing
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)

    // Hilt + testing
    testImplementation(libs.dagger.hilt.android.testing)

    implementation(libs.slf4j.slf4j.simple)
    testImplementation(libs.slf4j.slf4j.simple)
    testImplementation(kotlin("test"))
// For unit tests
    testImplementation(libs.google.truth)

// For instrumented (androidTest) tests
    androidTestImplementation(libs.google.truth)

    testImplementation(libs.mockk) // or latest available


    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
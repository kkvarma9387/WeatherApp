// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.sonarqube) apply false
  /*  alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false*/
}

/*sonarqube {
    properties {
        property("sonar.projectKey", "kkvarma9387_WeatherApp")
        property("sonar.organization", "kkvarma9387")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}*/

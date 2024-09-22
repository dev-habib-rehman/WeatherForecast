import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.navigation.safeargs.kotlin)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.devtools.ksp)
    id("kotlin-kapt")
}

val gradleProperties = Properties().apply {
    load(project.rootProject.file("gradle.properties").inputStream())
}

android {
    namespace = "com.example.weatherforecast"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
        dataBinding = true
    }

    defaultConfig {
        applicationId = "com.example.weatherforecast"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"http://api.weatherapi.com/\"")
            buildConfigField("String", "API_KEY", "\"${gradleProperties.getProperty("api_key")}\"")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"http://api.weatherapi.com/\"")
            buildConfigField("String", "API_KEY", "\"${gradleProperties.getProperty("api_key")}\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.org.mockito.core)
    testImplementation(libs.androidx.arch.core)
    testImplementation(libs.org.jetbrains.kotlinx.test)
    testImplementation(libs.dagger.hilt.android.testing)
    testImplementation (libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //navigation
    implementation(libs.androidx.navigation.fragemnt)
    implementation(libs.androidx.navigation.ui)

    // Hilt Dependency
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    // Kotlin Extensions and Coroutines support for Room
    implementation(libs.room.extension)
    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
}
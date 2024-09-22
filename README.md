# Weather Forecasting APP

This is a Kotlin-based Android application that integrates with a **Weather API** to provide users with weather forecasts. The application allow users to search for a city and view current weather conditions, along with a 5-day forecast.

## Key Features

- Fetches weather data from a remote API .
- Find the api key from https://www.weatherapi.com
- Stores weather data locally using Room Database.
- Allow users to search for weather information by city name.
- Display current weather conditions, including temperature, humidity, wind speed, and weather description.
- Display a 5-day weather forecast, with daily temperature highs and lows and weather conditions.
- Android jetpack
- Implements MVVM (Model-View-ViewModel) architecture.
- Follows Clean Architecture principles.
- Uses Hilt for Dependency Injection.
- Modular architecture with separate modules for different functionalities.
- Jetpack navigation component to navigate between screens.
- Unit testing 

**Clone the repository:**
git clone https://github.com/habi377/XischeAssesment.git

**About Environment**
- Developed on latest Android Studio Koala | 2024.1.1 Patch 2
- Build #AI-241.18034.62.2411.12169540, built on August 1, 2024
- Runtime version: 17.0.11+0-17.0.11b1207.24-11852314 aarch64
- gradle version = "8.5.2"
- kotlin = "2.0.20"

## Architecture

-**MVVM (Model-View-ViewModel**

-**Clean Architecture**
- Domain Layer: Contains the business logic and entity definitions(usecases).
- Data Layer: Manages data sources, including the Room database and remote API service.
- Presentation Layer: Handles UI components, ViewModels, and state management.

-**Unit Testing**
- clone the project navigate to test directory run the class with code coverage
- I have added multiple test cases for WeatherForecastRepositoryImpl class to test communication data to UI layer. 
- Its coverage is above 80%
- I have added multiple test cases for UI layer ViewModel class to test the data flow. 
- Its Its coverage is above 80%
- By testing above two major classes almost other main classes also get tested. 

-**Navigation Component**

-**Data Binding**

-**Flow API**

-**Room**

-**Kotlin DSL**

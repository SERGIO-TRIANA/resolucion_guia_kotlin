plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.misfinanzas"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.misfinanzas"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Retrofit: cliente HTTP para consumir APIs
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Converter Gson: convierte JSON a objetos Kotlin automáticamente
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Gson: librería de Google para parsear JSON
    implementation("com.google.code.gson:gson:2.10.1")
    // Coroutines Android: para ejecutar llamadas de red en segundo plano
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.constraintlayout)

    // Lifecycle & ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    // Fragment KTX: activityViewModels() para compartir ViewModel entre Fragments (Capítulo 10)
    implementation("androidx.fragment:fragment-ktx:1.7.1")

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    // Procesador de Room con KSP (más eficiente en Windows)
    ksp(libs.androidx.room.compiler)
    // Driver necesario para que Room genere el código en Windows
    ksp(libs.sqlite.jdbc)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

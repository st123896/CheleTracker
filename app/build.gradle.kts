plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.example.gnm18"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gnm18"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    //room
    val room_version = "2.7.1"

    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    // Lifecycle components
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // UI
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.android.material:material:1.9.0")

    // Image loading
    implementation ("com.github.bumptech.glide:glide:4.15.1")

    // Navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation ("androidx.navigation:navigation-ui-ktx:2.6.0")


    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

    // For charts
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // For camera and ML Kit
    implementation ("androidx.camera:camera-core:1.1.0")
    implementation ("androidx.camera:camera-lifecycle:1.1.0")
    implementation ("androidx.camera:camera-view:1.1.0")
    implementation ("com.google.mlkit:text-recognition:16.0.0")

    // For PDF generation
    implementation ("com.tom-roush:pdfbox-android:2.0.27.0")

    // For image loading
    implementation ("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.0")

    

}
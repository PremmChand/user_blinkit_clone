import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id ("kotlin-kapt")
}

//val razorpayKey: String = project.findProperty("RAZORPAY_SECRET_KEY") as? String ?: ""

val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.example.blinkitclone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.blinkitclone"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

       // buildConfigField("String", "RAZORPAY_SECRET_KEY", "\"$razorpayKey\"")

        buildConfigField("String", "RAZORPAY_SECRET_KEY", "\"${localProperties["RAZORPAY_SECRET_KEY"]}\"")

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
    buildFeatures{
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.lifecycle.runtime.android)
    implementation(libs.androidx.recyclerview)
    implementation(libs.play.services.analytics.impl)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("com.intuit.sdp:sdp-android:1.1.0")
    implementation ("com.intuit.ssp:ssp-android:1.1.0")

    implementation ("androidx.core:core-ktx:1.13.1")
    implementation ("androidx.appcompat:appcompat:1.6.1")

// ViewModel and LiveData (MVVM)
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

// Navigation Component
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")

// Retrofit (Networking)
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

// Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")
    implementation ("com.google.android.material:material:1.11.0")

//firebase
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-messaging")

    //Image slider
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")
    //shimmer effect
    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    // Room components
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    implementation ("androidx.room:room-paging:2.6.1")

    //glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    //PhonePe
    implementation ("phonepe.intentsdk.android.release:IntentSDK:2.4.3")

    //RazorPay
    implementation ("com.razorpay:checkout:1.6.33")

    //ohttp
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation ("com.google.firebase:firebase-functions-ktx")


}
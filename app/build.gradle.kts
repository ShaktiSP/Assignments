plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    kotlin("kapt")
    id ("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.authlistapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.authlistapp"
        minSdk = 24
        targetSdk = 34
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

    //Firebase Auth
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation(libs.firebase.auth)
    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    //MVVM
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    //RoomDB
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

    //Dagger-Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")

    //okHttp
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")
    implementation ("androidx.work:work-runtime-ktx:2.8.1")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")

}
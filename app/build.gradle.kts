plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp")
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin)
}

android {
    namespace = "com.dicoding.storyapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dicoding.storyapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://story-api.dicoding.dev/v1/\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // coroutines
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // data persistence
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // image utils
    implementation("androidx.exifinterface:exifinterface:1.3.7")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.play.services.maps)

    // cameraX
    val cameraxVersion = "1.3.3"
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // location
    implementation("com.google.android.gms:play-services-maps:18.0.0")
    implementation("com.google.android.gms:play-services-location:18.0.0")

    // paging
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    implementation("androidx.room:room-paging:2.6.1")

    // testing
    testImplementation(libs.junit)
    testImplementation("androidx.arch.core:core-testing:2.2.0") // InstantTaskExecutorRule
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3") //TestDispatcher
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito:mockito-inline:3.12.4")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3") //TestDispatcher
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.contrib)
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")//IntentsTestRule
    debugImplementation("androidx.fragment:fragment-testing:1.7.0")
}
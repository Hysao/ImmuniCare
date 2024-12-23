plugins {
    // alias(libs.plugins.android.application)
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.myprograms.immunicare"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.myprograms.immunicare"
        minSdk = 26
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.vision.common)
    implementation(libs.play.services.vision)
    implementation(libs.media3.common)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)



    // ML Kit (QR Code Scanning and Vision)
    implementation(libs.barcode.scanning.v1730)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)


    implementation (libs.play.services.auth)  // For Google authentication
    implementation (libs.google.firebase.auth)  // Firebase Authentication
    implementation (libs.google.firebase.firestore)

    // CameraX Dependencies (all CameraX dependencies with same version)
    implementation(libs.camera.core.v150)
    implementation(libs.camera.camera2.v150)
    implementation(libs.camera.lifecycle.v150)
    implementation(libs.camera.view.v150)
    implementation(libs.camera.extensions.v150)

    //qr generator
    implementation(libs.zxing.core)

    implementation(libs.material.v190)

    implementation(libs.firebase.storage)

    implementation(libs.threetenabp)



}
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

}

android {
    namespace = "com.orbits.queuesystem"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.orbits.queuesystem"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
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
    implementation(libs.websocket)
    implementation(libs.sdp)
    implementation(libs.ssp)
    implementation(libs.http)
    implementation(libs.socket)
    implementation(libs.datastore)
    implementation(libs.coilKtx)
    implementation(libs.coilGif)
    implementation(libs.coilBase)
    implementation(libs.gson)
    implementation(libs.multidex)
    implementation(libs.roomRuntime)
    annotationProcessor (libs.roomCompiler)
    implementation(libs.datastorePref)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
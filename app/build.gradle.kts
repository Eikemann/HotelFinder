plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.hotelapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.hotelapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Backend candidates. RetrofitHelper probes LOCAL first (emulator loopback
        // → host Docker backend on :8080) and falls back to REMOTE (hosted Railway,
        // stale NYC data) if the local one is unreachable. For a physical device,
        // change LOCAL to the host's LAN IP.honest-friendship-production-3c96.up.railway.app
        buildConfigField("String", "LOCAL_BASE_URL", "\"http://10.0.2.2:8081/api/\"")
        buildConfigField("String", "REMOTE_BASE_URL", "\"https://honest-friendship-production-3c96.up.railway.app/api/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false                      // keep R8 off so Retrofit/Gson DTOs aren't stripped
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
        // Opt in project-wide to the experimental Compose layout APIs (FlowRow /
        // FlowColumn) used in DetailScreen and FilterBottomSheet, so a clean
        // release compile doesn't fail on the missing opt-in.
        freeCompilerArgs += "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    coreLibraryDesugaring(libs.desugar.jdk)

    implementation(libs.coil.compose)

    implementation(libs.sheets.core)
    implementation(libs.sheets.calendar)

    implementation(libs.navigation.compose)

    implementation(libs.androidx.datastore.preferences)

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

}
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    namespace = "com.example.vpnstarter"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.falconvpn.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 34
        versionName = "0.27.0"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Sing-box Android engine
    implementation("com.github.singbox-android:libbox:1.14.0")

    // Google Ads - compatibility test
    implementation("com.google.android.gms:play-services-ads:23.4.0")

    // User Messaging Platform
    implementation("com.google.android.ump:user-messaging-platform:4.0.0")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Testing
    testImplementation("junit:junit:4.13.2")
}
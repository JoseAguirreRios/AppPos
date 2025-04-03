plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.elzarapeimports.zarapeimports"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.elzarapeimports.zarapeimports"
        minSdk = 30
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
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    
    // Cámara y escaneo de códigos - dejamos comentado ya que no lo usamos por ahora
    // implementation("androidx.camera:camera-camera2:1.3.0")
    // implementation("androidx.camera:camera-lifecycle:1.3.0")
    // implementation("androidx.camera:camera-view:1.3.0")
    // implementation("com.google.mlkit:barcode-scanning:17.2.0")
    
    // Permisos - dejamos comentado ya que no lo usamos por ahora
    // implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    
    // Formato de números y fechas
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
// Uygulama modülü — DEĞİŞTİRME (AGENTS.md §1.4, §1.5)
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.atasoy.eymentelefon"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.atasoy.eymentelefon"
        minSdk = 29
        targetSdk = 35
        versionCode = 4
        versionName = "1.3.0"
    }

    // V13-01: kalıcı imza. CI, GitHub secret EYMEN_IMZA'dan anahtarı EYMEN_KEYSTORE yoluna açar.
    // Anahtar yoksa (yerel derleme / secret eklenmemiş) debug anahtarı kullanılır.
    val imzaYolu = System.getenv("EYMEN_KEYSTORE")?.takeIf { file(it).exists() }
    signingConfigs {
        if (imzaYolu != null) {
            create("eymen") {
                storeFile = file(imzaYolu)
                storePassword = System.getenv("EYMEN_KEYSTORE_SIFRE")
                keyAlias = "eymen"
                keyPassword = System.getenv("EYMEN_KEYSTORE_SIFRE")
            }
        }
    }

    buildTypes {
        debug {
            if (imzaYolu != null) signingConfig = signingConfigs.getByName("eymen")
        }
        release {
            signingConfig = signingConfigs.getByName(if (imzaYolu != null) "eymen" else "debug")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    lint {
        abortOnError = true
        warningsAsErrors = false
        checkReleaseBuilds = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    // v1.3 (kullanıcı onaylı)
    implementation(libs.haze)
    implementation(libs.androidx.palette)
    implementation(libs.androidx.glance.appwidget)
    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit)
}

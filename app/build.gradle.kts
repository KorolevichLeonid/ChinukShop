// build.gradle (уровня приложения)
plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Плагин Google Services
    alias(libs.plugins.kotlin.android) // Плагин Kotlin
}

android {
    namespace = "com.kfnfnjlvdrngjrjkn.myshop"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kfnfnjlvdrngjrjkn.myshop"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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

    buildFeatures {
        viewBinding = true
        compose = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material) // Material Components
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase BoM для управления версиями Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))

    // Зависимости для Firebase продуктов
    implementation("com.google.firebase:firebase-analytics") // Firebase Analytics
    implementation("com.google.firebase:firebase-auth") // Firebase Authentication
    implementation("com.google.firebase:firebase-storage") // Firebase Storage
    implementation(libs.firebase.database) // Firebase Database
    implementation(libs.lifecycle.runtime.ktx) // Lifecycle KTX

    // PaperDB для локального хранения
    implementation("io.github.pilgr:paperdb:2.7.2")



    // Навигация
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx) // Material3

    implementation ("com.squareup.picasso:picasso:2.71828")

    implementation ("de.hdodenhof:circleimageview:3.1.0")






    // Тестовые зависимости
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Для Compose тестирования
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}
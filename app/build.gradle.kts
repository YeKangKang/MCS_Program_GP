plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("../key.keystore")
            storePassword = "sensor123"
            keyAlias = "sensor"
            keyPassword = "sensor123"
        }
        create("release") {
            storeFile = file("../key.keystore")
            storePassword = "sensor123"
            keyAlias = "sensor"
            keyPassword = "sensor123"
        }
    }
    namespace = "com.example.sensor"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sensor"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        javaCompileOptions {
            annotationProcessorOptions {
                argument("includeCompileClasspath", "true")
            }
        }
        ndk {
            // 设置支持的SO库架构（开发者可以根据需要，选择一个或多个平台的so）
//            "armeabi" "armeabi-v7a", "arm64-v8a", "x86","x86_64"
            abiFilters += listOf<String>("armeabi-v7a", "arm64-v8a")
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            signingConfig = signingConfigs.getByName("debug")
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
        dataBinding = true
    }

    sourceSets.getByName("main") {
        jniLibs.setSrcDirs(listOf("libs"))
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.4")
    implementation("com.airbnb.android:lottie:6.1.0")

    val room_version = "2.6.0"
    implementation("androidx.room:room-runtime:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-rxjava2:$room_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // baidu map
    implementation (files("libs/BaiduLBS_Android.aar"))   //地图组件

    // permission
    implementation("com.guolindev.permissionx:permissionx:1.7.1")
}
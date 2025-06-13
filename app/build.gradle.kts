plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.raite.crcc.systemui"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.raite.crcc.systemui"
        minSdk = 30
        versionCode = rootProject.ext["autoVersionCode"] as Int
        versionName = rootProject.ext["autoVersionName"] as String
        ndk {
            abiFilters += listOf("arm64-v8a")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("rail_platform") {
            // 唐车平台系统签名
            storeFile = file("../keystore/rail_platform.keystore")
            storePassword = "android"
            keyAlias = "android"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug {
            // debug 构建使用默认的调试签名，不指定 signingConfig
            manifestPlaceholders["sharedUserId"] = ""
        }
        release {
            signingConfig = signingConfigs["rail_platform"]
            manifestPlaceholders["sharedUserId"] = "android.uid.system"
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    compileOnly(files("libs/android.car.jar"))
    compileOnly(files("libs/raite.car.jar"))
    compileOnly(files("libs/framework.jar"))
    compileOnly(files("libs/android.hardware.automotive.vehicle-V2.0-java.jar"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.appcontext)
    implementation(libs.flexbox)
    implementation(libs.crcc.common)
    implementation(libs.crcc.cluster)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("io.github.cymchad:BaseRecyclerViewAdapterHelper:4.0.0-beta14")
    implementation("org.greenrobot:eventbus:3.3.1")

    // Coil - Coroutine Image Loader
    implementation("io.coil-kt:coil:2.5.0")

    testImplementation("org.mockito:mockito-core:5.4.0")
    testImplementation("org.mockito:mockito-android:5.4.0")
    androidTestImplementation("androidx.test:runner:1.6.0-alpha06")
}
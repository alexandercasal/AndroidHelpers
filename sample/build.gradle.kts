import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

val appVersionCode = 1
val appVersionName = "1.0"

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

android {
    compileSdkVersion(AndroidSdk.COMPILE)

    defaultConfig {
        applicationId = "com.alexandercasal.androidhelpers.sample"
        minSdkVersion (AndroidSdk.MIN)
        targetSdkVersion(AndroidSdk.TARGET)

        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(project(":library"))
    
    implementation(Deps.KOTLIN_STD_LIB)
    implementation(Deps.ANDROIDX_APP_COMPAT)
    implementation(Deps.ANDROIDX_CONSTRAINT_LAYOUT)
    implementation(Deps.ARCH_LIFECYCLE_EXT)
    testImplementation(Deps.TEST_JUNIT)
    androidTestImplementation(Deps.TEST_RUNNER)
    androidTestImplementation(Deps.TEST_ESPRESSO)
}

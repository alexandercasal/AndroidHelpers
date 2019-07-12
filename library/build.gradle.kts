plugins {
    id("com.android.library")
    kotlin("android")
    id("com.github.dcendents.android-maven")
}

group = "com.github.alexandercasal"

val appVersionCode = 11
val appVersionName = "1.1.1"

android {
    compileSdkVersion(AndroidSdk.COMPILE)

    defaultConfig {
        minSdkVersion(AndroidSdk.MIN)
        targetSdkVersion(AndroidSdk.TARGET)

        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to  "libs", "include" to listOf("*.jar"))))

    implementation(Deps.KOTLIN_STD_LIB)

    compileOnly(Deps.ARCH_LIFECYCLE_LIVEDATA)

    compileOnly(Deps.THREETEN_ABP)
    implementation(Deps.TIMBER)

    // Testing
    testImplementation(Deps.TEST_JUNIT)
}

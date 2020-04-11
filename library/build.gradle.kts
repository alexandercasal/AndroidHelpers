import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.github.dcendents.android-maven")
}

group = "com.github.alexandercasal"

val appVersionCode = 20
val appVersionName = "2.0.0"

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

android {
    compileSdkVersion(AndroidSdk.COMPILE)

    defaultConfig {
        minSdkVersion(AndroidSdk.MIN)
        targetSdkVersion(AndroidSdk.TARGET)
        multiDexEnabled = true

        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        // Flag to enable support for the new language APIs
        coreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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

    coreLibraryDesugaring(Deps.ANDROID_DESUGAR_JDK_LIBS)
    compileOnly(Deps.ARCH_LIFECYCLE_LIVEDATA)
    implementation(Deps.TIMBER)

    // Testing
    testImplementation(Deps.TEST_JUNIT)
}

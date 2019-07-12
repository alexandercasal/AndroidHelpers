const val KOTLIN_VERSION = "1.3.41"

object Deps {
    // Android (https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/buildSrc/src/main/kotlin/androidx/build/dependencies/Dependencies.kt)
    const val ANDROIDX_APP_COMPAT = "androidx.appcompat:appcompat:1.0.2"
    const val ANDROIDX_CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:1.1.3"

    private const val LIFECYCLE_VERSION = "2.0.0"
    const val ARCH_LIFECYCLE_EXT = "androidx.lifecycle:lifecycle-extensions:$LIFECYCLE_VERSION"
    const val ARCH_LIFECYCLE_LIVEDATA = "androidx.lifecycle:lifecycle-livedata:$LIFECYCLE_VERSION"

    // Code
    const val KOTLIN_STD_LIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$KOTLIN_VERSION"
    const val THREETEN_ABP = "com.jakewharton.threetenabp:threetenabp:1.2.1"
    const val TIMBER = "com.jakewharton.timber:timber:4.7.1"

    // Test
    const val TEST_JUNIT = "org.junit.jupiter:junit-jupiter-api:5.5.0"

    // Android Test
    const val TEST_RUNNER = "androidx.test:runner:1.2.0"
    const val TEST_ESPRESSO = "androidx.test.espresso:espresso-core:3.2.0"
}
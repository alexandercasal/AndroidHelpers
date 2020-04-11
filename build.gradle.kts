buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0-beta04")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_VERSION")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
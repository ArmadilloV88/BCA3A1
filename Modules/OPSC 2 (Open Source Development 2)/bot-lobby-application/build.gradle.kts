// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    extra["kotlin_version"] = "1.9.24"
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
//        classpath("com.android.tools.build:gradle:8.7.1")
        classpath("com.android.tools.build:gradle:8.7.2")

        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${project.extra["kotlin_version"]}")
    }
}
allprojects {
    repositories {
//        google()
//        mavenCentral()
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

// Remove the allprojects block as it's not necessary if repositories are declared in settings.gradle.kts

// The commented-out plugins block at the bottom can be removed as it's redundant
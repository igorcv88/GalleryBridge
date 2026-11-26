plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("io.gitlab.arturbosch.detekt")
}
android {
    namespace = "dev.gallerybridge"
    compileSdk = 36
    buildToolsVersion = "36.0.0"
    defaultConfig {
        applicationId = "dev.gallerybridge"
        minSdk = 29
        targetSdk = 36
        versionCode = 4
        versionName = "0.3.1"
        testInstrumentationRunner = "dev.gallerybridge.GalleryBridgeInstrumentation"
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging.resources.excludes += setOf("META-INF/DEPENDENCIES", "META-INF/LICENSE*", "META-INF/NOTICE*")
}
kotlin { compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) }
dependencies {
    implementation(project(":engine"))
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.exifinterface:exifinterface:1.4.1")
}
detekt { config.setFrom(rootProject.file("detekt.yml")); buildUponDefaultConfig = true }

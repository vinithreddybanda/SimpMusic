plugins {
    alias(libs.plugins.android.test)
}

android {
    namespace = "com.maxrave.simpmusic.benchmark"
    compileSdk = 37

    targetProjectPath = ":androidApp"

    defaultConfig {
        minSdk = 26
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        create("benchmark") {
            // The test APK is debuggable; the target app resolves to its
            // profileable, non-debuggable release variant.
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
        }
    }
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.uiautomator)
    implementation(libs.benchmark.macro.junit4)
}

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
        release {
            isDebuggable = true
            signingConfig = getByName("debug").signingConfig
        }
    }
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.uiautomator)
    implementation(libs.benchmark.macro.junit4)
}

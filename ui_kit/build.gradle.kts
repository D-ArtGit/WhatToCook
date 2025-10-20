plugins {
    alias(libs.plugins.whattocook.compose.library)
}

android {
    namespace = "ru.dartx.ui_kit"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(libs.material)
    implementation(libs.material.icons)

    implementation(project(":core_api"))
}
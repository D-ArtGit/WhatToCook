plugins {
    alias(libs.plugins.whattocook.android.library)
    alias(libs.plugins.whattocook.dagger)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ru.dartx.network"

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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(project(":core_api"))

    implementation(libs.kotlin.reflect)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging.jvm)
    implementation(libs.kotlinx.serialization.json)
}
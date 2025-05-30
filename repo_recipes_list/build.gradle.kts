plugins {
    alias(libs.plugins.whattocook.android.library)
    alias(libs.plugins.whattocook.dagger)
}

android {
    namespace = "ru.dartx.repo_recipes_list"

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

    implementation(project(":network"))
    implementation(project(":core_api"))
    implementation(project(":local_db"))

    implementation(libs.kotlinx.coroutines)
}
plugins {
    alias(libs.plugins.whattocook.compose.library)
    alias(libs.plugins.whattocook.dagger)
}

android {
    namespace = "ru.dartx.feature_recipe_card"

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

    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.coil.compose)

    implementation(project(":repo_recipe_card"))
    implementation(project(":ui_kit"))
    implementation(project(":core_api"))
}
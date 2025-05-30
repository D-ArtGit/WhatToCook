plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.whattocook.android.application)
    alias(libs.plugins.whattocook.dagger)
}

android {
    namespace = "ru.dartx.whattocook"

    defaultConfig {
        applicationId = "ru.dartx.whattocook"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(project(":ui_kit"))
    implementation(project(":network"))
    implementation(project(":core_api"))
    implementation(project(":core_factory"))
    implementation(project(":local_db"))
    implementation(project(":repo_recipes_list"))
    implementation(project(":feature_recipes_list"))
    implementation(project(":repo_recipe_card"))
    implementation(project(":feature_recipe_card"))
    implementation(project(":repo_ingredients"))
    implementation(project(":feature_ingredients_recalculation"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.mockito)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)

}
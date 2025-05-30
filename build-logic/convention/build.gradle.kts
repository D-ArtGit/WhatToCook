plugins {
    `kotlin-dsl`
}

group = "ru.dartx.whattocook.buildlogic"

dependencies {
    compileOnly(libs.android.tools)
    compileOnly(libs.kotlin)
}
gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "whattocook.android.application"
            implementationClass = "AndroidComposeApplicationConventionPlugin"
        }
        register("androidComposeLibrary") {
            id = "whattocook.compose.library"
            implementationClass = "AndroidComposeLibraryConventionPlugin"
        }
        register("androidLibrary") {
            id = "whattocook.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("dagger") {
            id = "whattocook.dagger"
            implementationClass = "DaggerConventionPlugin"
        }
    }
}
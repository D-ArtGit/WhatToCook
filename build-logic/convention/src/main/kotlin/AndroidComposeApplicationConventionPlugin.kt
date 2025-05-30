import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.dartx.whattocook.configureKotlinAndroid
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import ru.dartx.whattocook.configureAndroidCompose
import ru.dartx.whattocook.libs

class AndroidComposeApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.android")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk =libs.findVersion("targetSdk").get().toString().toInt()
                configureAndroidCompose(this)
            }
        }
    }
}
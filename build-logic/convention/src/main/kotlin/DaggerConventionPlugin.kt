import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import ru.dartx.whattocook.libs

class DaggerConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.devtools.ksp")

            dependencies{
                "implementation"(libs.findLibrary("dagger").get())
                "ksp"(libs.findLibrary("dagger.compiler").get())
            }
        }
    }
}
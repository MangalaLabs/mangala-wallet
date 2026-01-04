import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class KotlinMultiplatformFeatureModuleConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jetbrains.kotlin.multiplatform")
            apply(plugin = "com.android.library")
            apply(plugin = "kotlinx-serialization")
            apply(plugin = "org.jetbrains.compose")
            apply(plugin = "kotlin-parcelize")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")
        }
    }
}
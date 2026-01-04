buildscript {
    dependencies {
        classpath(libs.buildkonfig.gradle.plugin)
        classpath(libs.moko.resources.generator)
        // Browser
        classpath("com.diffplug.spotless:spotless-plugin-gradle:6.7.0")
        classpath("io.realm:realm-gradle-plugin:10.19.0")
        classpath(libs.dependency.check.gradle)
    }
}

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinxSerialization).apply(false)
    alias(libs.plugins.googleServices).apply(false)
    alias(libs.plugins.firebaseCrashlytics).apply(false)
    alias(libs.plugins.firebasePerformance).apply(false)
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
    id("org.sonarqube") version "6.1.0.5360"
    id("org.owasp.dependencycheck") version "12.1.1" apply false
}

allprojects {
    apply(plugin = "org.owasp.dependencycheck")
}

configure<org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension> {
    format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.ALL.toString()
}

sonar {
    properties {
        property("sonar.projectKey", "trainingdeveloperpro_mangala-wallet_b17072b2-5f18-4535-933d-a76e6d4cc151")
        property("sonar.projectName", "mangala-wallet")
        property("sonar.host.url", "http://localhost:9000")

        property("sonar.dependencyCheck.htmlReportPath", "${project.buildDir}/reports/dependency-check/dependency-check-report.html")
        property("sonar.dependencyCheck.jsonReportPath", "${project.buildDir}/reports/dependency-check/dependency-check-report.json")
        property("sonar.dependencyCheck.summarize", true)
        property("sonar.dependencyCheck.securityHotspot", true)
    }
}
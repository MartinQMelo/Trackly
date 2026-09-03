// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Note: Since AGP 9.0, Kotlin support is built in. The kotlin.android plugin is not required.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.ksp) apply false
}

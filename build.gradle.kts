// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.secrets) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.nordic.application) apply false
    alias(libs.plugins.nordic.application.compose) apply false
    alias(libs.plugins.nordic.library) apply false
    alias(libs.plugins.nordic.library.compose) apply false
    alias(libs.plugins.nordic.hilt) apply false
    alias(libs.plugins.nordic.feature) apply false
}

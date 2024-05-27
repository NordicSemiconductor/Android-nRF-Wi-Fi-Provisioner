plugins {
    alias(libs.plugins.nordic.feature)
    alias(libs.plugins.nordic.hilt)
}

android {
    namespace = "no.nordicsemi.android.wifi.provisioner.feature.nfc"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.nordic.theme)
    implementation(libs.nordic.navigation)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(project(":feature:common"))
    api(project(":lib:nfc"))
    api(project(":lib:domain")) // TODD: Remove this once feature:common is utilized in this module.
    implementation(libs.nordic.permissions.nfc)
}
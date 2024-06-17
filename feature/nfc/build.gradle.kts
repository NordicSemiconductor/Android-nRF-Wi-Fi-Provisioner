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
    // Permissions
    implementation(libs.nordic.permissions.nfc)
    implementation(libs.nordic.permissions.wifi)
    implementation(project(":feature:common"))
    implementation(project(":feature:ui"))
    api(project(":lib:nfc"))
}
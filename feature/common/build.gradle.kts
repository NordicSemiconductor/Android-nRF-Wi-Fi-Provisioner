plugins {
    alias(libs.plugins.nordic.feature)
    alias(libs.plugins.nordic.hilt)
}

android {
    namespace = "no.nordicsemi.wifi.provisioner.common"
}

dependencies {
    implementation(project(":lib:domain"))

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewModel.compose)
    implementation(libs.nordic.navigation)
}

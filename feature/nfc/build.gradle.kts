plugins {
    alias(libs.plugins.nordic.feature)
    alias(libs.plugins.nordic.hilt)
}

android {
    namespace = "no.nordicsemi.android.wifi.provisioner.feature.nfc"
}

dependencies {
    implementation(project(":feature:common"))
    implementation(project(":feature:ui"))
    implementation(project(":lib:nfc"))

    implementation("no.nordicsemi.android.common:ui:2.0.0")
    //implementation(libs.nordic.ui)
    implementation(libs.nordic.theme)
    implementation(libs.nordic.navigation)
    implementation(libs.nordic.permissions.nfc)
    implementation(libs.nordic.permissions.wifi)

    implementation(libs.androidx.compose.material.iconsExtended)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
}
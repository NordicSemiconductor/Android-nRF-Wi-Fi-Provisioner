plugins {
    alias(libs.plugins.nordic.application.compose)
    alias(libs.plugins.nordic.hilt)
}

android {
    namespace = "no.nordicsemi.android.wifi.provisioning"
}

dependencies {
    implementation(project(":lib_provisioner"))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.iconsExtended)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.nordic.scanner)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewModel.compose)

    implementation(libs.nordic.core)
    implementation(libs.nordic.theme)
    implementation(libs.nordic.navigation)
    implementation(libs.nordic.uiscanner)
    implementation(libs.nordic.uilogger)
    implementation(libs.nordic.permission)

    implementation(libs.accompanist.placeholder)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}

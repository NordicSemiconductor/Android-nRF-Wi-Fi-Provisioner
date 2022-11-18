pluginManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    versionCatalogs {
        create("libs") {
            from("no.nordicsemi.android.gradle:version-catalog:1.0.11")
        }
    }
}

rootProject.name = "Android-nRF-Wifi-Provisioner"
include(":app")
include(":lib_provisioner")
include(":lib_proto")

//if (file('../Android-Common-Libraries').exists()) {
//    includeBuild('../Android-Common-Libraries')
//}
//
//if (file('../Android-BLE-Library').exists()) {
//    includeBuild('../Android-BLE-Library')
//}

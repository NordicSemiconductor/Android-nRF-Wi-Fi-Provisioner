package com.nordicsemi.android.wifi.provisioning.home.view

sealed interface HomeScreenViewEvent

object OnSelectDeviceClickEvent : HomeScreenViewEvent

object OnFinishedEvent : HomeScreenViewEvent

object OnSelectWifiEvent : HomeScreenViewEvent

object OnShowPasswordDialog : HomeScreenViewEvent

object OnHidePasswordDialog : HomeScreenViewEvent

data class OnPasswordSelectedEvent(val password: String) : HomeScreenViewEvent

object OnProvisionClickEvent : HomeScreenViewEvent

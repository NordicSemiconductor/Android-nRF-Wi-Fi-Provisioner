package com.nordicsemi.android.wifi.provisioning.home.view

sealed interface HomeScreenViewEvent

object OnSelectButtonClickEvent : HomeScreenViewEvent

object OnFinishedEvent : HomeScreenViewEvent

object OnSelectWifiEvent : HomeScreenViewEvent

data class OnPasswordSelectedEvent(val password: String) : HomeScreenViewEvent

object OnProvisionClickEvent : HomeScreenViewEvent

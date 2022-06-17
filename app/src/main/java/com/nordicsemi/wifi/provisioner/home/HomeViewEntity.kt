package com.nordicsemi.wifi.provisioner.home

import android.bluetooth.BluetoothDevice

sealed interface HomeViewEntity

object IdleHomeViewEntity : HomeViewEntity

data class DeviceSelectedEntity(val device: BluetoothDevice) : HomeViewEntity

data class NetworkSelectedEntity(val device: BluetoothDevice) : HomeViewEntity

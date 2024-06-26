# nRF Wi-Fi Provisioner for Android

An Android library and a sample app for provisioning [nRF 7002 devices](https://www.nordicsemi.com/Products/nRF7002) to a Wi-Fi network. The library and sample application supports 3 different modes of provisioning.
* Bluetooth LE
* SoftAp
* NFC (Note NFC is not a part of the current nRF Connect SDK release 2.7.0)

## Application

<a href='https://play.google.com/store/apps/details?id=no.nordicsemi.android.wifi.provisioning'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' width='250'/></a>

The application requires nRF700x Wi-Fi device with a nRF5x companion chip with firmware allowing provisioning over Bluetooth LE as a transport.
### Features
* Provisioning over Bluetooth LE
  - Obtaining device status
  - Provisioning to Wi-Fi network
  - Unprovisioning

* Provisioning over SoftAP
  - List nearby Wi-Fi networks
  - Provisioning to Wi-Fi network
  - Verifying provisioning status -  Note that Verification is an experimental feature

* Provisioning over NFC
  - Scan Wi-Fi network using the App
  - Manually Add a Wi-Fi Network
  - Provisioning to Wi-Fi network

#### Known Issues
* Provisioning over SoftAP
  - Verification may fail at times and even though the device could be successfully provisioned to the network.
  - During a successful verification, the DnsResolver on certain Android versions/devices may cache the IP address of the SoftAP Provisioning service.
    This can cause, selecting a Wi-Fi network while provisioning/re-provisioning a device to fail. 
    However, turning off and on the wifi on the Android device before re-provisioning should fix this issue. 
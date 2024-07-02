# nRF Wi-Fi Provisioner for Android

An Android library and a sample app for provisioning [nRF 7002 devices](https://www.nordicsemi.com/Products/nRF7002) to a Wi-Fi network. 

The library and sample application supports 3 different modes of provisioning:
* Bluetooth LE
* SoftAp
* NFC

> [!Note]
> Provisioning over NFC is not a part of the current nRF Connect SDK release 2.7.0.

## Application

<a href='https://play.google.com/store/apps/details?id=no.nordicsemi.android.wifi.provisioning'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' width='250'/></a>

The application requires nRF700x Wi-Fi device with a nRF5x companion chip with firmware allowing provisioning over one of supported transport methods.

### Features

* Provisioning over Bluetooth LE
  - Obtaining available Wi-Fi networks from the device
  - Provisioning to Wi-Fi network
  - Obtaining device status
  - Unprovisioning

* Provisioning over Wi-Fi (SoftAP)
  - Obtaining available Wi-Fi networks from the device
  - Provisioning to Wi-Fi network
  - Verifying provisioning status

* Provisioning over NFC
  - Scan Wi-Fi network using the App
  - Manually Add a Wi-Fi Network
  - Provisioning to Wi-Fi network

### Known Issues

* Provisioning over Wi-Fi (SoftAP)
  - Verification may fail if the device was provisioned to a different network then the phone is connected to, even though the provisioned was successful.
  - During a successful verification, the `DnsResolver` on certain Android versions/devices may cache the IP address of the SoftAP Provisioning service.
    This can cause selecting a Wi-Fi network while provisioning/re-provisioning a device to fail. 
    However, turning Off and Nn the Wi-Fi adapter on the Android device before re-provisioning should fix this issue. 

## Libraries

![Maven Central Version](https://img.shields.io/maven-central/v/no.nordicsemi.android.wifi/provisioning-ble)

Include the library in your project by adding the following to your `build.gradle` file.

```gradle
implementation 'no.nordicsemi.android.wifi:provisioning-ble:<version>'
```

```gradle
implementation 'no.nordicsemi.android.wifi:provisioning-softap:<version>'
```

```gradle
implementation 'no.nordicsemi.android.wifi:provisioning-nfc:<version>'
```

Artifacts can be found on [Maven Central reopsitory](https://central.sonatype.com/search?q=no.nordicsemi.android.wifi&namespace=no.nordicsemi.android.wifi).

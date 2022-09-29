# Android-nRF-Wi-Fi-Provisioner

An Android library and app for provisioning [nRF 7 devices](https://www.nordicsemi.com/Products/nRF7002) to a Wi-Fi network.

## Application

<a href='https://play.google.com/store/apps/details?id=no.nordicsemi.android.wifi.provisioning'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' width='250'/></a>

### Flow
The application allows to communicate with a nRF 7 series device.
The main job of the phone is to get status from the device and initiate provisioning or unprovisioning process.

#### Obtaining status

1. The phone connects to the selected IoT device and initialise pairing.
2. After successful pairing it downloads current version and status.
3. Based on status - provisioning or unprovisioning process can be initiated. 

#### Provisioning
1. The phone send START_SCAN command to the IoT device and waits for the result.
2. The phone displays the list with result. An user can select Wi-Fi. If Wi-Fi item provides such an option, then there is a possibility to select a specific channel to connect.
3. After selecting interesting Wi-Fi the phone should send STOP_SCAN command.
4. The user needs to provide a password if the selected Wi-Fi is protected.
5. The user selects if the Wi-Fi should be stored in persistent memory which means that Wi-Fi credentials should survive restarting process.
6. Then the user clicks "Provision" button which sends credential to the IoT device and receive connectivity changes from it.
7. When the IoT device is provisioned then the process is finished and a next device can be provisioned.

#### Unprovisioning
1. The phone sends FORGET_CONFIG command and receive success/error result.

### Bluetooth LE Service
Application depends on one service which should be implemented by an IoT device.
```14387800-130c-49e7-b877-2881c89cb258```

#### Characteristics
The service contains 3 characteristics.
1. ```14387801-130c-49e7-b877-2881c89cb258``` - Unprotected version characteristic which return version number. It is reserved for checking supporting version before actual start of work. 
2. ```14387802-130c-49e7-b877-2881c89cb258``` - Protected with pairing control-point characteristic. It is used by the phone to send commands. In indication the command result status is obtained in asynchronous manner.
3. ```14387803-130c-49e7-b877-2881c89cb258``` - Protected with pairing data-out characteristic. In notification the IoT device sends available Wi-Fi items and connectivity status updates.

### Proto files
The communication with the IoT device is handled with the usage of [Proto files](lib_proto/src/main/proto).


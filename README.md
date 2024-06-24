# nRF Wi-Fi Provisioner for Android

An Android library and a sample app for provisioning [nRF 7002 devices](https://www.nordicsemi.com/Products/nRF7002) to a Wi-Fi network. The libary and sample application supports 3 different modes of provisioning.
* Bluetooth LE
* SoftAp
* NFC (Note NFC is not a part of the current NCS release 2.7.0)

## Application

<a href='https://play.google.com/store/apps/details?id=no.nordicsemi.android.wifi.provisioning'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' width='250'/></a>

The application requires nRF700x Wi-Fi device with a nRF5x companion chip with firmware allowing provisioning over Bluetooth LE as a transport.

### Features
- Obtaining device status
- Provisioning to Wi-Fi network
- Unprovisioning

See this [User Guide](doc/UserGuide.pdf) with the nice presentation about how to use the application.

#### Obtaining status

1. The phone connects to the selected device advertising Wi-Fi Provisioning Service UUID and initialise pairing.
2. After successful pairing, it requests the firmware version and device status.
3. Based on status - provisioning or unprovisioning process can be initiated. 

#### Provisioning

1. The phone sends `START_SCAN` command and waits for the result.
2. The phone displays the list with available Wi-Fi networks received from the device. 
3. User can select a Wi-Fi. In case of multiple Access Points with the same SSID, user may select specific Wi-Fi channel.
4. After selecting the Wi-Fi, the phone should send `STOP_SCAN` command.
5. User provides a password if the selected Wi-Fi network is protected.
7. User selects, whether the Wi-Fi should be stored in persistent memory which means that Wi-Fi credentials should survive power cycle. Disabling this option may be desired during testing.
8. User clicks the "Provision" button which sends credential to the device and receives provisioniong status updates.
9. When the device is provisioned, the process is complete and a next device can be selected.

#### Unprovisioning
1. The phone sends `FORGET_CONFIG` command and receive success/error result.

## Provisioning Protocol

The protocol uses Bluetooth LE as transport.

### Bluetooth LE Advertising

The device should advertise:
* *Service UUID* - Wi-Fi Provisioning Service UUID
* *Service Data*
   * UUID: Wi-Fi Provisioning Service UUID
   * Data: 
      - version (uint8) 
      - flags (uint16)
      - rssi (uint8)
* *Complete Local Name* - device name

### Bluetooth LE Service

| UUID | Name |
| ---- | ---- |
| 1438**7800**-130c-49e7-b877-2881c89cb258 | Wi-Fi Provisioning Service |

#### Characteristics

| UUID | Name | Security | Properties | Notes |
| ---- | ---- | -------- | ---------- | ----- |
| 1438**7801**-130c-49e7-b877-2881c89cb258 | Version | Unprotected | Read | Firmware version number |
| 1438**7802**-130c-49e7-b877-2881c89cb258 | Control Point | Encrypted | Write, Indicate | Commands and statuses |
| 1438**7803**-130c-49e7-b877-2881c89cb258 | Data Out | Encrypted | Notify | Scan resutls and provisioning statueses |

### Encoding

The commands and parameters are encoded using [Protobuf](lib_proto/src/main/proto).

Read more about Protobuf at https://developers.google.com/protocol-buffers


# Provisioning over SoftAP

### Features explained

1. The phone initially connects to the nRF 700x device via the SoftAP advertised by the device.
2. The mobile application then sends a mDNS request to discover the SoftAP Provisioning Service.
3. Once the the service is discovered, the phone resolves the IP address of the SoftAP Provisioning Service.
4. Now the user may select a Wi-Fi network from the list of nearby networks.
5. User provides a password if the selected Wi-Fi network is protected.
6. User clicks the "Provision" button which sends the Wi-Fi credentials to the device.
7. Once the device is provisioned the user may verify the provisioning status by pressing the optional "Verify" button.

#### Known Issues
* Provisioning over SoftAP
    - Verification may fail at times and even though the device could be successfully provisioned to the network.
    - During a successful verification, the DnsResolver on certain Android versions/devices may cache the IP address of the SoftAP Provisioning service.
      This can cause, selecting a Wi-Fi network while provisioning/re-provisioning a device to fail.
      However, turning off and on the wifi on the Android device before re-provisioning should fix this issue.

### Encoding

The commands and parameters are encoded using [Protobuf](lib_proto/src/main/proto).

Read more about Protobuf at https://developers.google.com/protocol-buffers


package no.nordicsemi.android.wifi.provisioner.nfc.domain

enum class EncryptionMode(val value: Int) {
    NONE(0),
    WEP(1),
    TKIP(2),
    AES(3),
    AES_TKIP(4);
}

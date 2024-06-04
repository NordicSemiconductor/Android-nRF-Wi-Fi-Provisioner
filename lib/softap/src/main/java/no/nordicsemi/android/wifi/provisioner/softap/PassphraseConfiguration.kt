package no.nordicsemi.android.wifi.provisioner.softap

sealed class PassphraseConfiguration(open val passphrase : String)

data object Open : PassphraseConfiguration("")

data class Wpa2Passphrase(override val passphrase: String) : PassphraseConfiguration(passphrase)

data class Wpa3Passphrase(override val passphrase: String) : PassphraseConfiguration(passphrase)

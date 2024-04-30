package no.nordicsemi.android.wifi.provisioner.softap

import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.decodeCertificatePem

/**
 * Host name configuration contains the hostname of the SoftAP provisioning service.
 *
 * @property hostName The hostname of the SoftAP provisioning service.
 */
data class HostNameConfiguration(
    val serviceName: String = "wifiprov",
    val hostName: String = "https://wifiprov.local/",
    val certificate: String = CERTIFICATE
) {
    val handshakeCertificates = HandshakeCertificates.Builder()
        .addTrustedCertificate(certificate.decodeCertificatePem())
        .build()
}


private const val CERTIFICATE = "" +
        "-----BEGIN CERTIFICATE-----\n" +
        "MIIB1jCCAXugAwIBAgIUbOY1v8ubBZy6qIsZXelxvLy5l+QwCgYIKoZIzj0EAwIw\n" +
        "YDELMAkGA1UEBhMCVVMxDTALBgNVBAgMBFRlc3QxDTALBgNVBAcMBFRlc3QxGjAY\n" +
        "BgNVBAoMEVRlc3QgT3JnYW5pemF0aW9uMRcwFQYDVQQDDA53aWZpcHJvdi5sb2Nh\n" +
        "bDAeFw0yNDAzMjYxMzI4MzNaFw0yNTAzMjYxMzI4MzNaMGAxCzAJBgNVBAYTAlVT\n" +
        "MQ0wCwYDVQQIDARUZXN0MQ0wCwYDVQQHDARUZXN0MRowGAYDVQQKDBFUZXN0IE9y\n" +
        "Z2FuaXphdGlvbjEXMBUGA1UEAwwOd2lmaXByb3YubG9jYWwwWTATBgcqhkjOPQIB\n" +
        "BggqhkjOPQMBBwNCAAR6COfDiVYhNJkqCe3COkrN/Y9U8LPSDElE+mDk0ri7Ivb8\n" +
        "LefdeYP3HgoTEEgem5eDNy10UZlf6+q6VUWyCH8toxMwETAPBgNVHRMBAf8EBTAD\n" +
        "AQH/MAoGCCqGSM49BAMCA0kAMEYCIQDmEcPlg4GuPIAE9xvpW8t8LGit/+eDWCqE\n" +
        "3ADi/H6f0QIhALUgBnN1+7awE7M1FvSnizX3b5ff7BfzltskPYnpjxqS\n" +
        "-----END CERTIFICATE-----\n"
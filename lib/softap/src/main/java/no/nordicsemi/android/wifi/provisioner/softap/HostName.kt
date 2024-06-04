package no.nordicsemi.android.wifi.provisioner.softap

import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.decodeCertificatePem

/**
 * Host name configuration contains the hostname of the SoftAP provisioning service.
 *
 * @property serviceName Service name of the SoftAP provisioning service.
 * @property hostName Hostname of the SoftAP provisioning service.
 * @property certificate The certificate of the SoftAP provisioning service.
 * @property handshakeCertificates The handshake certificates for the SoftAP provisioning service.
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
        "MIICzTCCAnOgAwIBAgIUF3C1+U4KvWOYM85tZRg5/wmWMKIwCgYIKoZIzj0EAwIw\n" +
        "gbAxCzAJBgNVBAYTAlVTMRIwEAYDVQQIDAlZb3VyU3RhdGUxETAPBgNVBAcMCFlv\n" +
        "dXJDaXR5MRkwFwYDVQQKDBBZb3VyT3JnYW5pemF0aW9uMR8wHQYDVQQLDBZZb3Vy\n" +
        "T3JnYW5pemF0aW9uYWxVbml0MSUwIwYJKoZIhvcNAQkBFhZ5b3VyLmVtYWlsQGV4\n" +
        "YW1wbGUuY29tMRcwFQYDVQQDDA53aWZpcHJvdi5sb2NhbDAeFw0yNDA1MjIwODUx\n" +
        "MTJaFw0zNDA1MjAwODUxMTJaMIGwMQswCQYDVQQGEwJVUzESMBAGA1UECAwJWW91\n" +
        "clN0YXRlMREwDwYDVQQHDAhZb3VyQ2l0eTEZMBcGA1UECgwQWW91ck9yZ2FuaXph\n" +
        "dGlvbjEfMB0GA1UECwwWWW91ck9yZ2FuaXphdGlvbmFsVW5pdDElMCMGCSqGSIb3\n" +
        "DQEJARYWeW91ci5lbWFpbEBleGFtcGxlLmNvbTEXMBUGA1UEAwwOd2lmaXByb3Yu\n" +
        "bG9jYWwwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAAQ7TywX0D1gDuNOB9QJxJtU\n" +
        "g9wKnBsgiajKCcpEKQzNyoYVtF6i4He//Oi01BvAiN5Wh636dzoXKsoP9y0yzx/a\n" +
        "o2kwZzArBgNVHREEJDAigg53aWZpcHJvdi5sb2NhbIIQKi53aWZpcHJvdi5sb2Nh\n" +
        "bDAMBgNVHRMEBTADAQH/MAsGA1UdDwQEAwIBpjAdBgNVHQ4EFgQUMbOrGx0KVcEJ\n" +
        "rEgRTGzoJOavreowCgYIKoZIzj0EAwIDSAAwRQIhAK1vHxEUMD/i3RzEnLPDxmqV\n" +
        "jNCb+C26GeHMp1IqwzeuAiA6+H3OiqFpssvXsVjWEEQxicNpAKL8ZCqL384+rjix\n" +
        "5Q==\n"                                                             +
        "-----END CERTIFICATE-----\n"
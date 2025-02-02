package gearth.protocol.connection.proxy.nitro;

public final class NitroConstants {

    public static final int HTTP_PORT = 9090;
    public static final int HTTP_BUFFER_SIZE = 1024 * 1024 * 10;

    public static final int WEBSOCKET_BUFFER_SIZE = 1024 * 1024 * 10;
    public static final String WEBSOCKET_REVISION = "PRODUCTION-201611291003-338511768";
    public static final String WEBSOCKET_CLIENT_IDENTIFIER = "HTML5";

    public static final String[] CIPHER_SUITES = new String[] {
            "TLS_AES_128_GCM_SHA256",
            "TLS_AES_256_GCM_SHA384",
            "TLS_CHACHA20_POLY1305_SHA256",
            "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
            "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256",
            "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
            "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
            "TLS_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_RSA_WITH_AES_128_CBC_SHA",
            "TLS_RSA_WITH_AES_256_CBC_SHA"
    };

}

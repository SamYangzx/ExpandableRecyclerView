package com.lanmeng.functiontest.rsa;

import com.lanmeng.functiontest.util.ConstantConfig;
import com.lanmeng.functiontest.util.GPMethods;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Base64;
import javax.crypto.Cipher;

public class RSAEncryptDecryptExample {
    /**
     * PKCS#8 X.509 SubjectPublicKeyInfo 格式）
     */
    static String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhS8klZcFlHyOCoC6BVh3HpKQMvG8TNg4Ju0JW8Ko5GD5abLchtnPeK55OkLBJ4qoehp8mAq31ncU5ZCNh06wiPLNC9koyMSdrRdhLAmZ/MGGJMubkqLPgZWq8cfTiAYersGUxtqGRVRE8q9XMvRK30OjUafVdpPCipYIPXKiBPdPtiWmC0t6o5KRhnGMmtaSAb+29uIVT+akMLHT4BTIy9uesFRJyxW1XrAb2DOoxb5UYEPtobjLVb1d90VYXz5x48ITSbJlWzvpnorgWBr9gwLBXDYK8Q0om+zktlP7qxK5VszEWxprEkEkimMnUkceEQhiDOzclc6UHjyMoIVvnQIDAQAB\n" +
            "-----END PUBLIC KEY-----";

    static String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCFLySVlwWUfI4KgLoFWHcekpAy8bxM2Dgm7QlbwqjkYPlpstyG2c94rnk6QsEniqh6GnyYCrfWdxTlkI2HTrCI8s0L2SjIxJ2tF2EsCZn8wYYky5uSos+Blarxx9OIBh6uwZTG2oZFVETyr1cy9ErfQ6NRp9V2k8KKlgg9cqIE90+2JaYLS3qjkpGGcYya1pIBv7b24hVP5qQwsdPgFMjL256wVEnLFbVesBvYM6jFvlRgQ+2huMtVvV33RVhfPnHjwhNJsmVbO+meiuBYGv2DAsFcNgrxDSib7OS2U/urErlWzMRbGmsSQSSKYydSRx4RCGIM7NyVzpQePIyghW+dAgMBAAECggEAY9HMiq1xCjQ8RVTrlxwR04EnwHb7mWCo3UvlKrf/24NvC7B5pjxTSnEB/Q+RMzCySynU3iOSW5ROviwUVyg8GbhYxBuTfL4YK2YNd3MOzajCAIv14vZRrh7dy27odNeZYYNHHa8k2YtqTbXrFg6QmztXidjPnZrrQPFn5Z4oHq2irWfUoRhOHveh1mHFVrXO1ckqw+q+rkSiH8Gg+rDzv++sb9bQM2mnlTg99t7lYDaWs2RtaP93UIiFGnBoEE9LnfVTEqw8aAenmFflg6tzyafNHBuqvZ9eTQSCeJHVlvKoFHPdY60ttkmJTTJmhAhwWgYGq+Loau4ZDKQiFbJdgQKBgQDOr2JFjBc4WYD/XkIyVYZ0j7a5Ed02EcSU1ZQQdDHS5fry4vXDCUQja9POebkzQWaVoVgZsQee5+nLOxdcAkjpSdCqTMXQfwkVIgglABYIZ7qAOl3xSoQ+ezqu1C2AtAZ/XbyAzsykMK6RpK8NuVTtBFTQC8xFI49zJarATXrVYQKBgQCk9jfbIwyB1fXxqz+8ICgzMPskHM8dNMQIAVVT/aQagabhXzntvzf7joNJ0w1ZGfrYOf4kr1YbBtYXpxRYnob9sxB486vpo7+2iLLWd+0nEmSbpttCZSbdYKgff4fKJPJ0LEEi00qNzyRogsYpFbJcO4HHL7fTs3xSLrWG1KhHvQKBgQCx/7dZf+mrfOGsjAfkg2V1UyOhRTJG04yCRWHrvbX/FFNIBJB1NLC69K0H4CrQap2ndbmC/sQ6ZS4i+bQHaGxUhV6wm8es5Qr0aUesXeObv4GN1zZ6jJtRmB0U3eSGOAR0gkzKfc8eBFMDWVntLtRl/1nvhomYbaSoHSNt1AxaYQKBgBIFtGSEl8E3lv8oLnZnLP0qxMVVQGt4cFJPkvryIxOiRpEozjNjSMJ6920BWdv6MqWc8F8hqM6bGJjM8DQeXbEp9wq/1HBaK3/Nv3we7hHuuH8o45uFYZGeFcAbNsKm9rYqf5NGcka78sZYQz4J2Mqq1arlU7gVS+fOopZwdVIhAoGBAMIzxHCGurVfx9LLutdADefJzDYWsSWgvHhm0DEV1yjFUIazr+H2KczZnFOBY9YH2eAEX2/QJdiLn+pA70KhGfudSYXBK5hRyXVU6L7/3s82yKHFjh1EQTLFqDEncJBCt5os+mlirImoDRuY9tVaHgLfBMOIn56kjwR3pwks6mwT" +
            "\n" +
            "-----END PRIVATE KEY-----";

    private static final String ENCRYPT_ALG = "RSA/ECB/NoPadding";
//    static String hexString = "00000000A9D50287D8406B269A3D3F8549FE157AE26066F0D8DEDA231276BDAE50CCBBA4AF4E1714BD259575F088B33711E04EF01E438DA9E3E1D55ED2503A04C8BF30C10F631F024DD349FD009AABE38E79F6861F727E3B35C67E2FBE6D733F95DE9333A597B917CB0F85211209CC2A9BAE4A80624ADE67D625C166B1CF2BDD22D36DC23D0AA1DD1294A8F9F79AD83EC240DDA0302CBA572049B09483D6C256B7BBD86E74678698E27FDB1381C9818B0656260E226220C0B86D5770E99B73B4B3333827BC857BAEEBEFDE2DDD41A19E13F83546039696706E65F8967C6B55F12C4CDFE22BEEB96FF18DC03D2AD6DDB1D3C008926BF8DF32BB4C9ECDFC5D295D";
    static String hexString = "00 00 00 00 3A 2E A5 05 C8 9B A0 1D 24 05 09 05 9E C3 5B 32 6E 82 D7 58 D6 BA 39 A6 33 E4 27 12 7A BF FB DA CD 9C F5 26 CB 52 C0 C7 91 97 1D 98 33 C5 6D 50 5A 8B F1 6D FB AE 56 BA C3 75 51 76 9C 56 70 73 B4 10 96 6C E6 EC 63 18 B4 BB 48 95 45 6B 60 72 7A B6 D9 A3 F2 3A 19 ED 46 4E E8 95 20 C2 6E 9B 8D F9 28 4C B2 EE ED 3A 48 83 12 B2 9D 0D F4 3B 41 D4 D0 49 52 5B 17 A5 0A 36 18 C0 67 F6 88 47 9A 04 A5 E4 72 40 A6 DD 16 4C C7 40 EB EA D5 C0 38 3B 0F 0A 83 C7 62 08 F0 F3 E5 A9 9E 2F 18 AE 0A 35 50 EA 03 81 0A 93 AC 0E DD 79 D6 4F FA A8 B3 66 A1 65 13 CB 56 90 45 55 9D E5 A5 0B A2 D3 D3 A8 D4 D5 3D 70 3E 20 95 F6 27 FF 3B 7E 67 E3 3F 9E FD 1A 4F AD 7E F6 E8 EC 95 11 1D 48 2F 81 4E 52 8A 7C 2C 52 69 8F 5A 7C A7 78 F1 2D 6A 1E 73 62 52 42 0C E2 AC 96 9E 4B 0A 52 ";
//    static String originalText = GPMethods.hexStr2Str(hexString);
    static byte[] originBytes = GPMethods.hexToByteArr(hexString);


    // =========================
    // 1. 从 PEM 格式字符串加载公钥
    // =========================
    public static PublicKey loadPublicKey(String publicKeyPem) throws Exception {
        // 去掉头尾和换行
        String publicKeyContent = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", ""); // 去掉所有空白字符

        // Base64 解码
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyContent);

        // 构造 X.509 编码的公钥规范
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    // =========================
    // 2. 从 PEM 格式字符串加载私钥
    //    支持 PKCS#8 格式: -----BEGIN PRIVATE KEY-----
    // =========================
    public static PrivateKey loadPrivateKey(String privateKeyPem) throws Exception {
        String privateKeyContent = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    // =========================
    // 3. 私钥加密
    // =========================
    public static byte[] encrypt(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALG); // 或 RSA/ECB/OAEPWithSHA-1AndMGF1Padding
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    // =========================
    // 4. 公钥解密
    // =========================
    public static byte[] decrypt(byte[] encryptedData, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALG); // 与加密保持一致
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(encryptedData);
    }

    static String generateSignData(String signHexString){
        String cmd = "421700000100" + signHexString;
        return cmd;
    }


    public static String  getKeyModulus(Key key) {
        String str = "";
        try {
            if (key instanceof RSAPublicKey) {
                RSAPublicKey rsaPub = (RSAPublicKey) key;
                str=rsaPub.getModulus().toString(16);
                System.out.println("public  模数 (Modulus): " + str);
            } else if (key instanceof RSAPrivateKey) {
                RSAPrivateKey rsaPriv = (RSAPrivateKey) key;
                str =rsaPriv.getModulus().toString(16);
                System.out.println( " 模数 (Modulus): " + str);
            } else {
                System.out.println( " 不是 RSA 密钥");
            }
        } catch (Exception e) {
            System.err.println("打印模数失败: " + e.getMessage());
        }
        return str;
    }
    static String generatePubKeyCmd(String pubKey) {
        System.out.println("pubKey: " + pubKey);
        String cmd = "421600000200" + pubKey + GPMethods.bytesToHexString(ConstantConfig.EXTRA_PUBLIC_KEY_SUF);
        return cmd;
    }

    // =========================
    // 示例：加密字符串并解密
    // =========================
    public static void main(String[] args) {
        try {
            // 📝 要加密的原文

            System.out.println("[原文] " + GPMethods.bytesToHexString(originBytes));

            // 🔒 加载密钥
            PublicKey publicKey = loadPublicKey(publicKeyPem);
            PrivateKey privateKey = loadPrivateKey(privateKeyPem);


            // 🛡️ 加密
            byte[] encryptedBytes = encrypt(
                    originBytes, //originalText.getBytes(StandardCharsets.UTF_8),
                    privateKey
            );
            System.out.println("[加密后（Base64）] " + Base64.getEncoder().encodeToString(encryptedBytes));
            System.out.println("[加密后 ] " + GPMethods.bytesToHexString(encryptedBytes));
            System.out.println("sign data cmd: "+ generateSignData(GPMethods.bytesToHexString(encryptedBytes)));

            System.out.println("public data cmd: "+ generatePubKeyCmd(getKeyModulus(publicKey)));

            // 🔓 解密
            byte[] decryptedBytes = decrypt(encryptedBytes, publicKey);
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
            System.out.println("[解密后] " + decryptedText);
            System.out.println("[解密后hex ] " + GPMethods.bytesToHexString(decryptedBytes));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
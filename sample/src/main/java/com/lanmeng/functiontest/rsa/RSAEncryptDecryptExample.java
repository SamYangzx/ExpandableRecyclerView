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
    // 🔐 你的公钥和私钥字符串（替换成你自己的！！！）
    static String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhS8klZcFlHyOCoC6BVh3HpKQMvG8TNg4Ju0JW8Ko5GD5abLchtnPeK55OkLBJ4qoehp8mAq31ncU5ZCNh06wiPLNC9koyMSdrRdhLAmZ/MGGJMubkqLPgZWq8cfTiAYersGUxtqGRVRE8q9XMvRK30OjUafVdpPCipYIPXKiBPdPtiWmC0t6o5KRhnGMmtaSAb+29uIVT+akMLHT4BTIy9uesFRJyxW1XrAb2DOoxb5UYEPtobjLVb1d90VYXz5x48ITSbJlWzvpnorgWBr9gwLBXDYK8Q0om+zktlP7qxK5VszEWxprEkEkimMnUkceEQhiDOzclc6UHjyMoIVvnQIDAQAB\n" +
            "-----END PUBLIC KEY-----";

    static String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCFLySVlwWUfI4KgLoFWHcekpAy8bxM2Dgm7QlbwqjkYPlpstyG2c94rnk6QsEniqh6GnyYCrfWdxTlkI2HTrCI8s0L2SjIxJ2tF2EsCZn8wYYky5uSos+Blarxx9OIBh6uwZTG2oZFVETyr1cy9ErfQ6NRp9V2k8KKlgg9cqIE90+2JaYLS3qjkpGGcYya1pIBv7b24hVP5qQwsdPgFMjL256wVEnLFbVesBvYM6jFvlRgQ+2huMtVvV33RVhfPnHjwhNJsmVbO+meiuBYGv2DAsFcNgrxDSib7OS2U/urErlWzMRbGmsSQSSKYydSRx4RCGIM7NyVzpQePIyghW+dAgMBAAECggEAY9HMiq1xCjQ8RVTrlxwR04EnwHb7mWCo3UvlKrf/24NvC7B5pjxTSnEB/Q+RMzCySynU3iOSW5ROviwUVyg8GbhYxBuTfL4YK2YNd3MOzajCAIv14vZRrh7dy27odNeZYYNHHa8k2YtqTbXrFg6QmztXidjPnZrrQPFn5Z4oHq2irWfUoRhOHveh1mHFVrXO1ckqw+q+rkSiH8Gg+rDzv++sb9bQM2mnlTg99t7lYDaWs2RtaP93UIiFGnBoEE9LnfVTEqw8aAenmFflg6tzyafNHBuqvZ9eTQSCeJHVlvKoFHPdY60ttkmJTTJmhAhwWgYGq+Loau4ZDKQiFbJdgQKBgQDOr2JFjBc4WYD/XkIyVYZ0j7a5Ed02EcSU1ZQQdDHS5fry4vXDCUQja9POebkzQWaVoVgZsQee5+nLOxdcAkjpSdCqTMXQfwkVIgglABYIZ7qAOl3xSoQ+ezqu1C2AtAZ/XbyAzsykMK6RpK8NuVTtBFTQC8xFI49zJarATXrVYQKBgQCk9jfbIwyB1fXxqz+8ICgzMPskHM8dNMQIAVVT/aQagabhXzntvzf7joNJ0w1ZGfrYOf4kr1YbBtYXpxRYnob9sxB486vpo7+2iLLWd+0nEmSbpttCZSbdYKgff4fKJPJ0LEEi00qNzyRogsYpFbJcO4HHL7fTs3xSLrWG1KhHvQKBgQCx/7dZf+mrfOGsjAfkg2V1UyOhRTJG04yCRWHrvbX/FFNIBJB1NLC69K0H4CrQap2ndbmC/sQ6ZS4i+bQHaGxUhV6wm8es5Qr0aUesXeObv4GN1zZ6jJtRmB0U3eSGOAR0gkzKfc8eBFMDWVntLtRl/1nvhomYbaSoHSNt1AxaYQKBgBIFtGSEl8E3lv8oLnZnLP0qxMVVQGt4cFJPkvryIxOiRpEozjNjSMJ6920BWdv6MqWc8F8hqM6bGJjM8DQeXbEp9wq/1HBaK3/Nv3we7hHuuH8o45uFYZGeFcAbNsKm9rYqf5NGcka78sZYQz4J2Mqq1arlU7gVS+fOopZwdVIhAoGBAMIzxHCGurVfx9LLutdADefJzDYWsSWgvHhm0DEV1yjFUIazr+H2KczZnFOBY9YH2eAEX2/QJdiLn+pA70KhGfudSYXBK5hRyXVU6L7/3s82yKHFjh1EQTLFqDEncJBCt5os+mlirImoDRuY9tVaHgLfBMOIn56kjwR3pwks6mwT" +
            "\n" +
            "-----END PRIVATE KEY-----";

    private static final String ENCRYPT_ALG = "RSA/ECB/NoPadding";
    static String hexString = "000000006D31C241103E8F3B0CC2E29A9E5565F679C9397835F806DDB5DE41D7C2308A336B9CD704514BBE725461207793D17A1F3E77CDAAC1211186DA8BA6847369DEA7F3472D9CDDB06AE187308F62035DEF60DCD5B6A9BAD60DF98F13A84F363280886D25BB609EBDC9A5E49EBF7CDD69D1E26D4E5AE273807486D89542F4A0010C03CD3C1C07F992EF3DC3B454995CC39C3903A8AB380DEC782CAAFB6286BD74A7A611CB627307A4F638FC7A923DEFB0E726D39A348349D88B19CD94D48BE05D02400B12097CF0FF91409763DCA6D0C9C0D3D56FD35216AC2D397ADA55D6AE9513FF6DA71296E5DA5C1FBE08BCF226D8E7335A0930E98C3CCE7625D2026E";
    static String originalText = GPMethods.hexStr2Str(hexString);
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
    // 3. 公钥加密
    // =========================
    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALG); // 或 RSA/ECB/OAEPWithSHA-1AndMGF1Padding
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    // =========================
    // 4. 私钥解密
    // =========================
    public static byte[] decrypt(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALG); // 与加密保持一致
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
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

            System.out.println("[原文] " + originalText);

            // 🔒 加载密钥
            PublicKey publicKey = loadPublicKey(publicKeyPem);
            PrivateKey privateKey = loadPrivateKey(privateKeyPem);


            // 🛡️ 加密
            byte[] encryptedBytes = encrypt(
                    originBytes, //originalText.getBytes(StandardCharsets.UTF_8),
                    publicKey
            );
            System.out.println("[加密后（Base64）] " + Base64.getEncoder().encodeToString(encryptedBytes));
            System.out.println("[加密后 ] " + GPMethods.bytesToHexString(encryptedBytes));
            System.out.println("sign data cmd: "+ generateSignData(GPMethods.bytesToHexString(encryptedBytes)));

            System.out.println("public data cmd: "+ generatePubKeyCmd(getKeyModulus(publicKey)));

            // 🔓 解密
            byte[] decryptedBytes = decrypt(encryptedBytes, privateKey);
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
            System.out.println("[解密后] " + decryptedText);
            System.out.println("[解密后hex ] " + GPMethods.bytesToHexString(decryptedBytes));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
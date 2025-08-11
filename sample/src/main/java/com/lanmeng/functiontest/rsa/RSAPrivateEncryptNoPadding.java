package com.lanmeng.functiontest.rsa;

import com.lanmeng.functiontest.util.ConstantConfig;
import com.lanmeng.functiontest.util.GPMethods;

import javax.crypto.Cipher;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class RSAPrivateEncryptNoPadding {

    /**
     * 打印公钥的 Base64 编码字符串（最常用，可保存/传输）
     */
    public static void printPublicKeyInBase64(PublicKey publicKey) {
        try {
            byte[] publicKeyBytes = publicKey.getEncoded(); // X.509 格式的编码
            String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKeyBytes);

            System.out.println("=== 公钥 (Base64编码) ===");
            System.out.println(publicKeyBase64);

            // 可选：打印成 PEM 格式（带头尾）
            String pemPublicKey = "-----BEGIN PUBLIC KEY-----\n" + publicKeyBase64.replaceAll("(.{64})", "$1\n") + "\n" + "-----END PUBLIC KEY-----";
            System.out.println("\n=== 公钥 (PEM 格式) ===");

            System.out.println(pemPublicKey);

            System.out.println("byte format: " + GPMethods.bytesToHexString(publicKeyBytes));
        } catch (Exception e) {
            System.err.println("打印公钥失败: " + e.getMessage());
        }
    }

    public static void printPrivateKeyInBase64(PrivateKey privateKey) {
        try {
            byte[] privateKeyEncoded = privateKey.getEncoded(); // X.509 格式的编码
            String private64 = Base64.getEncoder().encodeToString(privateKeyEncoded);

            System.out.println("=== private (Base64编码) ===");
            System.out.println(private64);


            System.out.println("byte format: " + GPMethods.bytesToHexString(privateKeyEncoded));
        } catch (Exception e) {
            System.err.println("打印公钥失败: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            // 1. 生成 RSA 2048 公私钥对
            KeyPair keyPair = generateRSAKeyPair(2048);
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            printPublicKeyInBase64(publicKey);
            printPrivateKeyInBase64(privateKey);

            // 2. 打印模数
            System.out.println("=== 公钥和私钥的模数 (Modulus) ===");
            printModulus("公钥", publicKey);
            printModulus("私钥", privateKey);

            // 3. 准备要加密的原始数据（比如一个字符 "A"）
            String originalText = ConstantConfig.ORIGIN_DATA; // 只有1字节
            int originTextLenght = originalText.length();

            System.out.println("\n=== 原始数据 ===");
            System.out.println("原文: " + originalText);

            // 4. 构造 256 字节的数据：前面 255 字节为 0，最后 1 字节为你的数据
            byte[] dataToEncrypt = new byte[256];
            byte[] originalBytes = originalText.getBytes(StandardCharsets.UTF_8);
            System.out.printf("origin byte length: %d\n", originalBytes.length);
            System.arraycopy(originalBytes, 0, dataToEncrypt, 256 - originalBytes.length, originalBytes.length);

            System.out.println("构造的 256 字节待加密数据（最后字节为有效数据）: ");
            System.out.printf("有效数据 = '%s' \n", originalText);
//            System.out.println("（其余 255 字节为 0，你可以自定义填充逻辑）");

            // 5. 使用 私钥 + RSA/ECB/NoPadding 加密
            byte[] encryptedData = encryptWithPrivateKey(privateKey, dataToEncrypt, "RSA/ECB/NoPadding");
            System.out.println("\n=== 加密后 ===");
            System.out.println("encryptedData.length: " + encryptedData.length);
            System.out.println("hex code: " + GPMethods.bytesToHexString(encryptedData));

            String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedData);
            System.out.println("私钥加密结果（Base64）: " + encryptedBase64);

            // 6. 使用 公钥 + RSA/ECB/NoPadding 解密
            byte[] decryptedData = decryptWithPublicKey(publicKey, encryptedData, "RSA/ECB/NoPadding");
            System.out.println("\n=== 解密后 ===");
            // 取出最后一个字节（你的原始数据）
//            byte[] decryptedOriginalByte = new byte[]{decryptedData[256 - 1]};
            String decryptedText = new String(decryptedData, StandardCharsets.UTF_8);
            System.out.println("解密得到的原文: " + decryptedText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成 RSA 密钥对
     */
    public static KeyPair generateRSAKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 使用私钥加密（指定算法，如 RSA/ECB/NoPadding）
     */
    public static byte[] encryptWithPrivateKey(PrivateKey privateKey, byte[] data, String transformation) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 使用公钥解密（指定算法，如 RSA/ECB/NoPadding）
     */
    public static byte[] decryptWithPublicKey(PublicKey publicKey, byte[] encryptedData, String transformation) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(encryptedData);
    }

    /**
     * 打印公钥/私钥的模数 (Modulus)
     */
    public static void printModulus(String keyType, Key key) {
        try {
            if (key instanceof java.security.interfaces.RSAPublicKey) {
                java.security.interfaces.RSAPublicKey rsaPub = (java.security.interfaces.RSAPublicKey) key;
                System.out.println(keyType + " 模数 (Modulus): " + rsaPub.getModulus().toString(16));
            } else if (key instanceof java.security.interfaces.RSAPrivateKey) {
                java.security.interfaces.RSAPrivateKey rsaPriv = (java.security.interfaces.RSAPrivateKey) key;
                System.out.println(keyType + " 模数 (Modulus): " + rsaPriv.getModulus().toString(16));
            } else {
                System.out.println(keyType + " 不是 RSA 密钥");
            }
        } catch (Exception e) {
            System.err.println("打印 " + keyType + " 模数失败: " + e.getMessage());
        }
    }
}
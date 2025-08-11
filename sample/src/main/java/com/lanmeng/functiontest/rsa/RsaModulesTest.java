package com.lanmeng.functiontest.rsa;


import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

public class RsaModulesTest {
    static final BigInteger MODULE = new BigInteger("18089015745572588181678739398468692405283157572453585123477625902873418619571625340381829103383005629107340369970508339533124119065635698326770883750453736823417496581727128872583606432872414378619549962492922191737827401373975869012785658643771645750516118823730454434808461411736705378545797578892128597196095117952899651155588581624950210052634764823824152296021544742400578819298773497034355292706059898672522491479898007632729400308548473448648184104642479701046159784833520390614776919317561533871729592252857847091691845841579578345031497394763861988366072144903331314777226054368962034244031409738286300613749");

    public static void main(String[] args) {
        System.out.println("1111111111111 ");
        getModules();
        try {
            //公钥加密
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            BigInteger modulus = new BigInteger("154471992999058139479994460025815654498183391593444870454838266974581244599191659985455957889064163942388409487313472074598227824609910604156744751985833898809065078785899074110993629452358669379496163284362583792866500058660069050752020922895749548342185553141417346777273482310707415185758164008066298773949", 10);
            BigInteger publicExponent = new BigInteger("65537");
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, publicExponent);
            PublicKey publicKey = keyFactory.generatePublic(rsaPublicKeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptData = cipher.doFinal("Hello,RSA!".getBytes());
            //私钥解密
            BigInteger privateExponent = new BigInteger("24953766420205815381764520016071994967304996670579990593182061010725111564027070269710579156377653900210050677360692873548856950717077735724971492275722465522175892883197573916804276397143284954594245180776141869860033925480138858143033802945465036705957639063440190950861284456594945244826689811470380537909", 10);
            RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(modulus, privateExponent);
            PrivateKey privateKey = keyFactory.generatePrivate(rsaPrivateKeySpec);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptData = cipher.doFinal(encryptData);
            System.out.println("decryptData:" + new String(decryptData));
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }


    }

    static void getModules() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            RSAPrivateKey privateKey = (RSAPrivateKey) pair.getPrivate();
            RSAPublicKey publicKey = (RSAPublicKey) pair.getPublic();
            System.out.println("publicKey exponent:" + publicKey.getPublicExponent());
            System.out.println("publicKey modules:" + publicKey.getModulus());
            System.out.println("publicKey format:" + publicKey.getFormat());
            System.out.println("---------------------华丽的分割线-------------------------");
            System.out.println("privateKey exponent:" + privateKey.getPrivateExponent());
            System.out.println("privateKey modules:" + privateKey.getModulus());
            System.out.println("privateKey format:" + privateKey.getFormat());
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }
}

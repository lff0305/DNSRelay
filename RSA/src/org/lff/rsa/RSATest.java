package org.lff.rsa;

/**
 * User: LFF
 * Datetime: 2014/12/24 13:35
 */
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.Cipher;

public class RSATest {
    public static void main(String[] args) throws Exception {
        String input = "thisIsMyPassword$7788";
        Cipher cipher = Cipher.getInstance("RSA");
        RSAPublicKey pubKey = (RSAPublicKey) PublicKeyReader.get("public.key");
        RSAPrivateKey privKey = (RSAPrivateKey) PrivateKeyReader.get("private.key");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        //加密后的东西
        System.out.println("cipher: " + new String(cipherText));
        //开始解密
        cipher.init(Cipher.DECRYPT_MODE, privKey);
        byte[] plainText = cipher.doFinal(cipherText);
        System.out.println("plain : " + new String(plainText));
    }

}
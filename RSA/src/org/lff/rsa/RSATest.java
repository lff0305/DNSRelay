package org.lff.rsa;

/**
 * User: LFF
 * Datetime: 2014/12/24 13:35
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import javax.crypto.Cipher;

public class RSATest {
    public static void main(String[] args) throws Exception {

        String input = "";

        for (int i=0; i<1; i++) {
            input += "thisIsMyPassword$7788";
        }
        Cipher cipher = Cipher.getInstance("RSA");
        RSAPublicKey pubKey = (RSAPublicKey) PublicKeyReader.get("public.key");
        RSAPrivateKey privKey = (RSAPrivateKey) PrivateKeyReader.get("private.key");


        ByteArrayInputStream bis = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] buf = new byte[200];
        int size = bis.read(buf);
        while (size > 0) {
            if (size == 200) {
                byte[] cipherText = cipher.doFinal(buf);
                System.out.println(cipherText.length + ":" + Arrays.toString(cipherText));
                bos.write(cipherText);
            } else {
                byte[] t = new byte[size];
                System.arraycopy(buf, 0, t, 0, size);
                byte[] cipherText = cipher.doFinal(t);
                bos.write(cipherText);
            }
            size = bis.read(buf);
        }
        bos.close();

       // System.out.println("cipher: " + new String(bos.toByteArray()));
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privKey);

        bis = new ByteArrayInputStream(bos.toByteArray());
        bos = new ByteArrayOutputStream();
        buf = new byte[256];
        size = bis.read(buf);
        while (size > 0) {
            if (size == 256) {
                System.out.println("X :" + Arrays.toString(buf));
                byte[] cipherText = cipher.doFinal(buf);
                bos.write(cipherText);
            } else {
                byte[] t = new byte[size];
                System.arraycopy(buf, 0, t, 0, size);
                byte[] cipherText = cipher.doFinal(t);
                bos.write(cipherText);
            }
            size = bis.read(buf);
        }
        bos.close();

        byte[] plainText = bos.toByteArray();
        System.out.println("plain : " + new String(plainText));
    }

}

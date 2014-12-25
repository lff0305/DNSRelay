package org.lff.rsa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

/**
 * User: LFF
 * Datetime: 2014/12/24 14:42
 */
public class RSACipher {

    private static final Logger logger = LoggerFactory.getLogger(RSACipher.class);

    private static RSAPublicKey pubKey;
    private static RSAPrivateKey privKey;

    static {
        try {
            pubKey = (RSAPublicKey) PublicKeyReader.get("public.key");
            privKey = (RSAPrivateKey) PrivateKeyReader.get("private.key");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] encrypt(byte[] input) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(input);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Cipher cipher = Cipher.getInstance("RSA");
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
            return bos.toByteArray();
        } catch (Exception e) {
            logger.error("Error in encrypt data " + Arrays.toString(input), e);
        }
        return null;
    }

    public static byte[] decrypt(byte[] encrypted) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privKey);

            ByteArrayInputStream bis = new ByteArrayInputStream(encrypted);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[256];
            int size = bis.read(buf);
            while (size > 0) {
                if (size == 256) {
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
            return bos.toByteArray();
        }catch (Exception e) {
            logger.error("Error in decrypt data " + Arrays.toString(encrypted), e);
        }
        return null;
    }
}

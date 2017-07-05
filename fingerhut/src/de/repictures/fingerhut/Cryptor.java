package de.repictures.fingerhut;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Random;

public class Cryptor {

    private byte[] hashKey = {-128, -48, -106, 59, -7, 49, -57, -21, 3, -103, 85, 58, -4, 90, -116, -105};

    public byte[] hash(String input) {
        try {
            KeySpec spec = new PBEKeySpec(input.toCharArray(), hashKey, 65536, 128);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return f.generateSecret(spec).getEncoded();
        }catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] encrypt(String input, byte[] key){
        try{
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, originalKey);
            return cipher.doFinal(input.getBytes("ISO-8859-1"));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e){
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(byte[] encryptedInput, byte[] key){
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES");
            cipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedInput);
            return new String(decryptedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    private AlgorithmParameterSpec getIV(Cipher cipher){
        byte[] iv = new byte[cipher.getBlockSize()];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}

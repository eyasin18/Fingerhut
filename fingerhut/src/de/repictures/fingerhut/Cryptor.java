package de.repictures.fingerhut;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Random;

public class Cryptor {

    public String hashToString(String input){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");//Wir wollen auf SHA-256 verschlüsseln
            messageDigest.update(input.getBytes()); //Input wird verschlüsselt
            return bytesToHex(messageDigest.digest()); //Hash wird als Hexadezimalzahl ausgegeben
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] hashToByte(String input){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");//Wir wollen auf SHA-256 verschlüsseln
            messageDigest.update(input.getBytes()); //Input wird verschlüsselt
            return messageDigest.digest(); //Hash wird als Hexadezimalzahl ausgegeben
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] encryptSymetricFromString(String input, byte[] key){
        try{
            Cipher cipher = Cipher.getInstance("AES"); //Cipher Objekt wird erzeugt. Wir wollen auf AES verschlüsseln.
            SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES"); //bytearray wir zum "SecretKey" gemacht (key [benutzter Schlüssel als bytearray], offset, [wie lang unser Schlüssel sein soll], algorithm [welchen verschlüsselungsargorythums verwenden wir?])
            cipher.init(Cipher.ENCRYPT_MODE, originalKey); //Cipher wird initialisiert
            return cipher.doFinal(input.getBytes("UTF-8")); //Input wird verschlüsselt
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e){
            e.printStackTrace();
            return null;
        }
    }

    public byte[] encryptSymetricFromByte(byte[] input, byte[] key){
        try{
            Cipher cipher = Cipher.getInstance("AES"); //Cipher Objekt wird erzeugt. Wir wollen auf AES verschlüsseln.
            SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES"); //bytearray wir zum "SecretKey" gemacht (key [benutzter Schlüssel als bytearray], offset, [wie lang unser Schlüssel sein soll], algorithm [welchen verschlüsselungsargorythums verwenden wir?])
            cipher.init(Cipher.ENCRYPT_MODE, originalKey); //Cipher wird initialisiert
            return cipher.doFinal(input); //Input wird verschlüsselt
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e){
            e.printStackTrace();
            return null;
        }
    }

    public String decryptSymetricToString(byte[] encryptedInput, byte[] key){
        try {
            Cipher cipher = Cipher.getInstance("AES"); //Cipher Objekt wird erzeugt. Wir wollen auf AES entschlüsseln.
            SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES"); //bytearray wir zum "SecretKey" gemacht (key [benutzter Schlüssel als bytearray], offset, [wie lang unser Schlüssel sein soll], algorithm [welchen verschlüsselungsargorythums verwenden wir?])
            cipher.init(Cipher.DECRYPT_MODE, originalKey); //cipher wird initialisiert
            byte[] decryptedBytes = cipher.doFinal(encryptedInput); //input wird entschlüsselt
            return new String(decryptedBytes); //bytearray wird zum string gemacht
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decryptSymetricToByte(byte[] encryptedInput, byte[] key){
        try {
            Cipher cipher = Cipher.getInstance("AES"); //Cipher Objekt wird erzeugt. Wir wollen auf AES entschlüsseln.
            SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES"); //bytearray wir zum "SecretKey" gemacht (key [benutzter Schlüssel als bytearray], offset, [wie lang unser Schlüssel sein soll], algorithm [welchen verschlüsselungsargorythums verwenden wir?])
            cipher.init(Cipher.DECRYPT_MODE, originalKey); //cipher wird initialisiert
            return cipher.doFinal(encryptedInput); //input wird entschlüsselt
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] encryptAsymetric(String input, PublicKey publicKey){ // Verschlüsselt asymetrisch
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(input.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryptAsymetric(byte[] input, PrivateKey privateKey){ // Entschlüsselt asymetrisch
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(input);
            return new String(decryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public KeyPair generateKeyPair(){ // Generiert Schlüsselpaar zur asymetrischen Verschlüsselung
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String generateAuthPin(int length){ // Generiert zufälligen String mit n Zeichen
        SecureRandom sr = new SecureRandom();
        return new BigInteger(130, sr).toString(32);
    }

    public String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        StringBuilder output = new StringBuilder();
        for (byte aByte : bytes) {
            int v = aByte & 0xFF;
            output.append(hexArray[v >>> 4]);
            output.append(hexArray[v & 0x0F]);
        }
        return output.toString();
    }

    public byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}

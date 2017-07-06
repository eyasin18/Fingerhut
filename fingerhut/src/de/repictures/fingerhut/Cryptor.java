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

    private byte[] hashKey = {36,-102,83,95,87,-91,-38,118,-16,20,52,-42,18,-70,-75,-69,-61,-6,40,-121,-118,-4,-113,-57,96,126,37,-88,-123,98,61,31}; //Schlüssel zum Hashen.

    public byte[] hash(String input) {
        try {
            KeySpec spec = new PBEKeySpec(input.toCharArray(), hashKey, 65536, 256); //Specifikationen des Schlüssels werden festgelegt. (input [zu verschlüsselnder string als chararray], salt [beliebige nummernfolge die dem verschlüsselten bytearray einfach angehängt wird], iterationCount [wie oft wird hintereinander verschlüsselt], keyLength [wie lang soll der schlüssel werden])
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1"); //Wir initialisieren die KeyFactory. Wir wollen die "Password-Based Key Derivation Function 2" mit dem "HmacSHA1" Algorythmus verwenden
            return f.generateSecret(spec).getEncoded(); //Mache den Schlüssel aus den Spezifikationen
        }catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] encrypt(String input, byte[] key){
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

    public String decrypt(byte[] encryptedInput, byte[] key){
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

    private AlgorithmParameterSpec getIV(Cipher cipher){ //sinnlose methode die wir vielleicht mal wieder gebrauchen könnten. Ich vermute dass hier der Punkt von der Kreisbahn abgelesen wird.
        byte[] iv = new byte[cipher.getBlockSize()];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}

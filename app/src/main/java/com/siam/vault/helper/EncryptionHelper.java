package com.siam.vault.helper;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import java.io.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public final class EncryptionHelper {
    private static final String STORE = "AndroidKeyStore", ALIAS = "photo_video_vault_aes_gcm_v1";
    private static final int IV_LENGTH = 12, TAG_BITS = 128;
    private EncryptionHelper() {}
    private static SecretKey key() throws Exception {
        KeyStore ks = KeyStore.getInstance(STORE); ks.load(null);
        if (ks.containsAlias(ALIAS)) return (SecretKey) ks.getKey(ALIAS, null);
        KeyGenerator gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, STORE);
        gen.init(new KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(false).build());
        return gen.generateKey();
    }
    public static void encrypt(InputStream source, File destination) throws Exception {
        byte[] iv = new byte[IV_LENGTH]; new SecureRandom().nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding"); cipher.init(Cipher.ENCRYPT_MODE, key(), new GCMParameterSpec(TAG_BITS, iv));
        try (OutputStream raw = new BufferedOutputStream(new FileOutputStream(destination))) {
            raw.write(iv); try (CipherOutputStream encrypted = new CipherOutputStream(raw, cipher)) { copy(source, encrypted); }
        }
    }
    public static void decrypt(File source, File destination) throws Exception {
        try (InputStream raw = new BufferedInputStream(new FileInputStream(source))) {
            byte[] iv = readExactly(raw, IV_LENGTH); if (iv.length != IV_LENGTH) throw new IOException("Invalid encrypted file");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding"); cipher.init(Cipher.DECRYPT_MODE, key(), new GCMParameterSpec(TAG_BITS, iv));
            try (CipherInputStream decrypted = new CipherInputStream(raw, cipher); OutputStream out = new BufferedOutputStream(new FileOutputStream(destination))) { copy(decrypted, out); }
        }
    }
    public static void decryptTo(File source, OutputStream destination) throws Exception {
        try (InputStream raw = new BufferedInputStream(new FileInputStream(source))) {
            byte[] iv = readExactly(raw, IV_LENGTH); if (iv.length != IV_LENGTH) throw new IOException("Invalid encrypted file");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding"); cipher.init(Cipher.DECRYPT_MODE, key(), new GCMParameterSpec(TAG_BITS, iv));
            try (CipherInputStream decrypted = new CipherInputStream(raw, cipher)) { copy(decrypted, destination); }
        }
    }
    public static byte[] seal(byte[] plain) throws Exception {
        byte[] iv = new byte[IV_LENGTH]; new SecureRandom().nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding"); cipher.init(Cipher.ENCRYPT_MODE, key(), new GCMParameterSpec(TAG_BITS, iv));
        byte[] enc = cipher.doFinal(plain), result = new byte[iv.length + enc.length]; System.arraycopy(iv,0,result,0,iv.length); System.arraycopy(enc,0,result,iv.length,enc.length); return result;
    }
    public static byte[] unseal(byte[] stored) throws Exception {
        if (stored == null || stored.length <= IV_LENGTH) throw new IOException("Invalid encrypted data");
        byte[] iv = java.util.Arrays.copyOfRange(stored,0,IV_LENGTH), data=java.util.Arrays.copyOfRange(stored,IV_LENGTH,stored.length);
        Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding"); cipher.init(Cipher.DECRYPT_MODE,key(),new GCMParameterSpec(TAG_BITS,iv)); return cipher.doFinal(data);
    }
    private static byte[] readExactly(InputStream in, int length) throws IOException { byte[] b=new byte[length]; int off=0,n; while(off<length&&(n=in.read(b,off,length-off))!=-1) off+=n; return off==length?b:java.util.Arrays.copyOf(b,off); }
    private static void copy(InputStream in, OutputStream out) throws IOException { byte[] b=new byte[64*1024]; int n; while((n=in.read(b))!=-1) out.write(b,0,n); }
}

package com.siam.vault.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PinHelper {
    private static final String PREFS = "vault_settings";
    private static final int ITERATIONS = 120_000;
    private PinHelper() {}
    private static SharedPreferences prefs(Context c) { return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE); }
    private static String preferredPrf(){try{SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");return "PBKDF2WithHmacSHA256";}catch(Exception ignored){return "PBKDF2WithHmacSHA1";}}
    private static String hash(String value, byte[] salt) throws Exception {
        return hash(value,salt,preferredPrf());
    }
    private static String hash(String value, byte[] salt,String prf) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(value.toCharArray(), salt, ITERATIONS, 256);
        byte[] result = SecretKeyFactory.getInstance(prf).generateSecret(spec).getEncoded();
        spec.clearPassword(); return prf+":"+Base64.encodeToString(result, Base64.NO_WRAP);
    }
    private static byte[] randomSalt() { byte[] b = new byte[16]; new SecureRandom().nextBytes(b); return b; }
    private static void storeHash(SharedPreferences p, String saltKey, String hashKey, String value) throws Exception {
        byte[] salt = randomSalt(); p.edit().putString(saltKey, Base64.encodeToString(salt, Base64.NO_WRAP)).putString(hashKey, hash(value, salt)).apply();
    }
    public static boolean isConfigured(Context c) { return prefs(c).contains("pin_hash"); }
    public static void setInitial(Context c, String pin, String fakePin, String answer) throws Exception {
        SharedPreferences p = prefs(c); storeHash(p, "pin_salt", "pin_hash", pin);
        if (fakePin != null && fakePin.matches("\\d{4}") && !fakePin.equals(pin)) storeHash(p, "fake_salt", "fake_hash", fakePin);
        else p.edit().remove("fake_salt").remove("fake_hash").apply();
        storeHash(p, "answer_salt", "answer_hash", answer.trim().toLowerCase(java.util.Locale.ROOT));
        p.edit().putInt("failed_count", 0).putLong("lock_until", 0).apply();
    }
    private static boolean verify(SharedPreferences p, String saltKey, String hashKey, String candidate) {
        try {
            String encodedSalt = p.getString(saltKey, null), expected = p.getString(hashKey, null);
            if (encodedSalt == null || expected == null) return false;
            byte[] salt = Base64.decode(encodedSalt, Base64.NO_WRAP);String[] pieces=expected.split(":",2);if(pieces.length!=2)return false;
            byte[] a = Base64.decode(hash(candidate, salt,pieces[0]).split(":",2)[1], Base64.NO_WRAP), b = Base64.decode(pieces[1], Base64.NO_WRAP);
            return MessageDigest.isEqual(a, b);
        } catch (Exception e) { return false; }
    }
    public static boolean isRealPin(Context c, String pin) { return verify(prefs(c), "pin_salt", "pin_hash", pin); }
    public static boolean isFakePin(Context c, String pin) { return verify(prefs(c), "fake_salt", "fake_hash", pin); }
    public static boolean isLocked(Context c) { return System.currentTimeMillis() < prefs(c).getLong("lock_until", 0); }
    public static long lockRemaining(Context c) { return Math.max(0, prefs(c).getLong("lock_until", 0) - System.currentTimeMillis()); }
    public static int recordFailure(Context c) {
        SharedPreferences p = prefs(c); int count = p.getInt("failed_count", 0) + 1;
        if (count >= 3) { p.edit().putInt("failed_count", 0).putLong("lock_until", System.currentTimeMillis() + 30_000).apply(); return 3; }
        p.edit().putInt("failed_count", count).apply(); return count;
    }
    public static void clearFailures(Context c) { prefs(c).edit().putInt("failed_count", 0).putLong("lock_until", 0).apply(); }
    public static boolean resetWithAnswer(Context c, String answer, String newPin) {
        SharedPreferences p = prefs(c); if (!verify(p, "answer_salt", "answer_hash", answer.trim().toLowerCase(java.util.Locale.ROOT))) return false;
        try { storeHash(p, "pin_salt", "pin_hash", newPin); clearFailures(c); return true; } catch (Exception e) { return false; }
    }
    public static boolean hasBiometricEnabled(Context c) { return prefs(c).getBoolean("biometric_enabled", false); }
    public static void setBiometricEnabled(Context c, boolean enabled) { prefs(c).edit().putBoolean("biometric_enabled", enabled).apply(); }
    public static boolean isDark(Context c) { return prefs(c).getBoolean("dark_mode", false); }
    public static void setDark(Context c, boolean dark) { prefs(c).edit().putBoolean("dark_mode", dark).apply(); }
}

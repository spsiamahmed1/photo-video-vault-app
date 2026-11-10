# photo-video-vault-app
Photo Video Vault - ছবি ভিডিও লুকানোর অ্যাপ - Private Locker - Java

## Build

Java-only Android app: minSdk 24, targetSdk 34, Java 17, with ViewBinding enabled. Open the repository in Android Studio (Android SDK 35) or run `./gradlew assembleDebug`. The GitHub Actions workflow builds an APK on each push and manual run; download it from **Actions → Build APK → Artifacts → Vault-App**.

## Use

On first launch set a 4-digit PIN, confirmation, optional different decoy PIN, and recovery answer. Later, the launcher shows a calculator; enter the real or decoy PIN and press `=` to open the appropriate view. Three wrong PIN attempts trigger a 30-second delay. Long-press the calculator title to open PIN recovery. Biometric unlock can be enabled in Profile when supported by the device.

## Privacy and security

- Imported photos and videos are encrypted with AES-GCM and stored in the app's private internal directory. The key is non-exportable and held by Android Keystore. Secret-note title and body are encrypted as well. The app does not declare Internet access.
- PINs are stored as salted PBKDF2 hashes. Biometric unlocking uses Android BiometricPrompt when available. An optional decoy PIN opens an empty vault.
- Failed unlock attempts are logged locally with their time. This project does **not** take covert intruder photos.
- Vault screens use `FLAG_SECURE`; temporary decrypted video previews are removed after playback.
- The Backup screen creates a portable, passphrase-encrypted archive of photos and videos and re-encrypts media with the destination phone's Keystore on restore. Secret Notes are not included in that archive. A system document provider (including a Drive provider if installed and chosen) can be selected; there is no direct Drive account integration.

## Current scope notes

The referenced UI screenshot was not included with the supplied text, so the app follows the written white/orange design brief and bundles Hind Siliguri under the SIL Open Font License. Gallery import uses Android's system document picker. The app asks before removing selected originals; whether removal succeeds depends on the selected provider's write/delete permission. Video/photo restoration to Gallery requires Android 10 or later. Vault copies remain encrypted either way.

This build does not include PDF/document/audio import, hidden-launcher-component switching, a fake crash screen, premium purchases, a dark-mode preference, or covert camera capture. The launcher label and calculator-style icon are camouflage only and do not protect against device inspection, a compromised OS, or clearing the app's data.

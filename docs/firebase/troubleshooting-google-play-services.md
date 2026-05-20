# Fix: Failed to get service from broker / Unknown calling package name

```
Failed to get service from broker
java.lang.SecurityException: Unknown calling package name 'com.google.android.gms'
```

This comes from **Google Play Services** when the app uses **Google Sign-In** (Credential Manager). It is usually an **environment or Firebase config** issue, not a bug in your Home/Firestore code.

---

## Fix 1 — Use a Google Play emulator (most common)

Many emulators are **AOSP** images without the Play Store. Google Sign-In will fail on those.

1. Android Studio → **Device Manager**
2. Create a new virtual device
3. Pick a system image that shows **Google Play** in the Play Store column (not "Google APIs" only)
4. Examples: `Pixel 8` + `API 35` with **Google Play**
5. Cold boot the new AVD → open **Play Store** → sign in with a Google account
6. Settings → Apps → **Google Play Services** → ensure it is updated

---

## Fix 2 — Register SHA-1 in Firebase (required for Google Sign-In)

Your `app/google-services.json` currently has **empty `oauth_client` arrays**. After adding SHA-1, re-download the file — `oauth_client` should be populated.

### Get debug SHA-1 (Windows, project root)

```powershell
cd D:\Newfolder
.\gradlew.bat signingReport
```

Copy **SHA-1** under `Variant: debug` for `com.example.jetpacktutorial`.

### Add to Firebase

1. [Firebase Console](https://console.firebase.google.com/) → project **simpleroomapp-46c79**
2. **Project settings** → **Your apps** → Android app `com.example.jetpacktutorial`
3. **Add fingerprint** → paste SHA-1 → Save
4. **Authentication** → **Sign-in method** → enable **Google**
5. **Download** new `google-services.json` → replace `app/google-services.json`
6. Rebuild and reinstall the app

### Web client ID (already in `build.gradle.kts`)

`WEB_CLIENT_ID` must be the **Web client** OAuth ID from the same Firebase/Google Cloud project (used by Credential Manager). In Google Cloud Console → **APIs & Services** → **Credentials**, confirm a **Web application** client exists.

---

## Fix 3 — Physical device

1. Install/update **Google Play Services** from Play Store
2. Device must be signed into a Google account
3. Use a **debug build** signed with the keystore whose SHA-1 you added to Firebase

---

## Test Firestore without Google Sign-In

While fixing Play Services / SHA-1:

1. On the login screen, tap **Continue as Guest** (anonymous Firebase Auth)
2. Open **Home** — matches and leaderboard load from Firestore if data is seeded

Guest mode does **not** use Credential Manager, so it avoids this broker error.

---

## Known noisy log

On some API 34–35 emulators, this stack trace appears in Logcat even when the app still works. If **Guest sign-in** and **Firestore** work but only **Google** fails, focus on Fix 1 and Fix 2.

---

## Checklist

| Step | Done? |
|------|-------|
| Emulator has **Google Play** icon on system image | |
| Play Store signed in on emulator | |
| SHA-1 added in Firebase for `com.example.jetpacktutorial` | |
| Google provider enabled in Firebase Authentication | |
| New `google-services.json` with non-empty `oauth_client` | |
| App reinstalled after replacing `google-services.json` | |

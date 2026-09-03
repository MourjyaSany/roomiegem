# Trust No One — Flat 2.0 (Android)

A one-phone party game for 1–10 roommates. Everything is bundled: no server, no
accounts, no network. The manifest deliberately has **no INTERNET permission**.

The whole game is one file: `app/src/main/assets/index.html`. The Android side is
a ~90 line WebView shell.

---

## Getting an APK

### Option 1 — GitHub Actions (nothing to install)

1. Create a new repository on GitHub and push this folder to it (branch `main`).
2. Open the **Actions** tab. The `Build APK` workflow starts on its own; if not,
   press **Run workflow**.
3. When it finishes (2–4 min), open the run and download the
   **TrustNoOne-apk** artifact. Unzip it — that's your APK.
4. Send the APK to whoever. On their phone: allow "install from unknown sources"
   for whatever app they opened it with, then install.

The workflow is `.github/workflows/build-apk.yml`. It uses the runner's Android
SDK, so there is no `gradlew` wrapper jar in this repo to keep it clean.

### Option 2 — Android Studio

Open this folder as a project, let it sync, press Run. Studio will generate the
Gradle wrapper for you. To get a file you can share:
**Build → Build Bundle(s)/APK(s) → Build APK(s)**.

### Option 3 — Command line

Needs JDK 17 and the Android SDK (`ANDROID_HOME` set):

```bash
gradle wrapper          # once, to create ./gradlew
./gradlew assembleDebug
# app/build/outputs/apk/debug/TrustNoOne-debug-2.0.apk
```

### Option 4 — no build at all

`index.html` runs standalone. Open it in Chrome on the phone and use
**⋮ → Add to Home screen**. You lose the launcher icon polish and the back-button
handling, but the game is identical and still works with the wifi off.

---

## A note on signing

`assembleDebug` signs with Android's debug key. That is fine for passing the APK
around a flat. If you ever want a longer-lived build, generate a keystore and add
a `signingConfigs` block to `app/build.gradle` — don't commit the keystore.

---

## What the shell actually does

| Concern | Handling |
| --- | --- |
| Screen sleeping mid-round | `FLAG_KEEP_SCREEN_ON` |
| Hardware back | Asks the game first (`window.__androidBack`) → returns to the menu. Press again at the menu to exit. |
| System font scaling | `setTextZoom(100)` so the fixed layout doesn't break |
| Click sounds | `setMediaPlaybackRequiresUserGesture(false)` for Web Audio |
| Rotation | Locked to portrait |
| File/content access | Disabled |

## Changing the game

Edit `app/src/main/assets/index.html` and rebuild. Scenarios, missions, mini
tasks, chaos cards and decoy intel are plain arrays near the top of the
`<script>` block — adding your own is a one-line job each.

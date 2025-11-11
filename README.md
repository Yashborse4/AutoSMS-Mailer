# SmsEmailForwarder

Private Android app that listens for incoming SMS and forwards content to an email address via SMTP (no backend server).

What it does
- Receives SMS via BroadcastReceiver
- Starts a ForegroundService to send the SMS to your email using SMTP (JavaMail)
- Stays reliable by using a foreground notification while sending

Permissions required
- RECEIVE_SMS, READ_SMS, READ_PHONE_STATE (sometimes needed), RECEIVE_MMS (optional)
- POST_NOTIFICATIONS (Android 13+)
- INTERNET
- Foreground service permission

Setup steps
1) Open in Android Studio (Giraffe+/AGP 8.5) and Sync
2) Fill SMTP credentials in `app/src/main/java/com/example/smsemailforwarder/smtp/EmailConfig.kt`
   - For Gmail: enable 2FA, generate an App Password, and use `smtp.gmail.com` with port 587 (STARTTLS) or 465 (SSL)
3) Install on device and open the app once to:
   - Grant runtime permissions (SMS, Notifications)
   - Allow ignoring Battery Optimizations
   - Manually enable Auto-start on OEM devices (Xiaomi/Oppo/Vivo/Realme/Samsung)

Build APK
- Android Studio: Build > Build Bundle(s)/APK(s) > Build APK(s)
- CLI: `gradlew.bat assembleRelease` (Windows) or `./gradlew assembleRelease`
- Output: `app/build/outputs/apk/release/app-release.apk`

Notes
- This app stores SMTP credentials in plain SharedConfig (constants by default). Consider securing/separating credentials for production.
- Android may still kill background tasks; using a ForegroundService from the SMS receiver is the most reliable approach.

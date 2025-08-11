

1. Setup service-json file: Add the Firebase `google-services.json` configuration file to your Android project at `android/app/` directory.

2. Get FCM token: Retrieve the FCM token from your Android app's UI (typically via `FirebaseMessaging.getInstance().getToken()`) and Google Auth token using `gcloud auth print-access-token` CLI command.

3. Thunder Client testing: Use Thunder Client (VS Code extension) to test notifications with the provided JSON format.

4. Notification format: Ensure notifications follow the exact structure with `token`, `data` (title, body, action, etc.), and `android` priority.

5. Firebase project setup: Confirm Firebase project is properly configured for Android.

6. AndroidManifest.xml: Add required permissions and service declarations for FCM.

7. Firebase dependencies: Include `com.google.firebase:firebase-messaging` in `build.gradle`.

8. Handling intents: Configure intent filters for notification actions ("Approve"/"Reject").

9. Notification channels: Create a high-priority Android notification channel for alarms.

10. Sound handling: Implement custom sound for notifications (place sound file in `res/raw/`).

11. Data payload handling: Ensure your app can process the `modelKey` and other custom data fields.

12. Authorization header: When sending requests, include `Authorization: Bearer <access_token>`.

13. Endpoint URL: Use correct FCM endpoint: `https://fcm.googleapis.com/v1/projects/{project_id}/messages:send`.

14. Content-Type: Set header to `application/json`.

15. Error handling: Implement response parsing for FCM errors (quota limits, invalid tokens, etc.).

16. Token refresh: Handle FCM token refresh scenarios in your app.

17. Background handling: Implement `FirebaseMessagingService` to process messages when app is in background.

18. Click actions: Configure pending intents for notification click behavior.

19. Local testing: Test with both foreground and background app states.

20. Logging: Add debug logging for token generation and notification receipt.


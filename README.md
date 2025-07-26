# 🔔 React Native Notifications (Android) with Firebase Messaging + Notifee

This project implements push notifications in a React Native CLI (Android-only) app using Firebase Cloud Messaging (FCM) and Notifee, with support for foreground, background, and killed modes, as well as custom sound and action buttons.

---

## ✅ Step-by-Step Setup Instructions

### Step 1: Create a New React Native CLI Project
- Use `npx react-native init YourAppName` to initialize a new project.

---

### Step 2: Install Required Packages
- Install the following npm packages:
  - `@react-native-firebase/app`
  - `@react-native-firebase/messaging`
  - `@notifee/react-native`
  - `react-native-mmkv` (optional for storing notification history)

---

### Step 3: Set Up Firebase
- Go to [Firebase Console](https://console.firebase.google.com/).
- Create a new Firebase project.
- Register your Android app with your app’s package name.
- Download the `google-services.json` file and place it inside the `android/app` directory.

---

### Step 4: Configure Android Project for Firebase
- In the `android/build.gradle` file, add the Google services classpath in the `buildscript` section.
        classpath("com.android.tools.build:gradle:8.2.2") // ✅ specify version
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.gms:google-services:4.3.15") // ✅ Firebase plugin

- In the `android/app/build.gradle` file:
- 
        apply plugin: "com.google.gms.google-services" // ✅ Correct, should only appear once

        dependencies {
         implementation platform('com.google.firebase:firebase-bom:34.0.0')
         implementation 'com.google.firebase:firebase-analytics'
         implementation 'com.google.firebase:firebase-messaging' 
         }
         apply plugin: 'com.google.gms.google-services' // add this line at the bottom
  

  - Apply the Google services plugin.
  - Add Firebase Messaging as a dependency.

---

### Step 5: Add a Custom Notification Sound
- Create a folder named `raw` inside `android/app/src/main/res/` if it doesn't exist.
- Add your `.mp3` or `.wav` file into this `raw` folder.
- Make sure the file name is in lowercase and has no spaces or special characters.

---

### Step 6: Create a JS Module to Handle Notifications
- Set up notification handling logic using Notifee and Firebase Messaging.
- Handle both foreground and background messages.
- Use Notifee to display custom notifications and action buttons.

---

### Step 7: Register Notification Listeners in Your App
- Inside your main App component, call the function to register Firebase listeners when the app starts.

---

### Step 8: Handle Notifications in Killed Mode
- In the `index.tsx` or `index.js` file, use `setBackgroundMessageHandler` from Firebase Messaging to process notifications when the app is terminated.
- Call the same display notification logic inside this handler.

---

### Step 9: Use FCM Data Payload for Notifications
- Always send `data`-only messages from FCM to ensure notifications are handled by your custom JS code.
- Include keys like `title`, `body`, `sound`, and `channelId` inside the data payload.
- Do not use the `notification` field for full control in React Native and to ensure support in killed mode.

---

### Step 10: Send Test Notifications
- You can use Postman, a Node.js script, or PowerShell to send test FCM messages.
- Use your Firebase project server key in the request header.
- Use the device’s FCM token in the body.
- Make sure the payload uses `data` only.

---

### Step 11: (Optional) Store Notification History
- Use `react-native-mmkv` to persist notification data locally.
- Store incoming notification data into MMKV storage when a notification is received.
- You can later read and display this history in a UI screen.

---

### Step 12: Build and Run Your App
- Run the app on Android using:
  - `npx react-native run-android`
- Ensure notification permissions are granted and the device is online.
- Test notifications in all app states (foreground, background, killed).

---

## ✅ Completed Features

- Push notifications via Firebase
- Custom notification sound
- Action buttons
- Works in foreground, background, and killed mode
- Optional local storage of notifications with MMKV

---

## 📌 Notes

- Always use `data` payload to guarantee killed mode delivery.
- Notifee is required to display custom notifications in all app states.
- Firebase’s native `notification` payload is not reliable for React Native apps in killed mode.

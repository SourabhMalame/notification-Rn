// import React, { useEffect, useState } from 'react';
// import { StyleSheet, Text, View, Platform, Alert } from 'react-native';
// import messaging from '@react-native-firebase/messaging';
// import notifee, { AndroidImportance } from '@notifee/react-native';
// import { getFcmToken } from './components/utils/getFcmToken';

// function App() {
//   const [fcmToken, setFcmToken] = useState<string | null>(null);

//   useEffect(() => {
//     const fetchToken = async () => {
//       const token = await getFcmToken();
//       setFcmToken(token);
//     };

//     fetchToken();

//     const unsubscribe = messaging().onMessage(async remoteMessage => {
//       console.log('📲 FCM Foreground:', remoteMessage);

//       await displayLocalNotification(remoteMessage);
//     });

//     return unsubscribe;
//   }, []);

//   return (
//     <View style={styles.container}>
//       <Text style={{ fontWeight: 'bold' }}>FCM Token:</Text>
//       <Text selectable style={styles.tokenText}>
//         {fcmToken ?? 'Fetching token...'}
//       </Text>
//     </View>
//   );
// }

// async function displayLocalNotification(remoteMessage: any) {
//   await notifee.requestPermission();

//   await notifee.createChannel({
//     id: 'default',
//     name: 'Default Channel',
//     importance: AndroidImportance.HIGH,
//   });

//   await notifee.displayNotification({
//     title: remoteMessage.notification?.title || 'New Notification',
//     body: remoteMessage.notification?.body || 'You have a message!',
//     android: {
//       channelId: 'default',
//       smallIcon: 'ic_launcher', // make sure you have this icon in android/app/src/main/res
//     },
//   });
// }

// const styles = StyleSheet.create({
//   container: {
//     flex: 1,
//     justifyContent: 'center',
//     alignItems: 'center',
//     padding: 16,
//   },
//   tokenText: {
//     marginTop: 10,
//     fontSize: 12,
//     color: '#333',
//   },
// });

// export default App;

import React, { useEffect, useState } from 'react';
import { StyleSheet, Text, View, Platform, Alert, Button } from 'react-native';
import messaging from '@react-native-firebase/messaging';
import notifee, { AndroidImportance } from '@notifee/react-native';
import { getFcmToken } from './components/utils/getFcmToken';

function App() {
  const [fcmToken, setFcmToken] = useState<string | null>(null);

  useEffect(() => {
    const fetchToken = async () => {
      const token = await getFcmToken();
      setFcmToken(token);
    };

    fetchToken();

    const unsubscribe = messaging().onMessage(async remoteMessage => {
      console.log('📲 FCM Foreground:', remoteMessage);

      await displayLocalNotification(remoteMessage);
    });

    return unsubscribe;
  }, []);

  const triggerLocationNotification = async () => {
    await notifee.requestPermission();

    await notifee.createChannel({
      id: 'location',
      name: 'Location Alerts',
      importance: AndroidImportance.HIGH,
    });

    await notifee.displayNotification({
      title: '📍 Location Alert',
      body: 'You’ve entered a tracked area!',
      android: {
        channelId: 'location',
        smallIcon: 'ic_launcher',
      },
    });
  };

  return (
    <View style={styles.container}>
      <Text style={{ fontWeight: 'bold' }}>FCM Token:</Text>
      <Text selectable style={styles.tokenText}>
        {fcmToken ?? 'Fetching token...'}
      </Text>

      {/* 🔘 Button to trigger location-based notification */}
      <View style={{ marginTop: 20 }}>
        <Button title="Send Location Notification" onPress={triggerLocationNotification} />
      </View>
    </View>
  );
}

async function displayLocalNotification(remoteMessage: any) {
  await notifee.requestPermission();

  await notifee.createChannel({
    id: 'default',
    name: 'Default Channel',
    importance: AndroidImportance.HIGH,
  });

  await notifee.displayNotification({
    title: remoteMessage.notification?.title || 'New Notification',
    body: remoteMessage.notification?.body || 'You have a message!',
    android: {
      sound: remoteMessage.notification?.sound || 'default',
      channelId: 'default',
      smallIcon: 'ic_launcher', // make sure you have this icon in android/app/src/main/res
    },
  });
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
  },
  tokenText: {
    marginTop: 10,
    fontSize: 12,
    color: '#333',
  },
});

export default App;

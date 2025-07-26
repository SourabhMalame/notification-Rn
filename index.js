import { AppRegistry } from 'react-native';
import messaging from '@react-native-firebase/messaging';
import App from './App';
import { name as appName } from './app.json';
import notifee, { AndroidImportance } from '@notifee/react-native';

messaging().setBackgroundMessageHandler(async remoteMessage => {
    console.log('📥 FCM Background/Killed Message:', remoteMessage);

    await notifee.createChannel({
        id: 'default',
        name: 'Default Channel',
        importance: AndroidImportance.HIGH,
    });

    await notifee.displayNotification({
        title: remoteMessage.notification?.title,
        body: remoteMessage.notification?.body,
        android: {
            channelId: 'default',
            smallIcon: 'ic_launcher',
        },
    });
});

AppRegistry.registerComponent(appName, () => App);

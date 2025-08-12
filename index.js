// index.js
import { AppRegistry } from 'react-native';
import messaging from '@react-native-firebase/messaging';
import App from './App';
import { name as appName } from './app.json';
import { MMKV } from 'react-native-mmkv';

const storage = new MMKV();

messaging().setBackgroundMessageHandler(async remoteMessage => {
  storage.set(`notif_${Date.now()}`, JSON.stringify(remoteMessage));
  console.log(storage.getAllKeys())
});


AppRegistry.registerComponent(appName, () => App);

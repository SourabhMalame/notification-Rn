import messaging from '@react-native-firebase/messaging';

export async function getFcmToken(): Promise<string | null> {
  try {
    const authStatus = await messaging().requestPermission();
    const enabled =
      authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
      authStatus === messaging.AuthorizationStatus.PROVISIONAL;

    if (enabled) {
      const token = await messaging().getToken();
      console.log('✅ FCM Token:', token);
      return token;
    }
  } catch (e) {
    console.error('FCM Token Error', e);
  }
  return null;
}

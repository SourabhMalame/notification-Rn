import { PermissionsAndroid, Platform, Alert } from 'react-native';

export async function requestNotificationPermission() {
    console.log("requesting permission")
    if (Platform.OS === 'android' && Platform.Version >= 33) {

        try {
            const granted = await PermissionsAndroid.request(
                PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS,
                {
                    title: 'Notification Permission',
                    message: 'This app needs notification permission to send you notifications.',
                    buttonPositive: 'Allow',
                    buttonNegative: 'Deny',
                }
            );

            console.log("requesting permission inside if")

            if (granted === PermissionsAndroid.RESULTS.GRANTED) {
                console.log('Notification permission granted');
            } else {
                console.log('Notification permission denied');
                Alert.alert('Permission Denied', 'You will not receive notifications.');
            }
        } catch (err) {
            console.warn(err);
        }
    } else {
        // For iOS or Android versions < 13, notification permission is granted by default
        console.log('Notification permission not required or already granted');
    }
}

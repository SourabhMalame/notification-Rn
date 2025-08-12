import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  DeviceEventEmitter,
  FlatList
} from 'react-native';
import { MMKV } from 'react-native-mmkv';
import { SafeAreaView } from 'react-native-safe-area-context';
import { NativeEventEmitter, NativeModules } from 'react-native';


const eventEmitter = new NativeEventEmitter(NativeModules.BackendModule);

export default function App() {

  const storage = new MMKV();

  const [notifications, setNotifications] = useState([]);
  const [data, setData] = useState(null)
 
  const notificationsData = JSON.parse(storage.getString('notifications') || '[]');
  console.log(notificationsData)

  // Load from MMKV at start
  useEffect(() => {
    loadNotifications();
  }, []);

  // Listen to native events
  useEffect(() => {
    const nativeListener = eventEmitter.addListener(
      'FCMNotificationReceived',
      (event) => saveNotification(event.data || event)

    );

    return () => nativeListener.remove();
  }, []);

  // Listen to RN DeviceEventEmitter
  useEffect(() => {
    const rnListener = DeviceEventEmitter.addListener(
      'FCMNotificationReceived',
      (notification) => saveNotification(notification)
    );
    return () => rnListener.remove();
  }, []);

  // Auto-refresh every 3 seconds in case background process updates MMKV
  useEffect(() => {
    const intervalId = setInterval(() => {
      loadNotifications();
    }, 3000);
    return () => clearInterval(intervalId);
  }, []);

  const loadNotifications = () => {
    const existing = storage.getString('notifications') || '[]';
    setNotifications(JSON.parse(existing));
  };

  const saveNotification = (notification) => {
    const newNotification = {
      ...notification,
      isRead: false,
      id: notification.id || Date.now().toString()
    };

    const existing = storage.getString('notifications') || '[]';
    const parsed = JSON.parse(existing);

    // Avoid duplicates by ID
    if (!parsed.find(n => n.id === newNotification.id)) {
      const updated = [newNotification, ...parsed].slice(0, 50);
      storage.set('notifications', JSON.stringify(updated));
      setNotifications(updated);
    }
  };

  const markAsRead = (id) => {
    const updated = notifications.map(n =>
      n.id === id ? { ...n, isRead: true } : n
    );
    storage.set('notifications', JSON.stringify(updated));
    setNotifications(updated);
  };

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: '#fff' }}>
      <Text style={{ fontSize: 20, fontWeight: 'bold', margin: 10 }}>
        Notifications ({notifications.filter(n => !n.isRead).length})
      </Text>

      <FlatList
        data={notifications}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <TouchableOpacity
            style={{
              padding: 10,
              borderBottomWidth: 1,
              borderBottomColor: '#ddd',
              backgroundColor: item.isRead ? '#f8f8f8' : '#fff'
            }}
            onPress={() => markAsRead(item.id)}
          >
            <Text style={{ fontWeight: 'bold' }}>{item.title || 'No title'}</Text>
            <Text>{item.body || 'No body'}</Text>
          </TouchableOpacity>
        )}
      />
    </SafeAreaView>
  );
}

import React, { useEffect, useState } from 'react';
import { View, TouchableOpacity, Text, StyleSheet } from 'react-native';
import { MMKV } from 'react-native-mmkv';

const storage = new MMKV();

const BellIcon = ({ onPress, size = 24, color = '#333' }) => {
    const [unreadCount, setUnreadCount] = useState(0);

    useEffect(() => {
        const checkUnreadNotifications = () => {
            try {
                const existingNotifications = storage.getString('notifications');
                if (existingNotifications) {
                    const parsedNotifications = JSON.parse(existingNotifications);
                    const unreadNotifications = parsedNotifications.filter(notif => !notif.isRead);
                    setUnreadCount(unreadNotifications.length);
                }
            } catch (error) {
                console.error('Error checking unread notifications:', error);
            }
        };

        // Initial check
        checkUnreadNotifications();

        // Periodic check (optional)
        const interval = setInterval(checkUnreadNotifications, 2000);
        return () => clearInterval(interval);
    }, []);

    return (
        <TouchableOpacity onPress={onPress} style={styles.container}>
            <View style={styles.bellContainer}>
                <Text>**</Text>
                {unreadCount > 0 && (
                    <View style={styles.badge}>
                        <Text style={styles.badgeText}>
                            {unreadCount > 99 ? '99+' : unreadCount}
                        </Text>
                    </View>
                )}
            </View>
        </TouchableOpacity>
    );
};

const styles = StyleSheet.create({
    container: {
        padding: 4,
    },
    bellContainer: {
        position: 'relative',
    },
    badge: {
        position: 'absolute',
        top: -6,
        right: -6,
        backgroundColor: '#ff4444',
        borderRadius: 10,
        minWidth: 20,
        height: 20,
        justifyContent: 'center',
        alignItems: 'center',
        paddingHorizontal: 4,
        borderWidth: 2,
        borderColor: '#fff',
    },
    badgeText: {
        color: 'white',
        fontSize: 10,
        fontWeight: 'bold',
    },
});

export default BellIcon;

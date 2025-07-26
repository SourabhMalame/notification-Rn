import React, { useEffect, useState } from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { getFcmToken } from './components/utils/getFcmToken';

function App() {
  const [fcmToken, setFcmToken] = useState<string | null>(null);

  useEffect(() => {
    const fetchToken = async () => {
      const token = await getFcmToken();
      setFcmToken(token);
    };

    fetchToken();
  }, []);

  return (
    <View style={styles.container}>
      <Text>FCM Token:</Text>
      <Text selectable style={styles.tokenText}>
        {fcmToken ?? 'Fetching token...'}
      </Text>
    </View>
  );
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

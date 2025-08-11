import React, { useEffect, useState } from 'react';
import { SafeAreaView, ScrollView, StyleSheet, Text, View } from 'react-native';
import { getFcmToken } from './components/utils/getFcmToken';
import FlatListComponent from './components/extra/flatlist'; // ensure correct filename

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
    <SafeAreaView style={styles.safeArea}>
      <View style={styles.tokenContainer}>
        <Text style={styles.heading}>FCM Token:</Text>
        <Text selectable style={styles.tokenText}>
          {fcmToken ?? 'Fetching token...'}
        </Text>
      </View>

      {/* FlatListComponent takes full space */}
      {/* <View style={styles.listContainer}> */}
        <FlatListComponent />
      {/* </View> */}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#fff',
  },
  tokenContainer: {
    padding: 16,
    alignItems: 'center',
  },
  heading: {
    fontWeight: 'bold',
    fontSize: 16,
  },
  tokenText: {
    marginTop: 8,
    fontSize: 12,
    color: '#333',
  },
  listContainer: {
    flex: 1,
    paddingHorizontal: 10,
  },
});

export default App;

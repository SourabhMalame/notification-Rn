import React, { useEffect, useState } from 'react';
import {
    FlatList,
    Image,
    StyleSheet,
    Text,
    View,
    ActivityIndicator,
} from 'react-native';

interface ApiData {
    albumId: number;
    id: number;
    title: string;
    url: string;
    thumbnailUrl: string;
}

const FlatListComponent = () => {
    const [mainData, setMainData] = useState<ApiData[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const res = await fetch('https://jsonplaceholder.typicode.com/photos?_limit=50');
                const json = await res.json();
                setMainData(json);
            } catch (error) {
                console.error('Error fetching data:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    if (loading) {
        return (
            <View style={styles.centered}>
                <ActivityIndicator size="large" color="#007aff" />
            </View>
        );
    }

    return (
        <View style={styles.container}>
            <Text style={styles.heading}>Photo List</Text>
            <FlatList
                data={mainData}
                keyExtractor={(item) => item.id.toString()}
                renderItem={({ item }) => (
                    <View style={styles.card}>
                        <Image source={{ uri: item.thumbnailUrl }} style={styles.image} />
                        <Text style={styles.title}>{item.title}</Text>
                    </View>
                )}
            />
        </View>
    );
};

export default FlatListComponent;

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 16,
        paddingTop: 40,
        backgroundColor: '#f4f4f4',
    },
    heading: {
        fontSize: 20,
        fontWeight: 'bold',
        marginBottom: 12,
    },
    card: {
        flexDirection: 'row',
        backgroundColor: '#fff',
        marginBottom: 10,
        padding: 10,
        borderRadius: 8,
        elevation: 2,
        alignItems: 'center',
    },
    image: {
        width: 60,
        height: 60,
        marginRight: 10,
        borderRadius: 4,
    },
    title: {
        flex: 1,
        fontSize: 14,
        color: '#333',
    },
    centered: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
});

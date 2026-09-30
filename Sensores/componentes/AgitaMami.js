import { useEffect, useState, useRef } from "react";
import { View, Text, StyleSheet, Animated } from "react-native";
import { Accelerometer } from "expo-sensors";

const COLORES = [
    '#efefef', 
    '#FF5252', 
    '#FF9800', 
    '#FFEB3B', 
    '#4CAF50', 
    '#2196F3', 
    '#9C27B0'  
];

export default function AccelerometerSensor() {
    const [datos, setDatos] = useState({ x: 0, y: 0, z: 0 });

    const animacionColor = useRef(new Animated.Value(0)).current;
    const ultimoAgite = useRef(0);

    useEffect(() => {
        let indiceColorActual = 0; 

        const subscribir = Accelerometer.addListener(measurements => {
            setDatos(measurements);

            const { x, y, z } = measurements;
            
            // Fórmula de magnitud del movimiento
            const aceleracionTotal = Math.sqrt(x * x + y * y + z * z);

            if (aceleracionTotal > 1.8) {
                const ahora = Date.now();
                
                // Evitamos que cambie muchas veces en menos de 800 milisegundos
                if (ahora - ultimoAgite.current > 800) {
                    ultimoAgite.current = ahora;

                    indiceColorActual = (indiceColorActual + 1) % COLORES.length;

                    Animated.timing(animacionColor, {
                        toValue: indiceColorActual,
                        duration: 500, // Medio segundo de transición
                        useNativeDriver: false,
                    }).start();
                }
            }
        });

        Accelerometer.setUpdateInterval(50);

        return () => {
            subscribir.remove();
        }
    }, []);

    const colorDeFondo = animacionColor.interpolate({
        inputRange: [0, 1, 2, 3, 4, 5, 6],
        outputRange: COLORES,
    });

    return (
        <Animated.View style={[styles.container, { backgroundColor: colorDeFondo }]}>
            <Text style={styles.title}>
                Acelerómetro
            </Text>
            <View style={styles.card}>
                <Text style={styles.axis}>X</Text>
                <Text style={styles.value}>{datos.x.toFixed(2)}</Text>
            </View>
            <View style={styles.card}>
                <Text style={styles.axis}>Y</Text>
                <Text style={styles.value}>{datos.y.toFixed(2)}</Text>
            </View>
            <View style={styles.card}>
                <Text style={styles.axis}>Z</Text>
                <Text style={styles.value}>{datos.z.toFixed(2)}</Text>
            </View>
        </Animated.View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        padding: 25,
    },
    title: {
        fontSize: 35,
        textAlign: 'center',
        marginBottom: 35,
        color: "#3a4a5a",
        fontWeight: 'bold', 
    },
    card: {
        backgroundColor: "#fff",
        padding: 20,
        marginBottom: 20,
        flexDirection: 'row',
        justifyContent: 'space-between',
        borderRadius: 12, 
        elevation: 4, 
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 4,
    },
    axis: {
        fontSize: 24,
        fontWeight: 'bold',
        color: "#333",
    },
    value: {
        fontSize: 24,
        fontWeight: 'bold',
        color: "#333",
    },
});
import { useEffect, useRef } from "react";
import { View, Text, StyleSheet, Animated } from "react-native";

const SplashPersonalizado = ({ onTerminado }) => {
    const escala = useRef(new Animated.Value(0.5)).current;
    const opacidad = useRef(new Animated.Value(0)).current;

    useEffect(() => {
        Animated.sequence([
            // 1. Aparece con un pequeño "rebote" (spring) y fade-in
            Animated.parallel([
                Animated.spring(escala, {
                    toValue: 1,
                    friction: 4,
                    useNativeDriver: true,
                }),
                Animated.timing(opacidad, {
                    toValue: 1,
                    duration: 500,
                    useNativeDriver: true,
                }),
            ]),
            // 2. Se queda visible un momento
            Animated.delay(1000),
            // 3. Se desvanece antes de mostrar la app
            Animated.timing(opacidad, {
                toValue: 0,
                duration: 400,
                useNativeDriver: true,
            }),
        ]).start(() => {
            onTerminado(); // avisa a App.js que ya puede mostrar el NavDrawer
        });
    }, []);

    return (
        <View style={styles.container}>
            <Animated.View
                style={[
                    styles.logoContainer,
                    { opacity: opacidad, transform: [{ scale: escala }] },
                ]}
            >
                <View style={styles.circulo}>
                    <Text style={styles.icono}>💼</Text>
                </View>
                <Text style={styles.nombre}>CotizaFácil</Text>
                <Text style={styles.tagline}>Cotizaciones rápidas y profesionales</Text>
            </Animated.View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: "#2c3e50",
        justifyContent: "center",
        alignItems: "center",
    },
    logoContainer: { alignItems: "center" },
    circulo: {
        width: 100,
        height: 100,
        borderRadius: 50,
        backgroundColor: "#27ae60",
        justifyContent: "center",
        alignItems: "center",
        marginBottom: 20,
    },
    icono: { fontSize: 48 },
    nombre: { fontSize: 28, fontWeight: "bold", color: "white" },
    tagline: { fontSize: 13, color: "#bdc3c7", marginTop: 6 },
});

export default SplashPersonalizado;
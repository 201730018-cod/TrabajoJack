import { useEffect, useRef } from "react";
import { SafeAreaView, View, Text, StyleSheet, Animated } from "react-native";

const PantallaInicio = ({ config, cotizaciones }) => {
    const opacidad = useRef(new Animated.Value(0)).current;
    const traslado = useRef(new Animated.Value(20)).current;

    useEffect(() => {
        Animated.parallel([
            Animated.timing(opacidad, {
                toValue: 1,
                duration: 600,
                useNativeDriver: true,
            }),
            Animated.timing(traslado, {
                toValue: 0,
                duration: 600,
                useNativeDriver: true,
            }),
        ]).start();
    }, []);

    const totalCotizado = cotizaciones.reduce((suma, c) => suma + c.total, 0);

    return (
        <SafeAreaView style={styles.container}>
            <Animated.View
                style={[
                    styles.contenido,
                    { opacity: opacidad, transform: [{ translateY: traslado }] },
                ]}
            >
                <Text style={styles.saludo}>Hola 👋</Text>
                <Text style={styles.negocio}>{config.nombreNegocio}</Text>

                <View style={styles.statsContainer}>
                    <View style={styles.statCard}>
                        <Text style={styles.statNumero}>{cotizaciones.length}</Text>
                        <Text style={styles.statLabel}>Cotizaciones</Text>
                    </View>
                    <View style={styles.statCard}>
                        <Text style={styles.statNumero}>${totalCotizado.toFixed(0)}</Text>
                        <Text style={styles.statLabel}>Total cotizado</Text>
                    </View>
                </View>

                <Text style={styles.tip}>
                    Usa el menú lateral para crear una nueva cotización o revisar tu historial.
                </Text>
            </Animated.View>
        </SafeAreaView>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: "#f2f2f2",
        justifyContent: "center",
    },
    contenido: {
        paddingHorizontal: 25,
    },
    saludo: {
        fontSize: 18,
        color: "#666",
    },
    negocio: {
        fontSize: 28,
        fontWeight: "bold",
        color: "#2c3e50",
        marginBottom: 25,
    },
    statsContainer: {
        flexDirection: "row",
        gap: 15,
        marginBottom: 25,
    },
    statCard: {
        flex: 1,
        backgroundColor: "white",
        borderRadius: 12,
        padding: 18,
        alignItems: "center",
    },
    statNumero: {
        fontSize: 24,
        fontWeight: "bold",
        color: "#27ae60",
    },
    statLabel: {
        fontSize: 13,
        color: "#666",
        marginTop: 4,
    },
    tip: {
        color: "#999",
        fontSize: 13,
        textAlign: "center",
    },
});

export default PantallaInicio;
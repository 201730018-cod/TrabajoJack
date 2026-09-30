import { useRef, useState } from "react";
import { View, Text, TouchableOpacity, StyleSheet, Animated } from "react-native";

const Dados = () => {
    const [numero, setNumero] = useState(1);
    const rotacion = useRef(new Animated.Value(0)).current;

    const lanzarDado = () => {
        // Genera un número random del 1 al 6
        const resultado = Math.floor(Math.random() * 6) + 1;

        // Resetea la rotación a 0 antes de animar
        rotacion.setValue(0);

        Animated.timing(rotacion, {
            toValue: 1,
            duration: 500,
            useNativeDriver: true,
        }).start(() => {
            // Cuando termina la animación, actualiza el número mostrado
            setNumero(resultado);
        });
    };

    const rotacionInterpolada = rotacion.interpolate({
        inputRange: [0, 1],
        outputRange: ["0deg", "720deg"], // dos vueltas completas
    });

    return (
        <View style={styles.container}>
            <Animated.View
                style={[
                    styles.dado,
                    { transform: [{ rotate: rotacionInterpolada }] },
                ]}
            >
                <Text style={styles.numero}>{numero}</Text>
            </Animated.View>

            <TouchableOpacity style={styles.boton} onPress={lanzarDado}>
                <Text style={styles.botonTexto}>Lanzar dado</Text>
            </TouchableOpacity>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
        gap: 30,
    },
    dado: {
        width: 100,
        height: 100,
        backgroundColor: "white",
        borderRadius: 16,
        justifyContent: "center",
        alignItems: "center",
        borderWidth: 2,
        borderColor: "#333",
        shadowColor: "#000",
        shadowOffset: { width: 0, height: 4 },
        shadowOpacity: 0.3,
        shadowRadius: 4,
        elevation: 5,
    },
    numero: {
        fontSize: 48,
        fontWeight: "bold",
    },
    boton: {
        backgroundColor: "#4A90E2",
        paddingVertical: 12,
        paddingHorizontal: 30,
        borderRadius: 8,
    },
    botonTexto: {
        color: "white",
        fontSize: 18,
        fontWeight: "600",
    },
});

export default Dados;
import { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, StyleSheet, Keyboard, TouchableWithoutFeedback } from "react-native";

// Tasas fijas (base: 1 USD)
const TASAS = {
    USD: 1,
    MXN: 18.5,
    EUR: 0.92,
    GBP: 0.79,
};

const MONEDAS = Object.keys(TASAS);

const Divisas = () => {
    const [monto, setMonto] = useState("");
    const [monedaOrigen, setMonedaOrigen] = useState("USD");
    const [monedaDestino, setMonedaDestino] = useState("MXN");
    const [resultado, setResultado] = useState(null);

    const convertir = () => {
        const montoNum = parseFloat(monto);

        if (!montoNum) {
            alert("Ingresa un monto válido");
            return;
        }

        // Convierte a USD primero, luego a la moneda destino
        const enUSD = montoNum / TASAS[monedaOrigen];
        const convertido = enUSD * TASAS[monedaDestino];

        setResultado(convertido.toFixed(2));
    };

    return (
         <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
        <View style={styles.container}>
            <TextInput
                style={styles.input}
                placeholder="Monto a convertir"
                keyboardType="numeric"
                value={monto}
                onChangeText={setMonto}
            />

            <Text style={styles.label}>De:</Text>
            <View style={styles.selector}>
                {MONEDAS.map((moneda) => (
                    <TouchableOpacity
                        key={moneda}
                        style={[
                            styles.opcion,
                            monedaOrigen === moneda && styles.opcionSeleccionada,
                        ]}
                        onPress={() => setMonedaOrigen(moneda)}
                    >
                        <Text
                            style={[
                                styles.opcionTexto,
                                monedaOrigen === moneda && styles.opcionTextoSeleccionado,
                            ]}
                        >
                            {moneda}
                        </Text>
                    </TouchableOpacity>
                ))}
            </View>

            <Text style={styles.label}>A:</Text>
            <View style={styles.selector}>
                {MONEDAS.map((moneda) => (
                    <TouchableOpacity
                        key={moneda}
                        style={[
                            styles.opcion,
                            monedaDestino === moneda && styles.opcionSeleccionada,
                        ]}
                        onPress={() => setMonedaDestino(moneda)}
                    >
                        <Text
                            style={[
                                styles.opcionTexto,
                                monedaDestino === moneda && styles.opcionTextoSeleccionado,
                            ]}
                        >
                            {moneda}
                        </Text>
                    </TouchableOpacity>
                ))}
            </View>

            <TouchableOpacity style={styles.boton} onPress={convertir}>
                <Text style={styles.botonTexto}>Convertir</Text>
            </TouchableOpacity>

            {resultado !== null && (
                <Text style={styles.resultado}>
                    {monto} {monedaOrigen} = {resultado} {monedaDestino}
                </Text>
            )}
        </View>
        </TouchableWithoutFeedback>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 20,
        justifyContent: "center",
    },
    input: {
        borderWidth: 1,
        borderColor: "#999",
        borderRadius: 8,
        padding: 12,
        fontSize: 16,
        backgroundColor: "white",
        marginBottom: 20,
    },
    label: {
        fontSize: 14,
        fontWeight: "600",
        marginBottom: 8,
        color: "#555",
    },
    selector: {
        flexDirection: "row",
        flexWrap: "wrap",
        gap: 8,
        marginBottom: 20,
    },
    opcion: {
        paddingVertical: 8,
        paddingHorizontal: 16,
        borderRadius: 8,
        borderWidth: 1,
        borderColor: "#4A90E2",
    },
    opcionSeleccionada: {
        backgroundColor: "#4A90E2",
    },
    opcionTexto: {
        color: "#4A90E2",
        fontWeight: "600",
    },
    opcionTextoSeleccionado: {
        color: "white",
    },
    boton: {
        backgroundColor: "#27ae60",
        paddingVertical: 14,
        borderRadius: 8,
        alignItems: "center",
        marginTop: 10,
    },
    botonTexto: {
        color: "white",
        fontSize: 18,
        fontWeight: "600",
    },
    resultado: {
        marginTop: 20,
        fontSize: 20,
        fontWeight: "bold",
        textAlign: "center",
        color: "#333",
    },
});

export default Divisas;
import { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, StyleSheet, Keyboard, TouchableWithoutFeedback } from "react-native";

const PORCENTAJES = [10, 15, 20, 25];

const Propinas = () => {
    const [cuenta, setCuenta] = useState("");
    const [porcentaje, setPorcentaje] = useState(15);
    const [personas, setPersonas] = useState("1");

    const cuentaNum = parseFloat(cuenta) || 0;
    const personasNum = parseInt(personas) || 1;

    const propina = cuentaNum * (porcentaje / 100);
    const total = cuentaNum + propina;
    const porPersona = total / personasNum;

    return (
        <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
        <View style={styles.container}>
            <Text style={styles.label}>Total de la cuenta</Text>
            <TextInput
                style={styles.input}
                placeholder="$0.00"
                keyboardType="numeric"
                value={cuenta}
                onChangeText={setCuenta}
            />

            <Text style={styles.label}>Propina</Text>
            <View style={styles.selector}>
                {PORCENTAJES.map((p) => (
                    <TouchableOpacity
                        key={p}
                        style={[
                            styles.opcion,
                            porcentaje === p && styles.opcionSeleccionada,
                        ]}
                        onPress={() => setPorcentaje(p)}
                    >
                        <Text
                            style={[
                                styles.opcionTexto,
                                porcentaje === p && styles.opcionTextoSeleccionado,
                            ]}
                        >
                            {p}%
                        </Text>
                    </TouchableOpacity>
                ))}
            </View>

            <Text style={styles.label}>Dividir entre</Text>
            <TextInput
                style={styles.input}
                placeholder="Número de personas"
                keyboardType="numeric"
                value={personas}
                onChangeText={setPersonas}
            />

            <View style={styles.resultadoContainer}>
                <View style={styles.fila}>
                    <Text style={styles.filaLabel}>Propina</Text>
                    <Text style={styles.filaValor}>${propina.toFixed(2)}</Text>
                </View>
                <View style={styles.fila}>
                    <Text style={styles.filaLabel}>Total</Text>
                    <Text style={styles.filaValor}>${total.toFixed(2)}</Text>
                </View>
                <View style={[styles.fila, styles.filaDestacada]}>
                    <Text style={styles.filaLabelDestacada}>Por persona</Text>
                    <Text style={styles.filaValorDestacado}>${porPersona.toFixed(2)}</Text>
                </View>
            </View>
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
    label: {
        fontSize: 14,
        fontWeight: "600",
        marginBottom: 8,
        color: "#555",
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
    selector: {
        flexDirection: "row",
        gap: 8,
        marginBottom: 20,
    },
    opcion: {
        flex: 1,
        paddingVertical: 10,
        borderRadius: 8,
        borderWidth: 1,
        borderColor: "#27ae60",
        alignItems: "center",
    },
    opcionSeleccionada: {
        backgroundColor: "#27ae60",
    },
    opcionTexto: {
        color: "#27ae60",
        fontWeight: "600",
    },
    opcionTextoSeleccionado: {
        color: "white",
    },
    resultadoContainer: {
        backgroundColor: "white",
        borderRadius: 12,
        padding: 20,
        marginTop: 10,
        shadowColor: "#000",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 4,
        elevation: 3,
    },
    fila: {
        flexDirection: "row",
        justifyContent: "space-between",
        paddingVertical: 8,
    },
    filaLabel: {
        fontSize: 16,
        color: "#666",
    },
    filaValor: {
        fontSize: 16,
        fontWeight: "600",
    },
    filaDestacada: {
        borderTopWidth: 1,
        borderTopColor: "#eee",
        marginTop: 8,
        paddingTop: 12,
    },
    filaLabelDestacada: {
        fontSize: 18,
        fontWeight: "bold",
    },
    filaValorDestacado: {
        fontSize: 18,
        fontWeight: "bold",
        color: "#27ae60",
    },
});

export default Propinas;
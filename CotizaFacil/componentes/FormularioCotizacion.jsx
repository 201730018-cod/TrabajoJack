import { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, StyleSheet, Keyboard, TouchableWithoutFeedback } from "react-native";

const FormularioCotizacion = ({ config, onCalcular }) => {
    const [cliente, setCliente] = useState("");
    const [horas, setHoras] = useState("");
    const [tarifa, setTarifa] = useState(config.tarifaDefault);
    const [iva, setIva] = useState(config.ivaDefault);
    const [notas, setNotas] = useState("");

    const validarYCalcular = () => {
        if (cliente.trim() === "") {
            alert("Ingresa el nombre del cliente");
            return;
        }

        const horasNum = parseFloat(horas);
        const tarifaNum = parseFloat(tarifa);
        const ivaNum = parseFloat(iva);

        if (!horasNum || horasNum <= 0) {
            alert("Ingresa un número de horas válido");
            return;
        }

        if (!tarifaNum || tarifaNum <= 0) {
            alert("Ingresa una tarifa válida");
            return;
        }

        if (isNaN(ivaNum) || ivaNum < 0 || ivaNum > 100) {
            alert("El IVA debe estar entre 0 y 100");
            return;
        }

        const subtotal = horasNum * tarifaNum;
        const montoIva = subtotal * (ivaNum / 100);
        const total = subtotal + montoIva;

        onCalcular({
            cliente: cliente.trim(),
            horas: horasNum,
            tarifa: tarifaNum,
            iva: ivaNum,
            subtotal,
            montoIva,
            total,
            notas: notas.trim(),
        });
    };

    return (
        <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
            <View style={styles.container}>
                <Text style={styles.label}>Cliente</Text>
                <TextInput
                    style={styles.input}
                    placeholder="Nombre del cliente"
                    value={cliente}
                    onChangeText={setCliente}
                />

                <Text style={styles.label}>Horas trabajadas</Text>
                <TextInput
                    style={styles.input}
                    placeholder="Ej. 10"
                    keyboardType="numeric"
                    value={horas}
                    onChangeText={setHoras}
                />

                <Text style={styles.label}>Tarifa por hora (MXN)</Text>
                <TextInput
                    style={styles.input}
                    keyboardType="numeric"
                    value={tarifa}
                    onChangeText={setTarifa}
                />

                <Text style={styles.label}>IVA (%)</Text>
                <TextInput
                    style={styles.input}
                    keyboardType="numeric"
                    value={iva}
                    onChangeText={setIva}
                />

                <Text style={styles.label}>Notas (opcional)</Text>
                <TextInput
                    style={[styles.input, styles.inputNotas]}
                    placeholder="Ej. Desarrollo de módulo de login"
                    value={notas}
                    onChangeText={setNotas}
                    multiline
                />

                <TouchableOpacity style={styles.boton} onPress={validarYCalcular}>
                    <Text style={styles.botonTexto}>Calcular Cotización</Text>
                </TouchableOpacity>
            </View>
        </TouchableWithoutFeedback>
    );
};

const styles = StyleSheet.create({
    container: {
        padding: 20,
    },
    label: {
        fontSize: 14,
        fontWeight: "600",
        marginBottom: 6,
        color: "#555",
    },
    input: {
        borderWidth: 1,
        borderColor: "#999",
        borderRadius: 8,
        padding: 12,
        fontSize: 16,
        backgroundColor: "white",
        marginBottom: 16,
    },
    inputNotas: {
        height: 70,
        textAlignVertical: "top",
    },
    boton: {
        backgroundColor: "#2c3e50",
        paddingVertical: 14,
        borderRadius: 8,
        alignItems: "center",
        marginTop: 10,
    },
    botonTexto: {
        color: "white",
        fontSize: 16,
        fontWeight: "600",
    },
});

export default FormularioCotizacion;
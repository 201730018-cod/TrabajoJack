import { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, Modal, StyleSheet, Keyboard, TouchableWithoutFeedback } from "react-native";

const IMC = () => {
    const [peso, setPeso] = useState("");
    const [altura, setAltura] = useState("");
    const [modalVisible, setModalVisible] = useState(false);
    const [resultado, setResultado] = useState({ imc: 0, categoria: "", color: "#000" });

    const calcular = () => {
        const pesoNum = parseFloat(peso);
        const alturaNum = parseFloat(altura);

        if (!pesoNum || !alturaNum) {
            alert("Ingresa peso y altura válidos");
            return;
        }

        const imcCalculado = pesoNum / (alturaNum * alturaNum);
        let categoria = "";
        let color = "";

        if (imcCalculado < 18.5) {
            categoria = "Bajo peso ⚠️";
            color = "#3498db";
        } else if (imcCalculado < 25) {
            categoria = "Peso normal ✅";
            color = "#27ae60";
        } else if (imcCalculado < 30) {
            categoria = "Sobrepeso ⚠️";
            color = "#e67e22";
        } else {
            categoria = "Obesidad ❌";
            color = "#e74c3c";
        }

        setResultado({ imc: imcCalculado.toFixed(2), categoria, color });
        setModalVisible(true);
    };

    return (
        <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
        <View style={styles.container}>
            <TextInput
                style={styles.input}
                placeholder="Peso en kg (ej. 70)"
                keyboardType="numeric"
                value={peso}
                onChangeText={setPeso}
            />
            <TextInput
                style={styles.input}
                placeholder="Altura en metros (ej. 1.75)"
                keyboardType="numeric"
                value={altura}
                onChangeText={setAltura}
            />
            <TouchableOpacity style={styles.boton} onPress={calcular}>
                <Text style={styles.botonTexto}>Calcular IMC</Text>
            </TouchableOpacity>

            <Modal
                animationType="fade"
                transparent={true}
                visible={modalVisible}
                onRequestClose={() => setModalVisible(false)}
            >
                <View style={styles.centeredView}>
                    <View style={styles.modalView}>
                        <Text style={styles.imcLabel}>Tu IMC es:</Text>
                        <Text style={styles.imcNumero}>{resultado.imc}</Text>
                        <Text style={[styles.categoria, { color: resultado.color }]}>
                            {resultado.categoria}
                        </Text>
                        <TouchableOpacity
                            style={styles.botonCerrar}
                            onPress={() => setModalVisible(false)}
                        >
                            <Text style={styles.botonTexto}>Cerrar</Text>
                        </TouchableOpacity>
                    </View>
                </View>
            </Modal>
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
        marginBottom: 15,
    },
    boton: {
        backgroundColor: "#4A90E2",
        paddingVertical: 14,
        borderRadius: 8,
        alignItems: "center",
    },
    botonTexto: {
        color: "white",
        fontSize: 16,
        fontWeight: "600",
    },
    centeredView: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "rgba(0,0,0,0.5)",
    },
    modalView: {
        margin: 20,
        backgroundColor: "white",
        borderRadius: 15,
        padding: 25,
        alignItems: "center",
        width: "80%",
    },
    imcLabel: {
        fontSize: 16,
        color: "#555",
    },
    imcNumero: {
        fontSize: 40,
        fontWeight: "bold",
        marginVertical: 10,
    },
    categoria: {
        fontSize: 18,
        fontWeight: "600",
        marginBottom: 20,
        textAlign: "center",
    },
    botonCerrar: {
        backgroundColor: "#4A90E2",
        paddingVertical: 10,
        paddingHorizontal: 30,
        borderRadius: 8,
    },
});

export default IMC;
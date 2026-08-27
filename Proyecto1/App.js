import React, { useState } from "react";
import { View, StyleSheet, SafeAreaView, Text } from "react-native";
import IMCForm from "./componentes/IMCForm";
import ResultadoModal from "./componentes/ResultadoModal";

export default function App() {
    const [modalVisible, setModalVisible] = useState(false);
    const [imc, setImc] = useState(0);
    const [categoria, setCategoria] = useState("");
    const [color, setColor] = useState("#000");

    const calcularIMC = (peso, altura) => {
        const resultado = peso / (altura * altura);
        const resultadoRedondeado = resultado.toFixed(2);

        let cat = "";
        let col = "";

        if (resultado < 18.5) {
            cat = "Estás por debajo de tu peso ⚠️";
            col = "#3498db";
        } else if (resultado < 25) {
            cat = "¡Estás en tu peso ideal! ✅";
            col = "#27ae60";
        } else if (resultado < 30) {
            cat = "Tienes sobrepeso ⚠️";
            col = "#e67e22";
        } else {
            cat = "Estás en un nivel de obesidad ❌";
            col = "#e74c3c";
        }

        setImc(resultadoRedondeado);
        setCategoria(cat);
        setColor(col);
        setModalVisible(true);
    };

    return (
        <SafeAreaView style={styles.container}>
            <View style={styles.content}>
                <Text style={styles.titulo}>Calculadora de IMC</Text>
                <IMCForm onCalcular={calcularIMC} />
                <ResultadoModal
                    visible={modalVisible}
                    onClose={() => setModalVisible(false)}
                    imc={imc}
                    categoria={categoria}
                    color={color}
                />
            </View>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: "#f2f2f2",
    },
    content: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
    },
    titulo: {
        fontSize: 24,
        fontWeight: "bold",
        marginBottom: 30,
    },
});
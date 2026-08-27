import { useState } from "react";
import { View, TextInput, Button, StyleSheet } from "react-native";

const IMCForm = ({ onCalcular }) => {
    const [peso, setPeso] = useState("");
    const [altura, setAltura] = useState("");

    const handleCalcular = () => {
        const pesoNum = parseFloat(peso);
        const alturaNum = parseFloat(altura);

        if (!pesoNum || !alturaNum) {
            alert("Ingresa peso y altura válidos");
            return;
        }

        onCalcular(pesoNum, alturaNum);
    };

    return (
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
            <Button title="Calcular IMC" onPress={handleCalcular} />
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        width: "100%",
        paddingHorizontal: 20,
    },
    input: {
        borderWidth: 1,
        borderColor: "#999",
        borderRadius: 8,
        padding: 10,
        marginBottom: 15,
        fontSize: 16,
        backgroundColor: "white",
    },
});

export default IMCForm;
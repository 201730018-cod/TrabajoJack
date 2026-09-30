import { SafeAreaView, Text, StyleSheet } from "react-native";
import IMC from "../componentes/IMC";

const PantallaIMC = () => {
    return (
        <SafeAreaView style={styles.container}>
            <Text style={styles.titulo}>⚖️ Calculadora IMC</Text>
            <IMC />
        </SafeAreaView>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: "#f2f2f2",
    },
    titulo: {
        fontSize: 22,
        fontWeight: "bold",
        textAlign: "center",
        marginTop: 20,
    },
});

export default PantallaIMC;
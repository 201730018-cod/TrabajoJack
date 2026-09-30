import { SafeAreaView, Text, StyleSheet } from "react-native";
import Divisas from "../componentes/Divisas";

const PantallaDivisas = () => {
    return (
        <SafeAreaView style={styles.container}>
            <Text style={styles.titulo}>💱 Conversión de Divisas</Text>
            <Divisas />
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

export default PantallaDivisas;
import { SafeAreaView, Text, StyleSheet } from "react-native";
import Propinas from "../componentes/Propinas";

const PantallaPropinas = () => {
    return (
        <SafeAreaView style={styles.container}>
            <Text style={styles.titulo}>💵 Calcular Propina</Text>
            <Propinas />
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

export default PantallaPropinas;
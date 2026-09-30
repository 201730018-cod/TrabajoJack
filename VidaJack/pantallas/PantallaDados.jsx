import { SafeAreaView, Text, StyleSheet } from "react-native";
import Dados from "../componentes/Dados";

const PantallaDados = () => {
    return (
        <SafeAreaView style={styles.container}>
            <Text style={styles.titulo}>🎲 Lanzar Dados</Text>
            <Dados />
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

export default PantallaDados;
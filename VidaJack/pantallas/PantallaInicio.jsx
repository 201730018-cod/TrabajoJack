import { SafeAreaView, Text, StyleSheet } from "react-native";

const PantallaInicio = () => {
    return (
        <SafeAreaView style={styles.container}>
            <Text style={styles.titulo}>Bienvenido a VidaJack</Text>
            <Text style={styles.subtitulo}>Elige una herramienta del menú</Text>
        </SafeAreaView>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "#f2f2f2",
    },
    titulo: {
        fontSize: 26,
        fontWeight: "bold",
        marginBottom: 10,
    },
    subtitulo: {
        fontSize: 16,
        color: "#666",
    },
});

export default PantallaInicio;
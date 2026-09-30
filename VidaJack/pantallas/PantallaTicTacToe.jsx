import { SafeAreaView, Text, StyleSheet } from "react-native";
import TicTacToe from "../componentes/TicTacToe";

const PantallaTicTacToe = () => {
    return (
        <SafeAreaView style={styles.container}>
            <Text style={styles.titulo}>⭕ Tic Tac Toe</Text>
            <TicTacToe />
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

export default PantallaTicTacToe;
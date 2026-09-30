import { useState } from "react";
import { View, Text, TouchableOpacity, StyleSheet } from "react-native";

const LINEAS_GANADORAS = [
    [0, 1, 2], [3, 4, 5], [6, 7, 8], // filas
    [0, 3, 6], [1, 4, 7], [2, 5, 8], // columnas
    [0, 4, 8], [2, 4, 6],            // diagonales
];

const calcularGanador = (tablero) => {
    for (const [a, b, c] of LINEAS_GANADORAS) {
        if (tablero[a] && tablero[a] === tablero[b] && tablero[a] === tablero[c]) {
            return tablero[a];
        }
    }
    return null;
};

const TicTacToe = () => {
    const [tablero, setTablero] = useState(Array(9).fill(null));
    const [turno, setTurno] = useState("X");

    const ganador = calcularGanador(tablero);
    const empate = !ganador && tablero.every((casilla) => casilla !== null);

    const jugar = (index) => {
        if (tablero[index] || ganador) {
            return; // casilla ocupada o el juego ya terminó
        }

        const nuevoTablero = [...tablero];
        nuevoTablero[index] = turno;
        setTablero(nuevoTablero);
        setTurno(turno === "X" ? "O" : "X");
    };

    const reiniciar = () => {
        setTablero(Array(9).fill(null));
        setTurno("X");
    };

    let mensaje = `Turno de: ${turno}`;
    if (ganador) mensaje = `¡Ganó ${ganador}! 🎉`;
    if (empate) mensaje = "¡Empate! 🤝";

    return (
        <View style={styles.container}>
            <Text style={styles.mensaje}>{mensaje}</Text>

            <View style={styles.tablero}>
                {tablero.map((valor, index) => (
                    <TouchableOpacity
                        key={index}
                        style={styles.casilla}
                        onPress={() => jugar(index)}
                    >
                        <Text
                            style={[
                                styles.simbolo,
                                { color: valor === "X" ? "#4A90E2" : "#e74c3c" },
                            ]}
                        >
                            {valor}
                        </Text>
                    </TouchableOpacity>
                ))}
            </View>

            <TouchableOpacity style={styles.botonReiniciar} onPress={reiniciar}>
                <Text style={styles.botonTexto}>Reiniciar</Text>
            </TouchableOpacity>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
        gap: 20,
    },
    mensaje: {
        fontSize: 22,
        fontWeight: "bold",
    },
    tablero: {
        width: 300,
        flexDirection: "row",
        flexWrap: "wrap",
    },
    casilla: {
        width: 100,
        height: 100,
        justifyContent: "center",
        alignItems: "center",
        borderWidth: 1,
        borderColor: "#333",
    },
    simbolo: {
        fontSize: 48,
        fontWeight: "bold",
    },
    botonReiniciar: {
        backgroundColor: "#4A90E2",
        paddingVertical: 12,
        paddingHorizontal: 30,
        borderRadius: 8,
    },
    botonTexto: {
        color: "white",
        fontSize: 16,
        fontWeight: "600",
    },
});

export default TicTacToe;
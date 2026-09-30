import { useState, useEffect } from "react";
import { View, Text, TouchableOpacity, StyleSheet } from "react-native";

const EMOJIS = ["🍎", "🍌", "🍇", "🍓", "🍉", "🍒"];

// Genera 12 cartas (6 pares) mezcladas
const generarCartas = () => {
    const pares = [...EMOJIS, ...EMOJIS];
    const mezcladas = pares
        .map((valor) => ({ id: Math.random(), valor }))
        .sort(() => Math.random() - 0.5);
    return mezcladas.map((carta, index) => ({ ...carta, posicion: index }));
};

const Memorama = () => {
    const [cartas, setCartas] = useState(generarCartas());
    const [volteadas, setVolteadas] = useState([]); // posiciones volteadas temporalmente (máx 2)
    const [encontradas, setEncontradas] = useState([]); // posiciones ya emparejadas
    const [movimientos, setMovimientos] = useState(0);
    const [bloqueado, setBloqueado] = useState(false); // evita clics mientras se comparan 2 cartas

    const juegoCompleto = encontradas.length === cartas.length;

    const voltearCarta = (posicion) => {
        // Ignora si: ya está bloqueado, ya hay 2 volteadas, la carta ya está encontrada, o ya está volteada
        if (bloqueado || volteadas.includes(posicion) || encontradas.includes(posicion)) {
            return;
        }

        const nuevasVolteadas = [...volteadas, posicion];
        setVolteadas(nuevasVolteadas);

        if (nuevasVolteadas.length === 2) {
            setBloqueado(true);
            setMovimientos((m) => m + 1);

            const [primera, segunda] = nuevasVolteadas;
            const cartaA = cartas.find((c) => c.posicion === primera);
            const cartaB = cartas.find((c) => c.posicion === segunda);

            if (cartaA.valor === cartaB.valor) {
                // Son par: las marca como encontradas
                setEncontradas((prev) => [...prev, primera, segunda]);
                setVolteadas([]);
                setBloqueado(false);
            } else {
                // No son par: espera un momento y las voltea de regreso
                setTimeout(() => {
                    setVolteadas([]);
                    setBloqueado(false);
                }, 800);
            }
        }
    };

    const reiniciar = () => {
        setCartas(generarCartas());
        setVolteadas([]);
        setEncontradas([]);
        setMovimientos(0);
        setBloqueado(false);
    };

    return (
        <View style={styles.container}>
            <Text style={styles.contador}>Movimientos: {movimientos}</Text>

            {juegoCompleto && (
                <Text style={styles.mensajeGanaste}>¡Completado! 🎉</Text>
            )}

            <View style={styles.tablero}>
                {cartas.map((carta) => {
                    const estaVolteada = volteadas.includes(carta.posicion);
                    const estaEncontrada = encontradas.includes(carta.posicion);
                    const mostrarValor = estaVolteada || estaEncontrada;

                    return (
                        <TouchableOpacity
                            key={carta.id}
                            style={[
                                styles.carta,
                                estaEncontrada && styles.cartaEncontrada,
                            ]}
                            onPress={() => voltearCarta(carta.posicion)}
                        >
                            <Text style={styles.emoji}>
                                {mostrarValor ? carta.valor : "❓"}
                            </Text>
                        </TouchableOpacity>
                    );
                })}
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
        gap: 15,
        padding: 20,
    },
    contador: {
        fontSize: 18,
        fontWeight: "600",
    },
    mensajeGanaste: {
        fontSize: 20,
        fontWeight: "bold",
        color: "#27ae60",
    },
    tablero: {
        width: 300,
        flexDirection: "row",
        flexWrap: "wrap",
        justifyContent: "center",
        gap: 8,
    },
    carta: {
        width: 70,
        height: 70,
        backgroundColor: "white",
        borderRadius: 10,
        justifyContent: "center",
        alignItems: "center",
        borderWidth: 1,
        borderColor: "#ccc",
    },
    cartaEncontrada: {
        backgroundColor: "#d5f5e3",
        borderColor: "#27ae60",
    },
    emoji: {
        fontSize: 32,
    },
    botonReiniciar: {
        backgroundColor: "#4A90E2",
        paddingVertical: 12,
        paddingHorizontal: 30,
        borderRadius: 8,
        marginTop: 10,
    },
    botonTexto: {
        color: "white",
        fontSize: 16,
        fontWeight: "600",
    },
});

export default Memorama;
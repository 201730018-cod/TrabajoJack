import { useState } from "react";
import {
    View,
    Text,
    TextInput,
    TouchableOpacity,
    FlatList,
    StyleSheet,
    SafeAreaView,
    Keyboard,
    TouchableWithoutFeedback,
} from "react-native";

const ListaSuper = () => {
    const [articulo, setArticulo] = useState("");
    const [lista, setLista] = useState([]);

    const agregarArticulo = () => {
        if (articulo.trim() === "") {
            return;
        }

        const nuevoItem = {
            id: Date.now().toString(),
            nombre: articulo.trim(),
        };

        setLista([...lista, nuevoItem]);
        setArticulo("");
        Keyboard.dismiss(); // cierra el teclado al agregar
    };

    const eliminarArticulo = (id) => {
        setLista(lista.filter((item) => item.id !== id));
    };

    return (
        <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
            <SafeAreaView style={styles.safeArea}>
                <View style={styles.container}>
                    <View style={styles.inputRow}>
                        <TextInput
                            style={styles.input}
                            placeholder="Ej. Leche, huevos, pan..."
                            value={articulo}
                            onChangeText={setArticulo}
                            onSubmitEditing={agregarArticulo}
                        />
                        <TouchableOpacity style={styles.botonAgregar} onPress={agregarArticulo}>
                            <Text style={styles.botonAgregarTexto}>+</Text>
                        </TouchableOpacity>
                    </View>

                    {lista.length === 0 ? (
                        <Text style={styles.vacio}>Tu lista está vacía</Text>
                    ) : (
                        <FlatList
                            data={lista}
                            keyExtractor={(item) => item.id}
                            keyboardShouldPersistTaps="handled"
                            renderItem={({ item }) => (
                                <View style={styles.fila}>
                                    <Text style={styles.filaTexto}>{item.nombre}</Text>
                                    <TouchableOpacity onPress={() => eliminarArticulo(item.id)}>
                                        <Text style={styles.botonEliminar}>Eliminar</Text>
                                    </TouchableOpacity>
                                </View>
                            )}
                        />
                    )}

                    {lista.length > 0 && (
                        <Text style={styles.contador}>
                            {lista.length} {lista.length === 1 ? "artículo" : "artículos"}
                        </Text>
                    )}
                </View>
            </SafeAreaView>
        </TouchableWithoutFeedback>
    );
};

const styles = StyleSheet.create({
    safeArea: {
        flex: 1,
        backgroundColor: "#f2f2f2",
    },
    container: {
        flex: 1,
        padding: 20,
    },
    inputRow: {
        flexDirection: "row",
        gap: 10,
        marginBottom: 20,
    },
    input: {
        flex: 1,
        borderWidth: 1,
        borderColor: "#999",
        borderRadius: 8,
        padding: 12,
        fontSize: 16,
        backgroundColor: "white",
    },
    botonAgregar: {
        backgroundColor: "#4A90E2",
        width: 48,
        height: 48,
        borderRadius: 8,
        justifyContent: "center",
        alignItems: "center",
    },
    botonAgregarTexto: {
        color: "white",
        fontSize: 24,
        fontWeight: "bold",
    },
    vacio: {
        textAlign: "center",
        color: "#999",
        fontSize: 16,
        marginTop: 40,
    },
    fila: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        backgroundColor: "white",
        padding: 15,
        borderRadius: 8,
        marginBottom: 10,
    },
    filaTexto: {
        fontSize: 16,
    },
    botonEliminar: {
        color: "#e74c3c",
        fontWeight: "600",
    },
    contador: {
        textAlign: "center",
        marginTop: 10,
        color: "#666",
    },
});

export default ListaSuper;
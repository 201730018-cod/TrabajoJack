import { useState } from "react";
import { SafeAreaView, View, Text, FlatList, TouchableOpacity, StyleSheet } from "react-native";
import DetalleModal from "../modales/DetalleModal";
import ConfirmarEliminarModal from "../modales/ConfirmarEliminarModal";

const PantallaHistorial = ({ cotizaciones, eliminarCotizacion }) => {
    const [seleccionada, setSeleccionada] = useState(null);
    const [detalleVisible, setDetalleVisible] = useState(false);
    const [confirmarVisible, setConfirmarVisible] = useState(false);

    const abrirDetalle = (cotizacion) => {
        setSeleccionada(cotizacion);
        setDetalleVisible(true);
    };

    const pedirConfirmacion = () => {
        setDetalleVisible(false);
        setConfirmarVisible(true);
    };

    const confirmarEliminacion = () => {
        eliminarCotizacion(seleccionada.id);
        setConfirmarVisible(false);
        setSeleccionada(null);
    };

    return (
        <SafeAreaView style={styles.container}>
            <Text style={styles.titulo}>📋 Historial</Text>

            {cotizaciones.length === 0 ? (
                <Text style={styles.vacio}>Aún no hay cotizaciones guardadas</Text>
            ) : (
                <FlatList
                    data={cotizaciones}
                    keyExtractor={(item) => item.id}
                    contentContainerStyle={styles.lista}
                    renderItem={({ item }) => (
                        <TouchableOpacity style={styles.card} onPress={() => abrirDetalle(item)}>
                            <View>
                                <Text style={styles.cliente}>{item.cliente}</Text>
                                <Text style={styles.fecha}>{item.fecha}</Text>
                            </View>
                            <Text style={styles.total}>${item.total.toFixed(2)}</Text>
                        </TouchableOpacity>
                    )}
                />
            )}

            <DetalleModal
                visible={detalleVisible}
                cotizacion={seleccionada}
                onCerrar={() => setDetalleVisible(false)}
                onEliminar={pedirConfirmacion}
            />

            <ConfirmarEliminarModal
                visible={confirmarVisible}
                onConfirmar={confirmarEliminacion}
                onCancelar={() => setConfirmarVisible(false)}
            />
        </SafeAreaView>
    );
};

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: "#f2f2f2" },
    titulo: {
        fontSize: 22,
        fontWeight: "bold",
        textAlign: "center",
        marginVertical: 20,
    },
    vacio: { textAlign: "center", color: "#999", marginTop: 40 },
    lista: { paddingHorizontal: 20 },
    card: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        backgroundColor: "white",
        padding: 16,
        borderRadius: 10,
        marginBottom: 10,
    },
    cliente: { fontSize: 16, fontWeight: "600" },
    fecha: { color: "#999", fontSize: 12, marginTop: 2 },
    total: { fontSize: 16, fontWeight: "bold", color: "#27ae60" },
});

export default PantallaHistorial;
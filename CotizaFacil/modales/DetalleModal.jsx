import { View, Text, TouchableOpacity, Modal, StyleSheet } from "react-native";

const DetalleModal = ({ visible, cotizacion, onCerrar, onEliminar }) => {
    if (!cotizacion) return null;

    return (
        <Modal
            animationType="slide"
            transparent={true}
            visible={visible}
            onRequestClose={onCerrar}
        >
            <View style={styles.centeredView}>
                <View style={styles.modalView}>
                    <Text style={styles.titulo}>{cotizacion.cliente}</Text>
                    <Text style={styles.fecha}>{cotizacion.fecha}</Text>

                    <View style={styles.fila}>
                        <Text style={styles.label}>Horas</Text>
                        <Text style={styles.valor}>{cotizacion.horas}</Text>
                    </View>
                    <View style={styles.fila}>
                        <Text style={styles.label}>Tarifa por hora</Text>
                        <Text style={styles.valor}>${cotizacion.tarifa.toFixed(2)}</Text>
                    </View>
                    <View style={styles.fila}>
                        <Text style={styles.label}>Subtotal</Text>
                        <Text style={styles.valor}>${cotizacion.subtotal.toFixed(2)}</Text>
                    </View>
                    <View style={styles.fila}>
                        <Text style={styles.label}>IVA ({cotizacion.iva}%)</Text>
                        <Text style={styles.valor}>${cotizacion.montoIva.toFixed(2)}</Text>
                    </View>
                    <View style={[styles.fila, styles.filaTotal]}>
                        <Text style={styles.labelTotal}>Total</Text>
                        <Text style={styles.valorTotal}>${cotizacion.total.toFixed(2)}</Text>
                    </View>

                    {cotizacion.notas !== "" && (
                        <View style={styles.notasContainer}>
                            <Text style={styles.label}>Notas</Text>
                            <Text style={styles.notas}>{cotizacion.notas}</Text>
                        </View>
                    )}

                    <View style={styles.botones}>
                        <TouchableOpacity style={styles.botonEliminar} onPress={onEliminar}>
                            <Text style={styles.botonEliminarTexto}>Eliminar</Text>
                        </TouchableOpacity>
                        <TouchableOpacity style={styles.botonCerrar} onPress={onCerrar}>
                            <Text style={styles.botonCerrarTexto}>Cerrar</Text>
                        </TouchableOpacity>
                    </View>
                </View>
            </View>
        </Modal>
    );
};

const styles = StyleSheet.create({
    centeredView: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "rgba(0,0,0,0.5)",
    },
    modalView: {
        width: "85%",
        backgroundColor: "white",
        borderRadius: 15,
        padding: 25,
    },
    titulo: { fontSize: 20, fontWeight: "bold", color: "#2c3e50" },
    fecha: { color: "#999", marginBottom: 15 },
    fila: {
        flexDirection: "row",
        justifyContent: "space-between",
        paddingVertical: 6,
    },
    label: { color: "#666" },
    valor: { fontWeight: "600" },
    filaTotal: {
        borderTopWidth: 1,
        borderTopColor: "#eee",
        marginTop: 8,
        paddingTop: 12,
    },
    labelTotal: { fontSize: 18, fontWeight: "bold" },
    valorTotal: { fontSize: 18, fontWeight: "bold", color: "#27ae60" },
    notasContainer: { marginTop: 15 },
    notas: { marginTop: 4, color: "#333" },
    botones: {
        flexDirection: "row",
        gap: 10,
        marginTop: 20,
    },
    botonEliminar: {
        flex: 1,
        paddingVertical: 12,
        borderRadius: 8,
        borderWidth: 1,
        borderColor: "#e74c3c",
        alignItems: "center",
    },
    botonEliminarTexto: { color: "#e74c3c", fontWeight: "600" },
    botonCerrar: {
        flex: 1,
        paddingVertical: 12,
        borderRadius: 8,
        backgroundColor: "#2c3e50",
        alignItems: "center",
    },
    botonCerrarTexto: { color: "white", fontWeight: "600" },
});

export default DetalleModal;
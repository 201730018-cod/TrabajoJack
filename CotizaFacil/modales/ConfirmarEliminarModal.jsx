import { View, Text, TouchableOpacity, Modal, StyleSheet } from "react-native";

const ConfirmarEliminarModal = ({ visible, onConfirmar, onCancelar }) => {
    return (
        <Modal
            animationType="fade"
            transparent={true}
            visible={visible}
            onRequestClose={onCancelar}
        >
            <View style={styles.centeredView}>
                <View style={styles.modalView}>
                    <Text style={styles.titulo}>¿Eliminar cotización?</Text>
                    <Text style={styles.mensaje}>Esta acción no se puede deshacer.</Text>

                    <View style={styles.botones}>
                        <TouchableOpacity style={styles.botonCancelar} onPress={onCancelar}>
                            <Text style={styles.botonCancelarTexto}>Cancelar</Text>
                        </TouchableOpacity>
                        <TouchableOpacity style={styles.botonConfirmar} onPress={onConfirmar}>
                            <Text style={styles.botonConfirmarTexto}>Eliminar</Text>
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
        width: "80%",
        backgroundColor: "white",
        borderRadius: 15,
        padding: 25,
        alignItems: "center",
    },
    titulo: { fontSize: 18, fontWeight: "bold", marginBottom: 8 },
    mensaje: { color: "#666", textAlign: "center", marginBottom: 20 },
    botones: { flexDirection: "row", gap: 10, width: "100%" },
    botonCancelar: {
        flex: 1,
        paddingVertical: 12,
        borderRadius: 8,
        borderWidth: 1,
        borderColor: "#999",
        alignItems: "center",
    },
    botonCancelarTexto: { color: "#666", fontWeight: "600" },
    botonConfirmar: {
        flex: 1,
        paddingVertical: 12,
        borderRadius: 8,
        backgroundColor: "#e74c3c",
        alignItems: "center",
    },
    botonConfirmarTexto: { color: "white", fontWeight: "600" },
});

export default ConfirmarEliminarModal;
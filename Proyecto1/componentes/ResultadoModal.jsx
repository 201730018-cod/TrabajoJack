import { StyleSheet, Text, View, Button, Modal } from "react-native";

const ResultadoModal = ({ visible, onClose, imc, categoria, color }) => {
    return (
        <Modal
            animationType="fade"
            transparent={true}
            visible={visible}
            onRequestClose={onClose}
        >
            <View style={styles.centeredView}>
                <View style={styles.modalView}>
                    <Text style={styles.imcTexto}>Tu IMC es:</Text>
                    <Text style={styles.imcNumero}>{imc}</Text>
                    <Text style={[styles.categoria, { color }]}>{categoria}</Text>
                    <Button title="Cerrar" onPress={onClose} />
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
        margin: 20,
        backgroundColor: "white",
        borderRadius: 15,
        padding: 25,
        alignItems: "center",
        shadowColor: "#000",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.3,
        shadowRadius: 4,
        elevation: 5,
    },
    imcTexto: {
        fontSize: 16,
        color: "#555",
    },
    imcNumero: {
        fontSize: 36,
        fontWeight: "bold",
        marginVertical: 10,
    },
    categoria: {
        fontSize: 18,
        fontWeight: "600",
        marginBottom: 20,
        textAlign: "center",
    },
});

export default ResultadoModal;
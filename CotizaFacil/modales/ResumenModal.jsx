import { useEffect, useRef } from "react";
import { View, Text, TouchableOpacity, Modal, StyleSheet, Animated } from "react-native";

const ResumenModal = ({ visible, cotizacion, onGuardar, onCerrar }) => {
    const opacidad = useRef(new Animated.Value(0)).current;

    useEffect(() => {
        if (visible) {
            opacidad.setValue(0);
            Animated.timing(opacidad, {
                toValue: 1,
                duration: 400,
                useNativeDriver: true,
            }).start();
        }
    }, [visible]);

    if (!cotizacion) return null;

    return (
        <Modal
            animationType="none"
            transparent={true}
            visible={visible}
            onRequestClose={onCerrar}
        >
            <View style={styles.centeredView}>
                <Animated.View style={[styles.modalView, { opacity: opacidad }]}>
                    <Text style={styles.titulo}>Resumen de Cotización</Text>

                    <View style={styles.fila}>
                        <Text style={styles.label}>Cliente</Text>
                        <Text style={styles.valor}>{cotizacion.cliente}</Text>
                    </View>
                    <View style={styles.fila}>
                        <Text style={styles.label}>Horas</Text>
                        <Text style={styles.valor}>{cotizacion.horas}</Text>
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

                    <View style={styles.botones}>
                        <TouchableOpacity style={styles.botonSecundario} onPress={onCerrar}>
                            <Text style={styles.botonSecundarioTexto}>Descartar</Text>
                        </TouchableOpacity>
                        <TouchableOpacity style={styles.botonPrimario} onPress={onGuardar}>
                            <Text style={styles.botonPrimarioTexto}>Guardar</Text>
                        </TouchableOpacity>
                    </View>
                </Animated.View>
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
    titulo: {
        fontSize: 20,
        fontWeight: "bold",
        marginBottom: 15,
        textAlign: "center",
        color: "#2c3e50",
    },
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
    botones: {
        flexDirection: "row",
        gap: 10,
        marginTop: 20,
    },
    botonSecundario: {
        flex: 1,
        paddingVertical: 12,
        borderRadius: 8,
        borderWidth: 1,
        borderColor: "#999",
        alignItems: "center",
    },
    botonSecundarioTexto: { color: "#666", fontWeight: "600" },
    botonPrimario: {
        flex: 1,
        paddingVertical: 12,
        borderRadius: 8,
        backgroundColor: "#2c3e50",
        alignItems: "center",
    },
    botonPrimarioTexto: { color: "white", fontWeight: "600" },
});

export default ResumenModal;
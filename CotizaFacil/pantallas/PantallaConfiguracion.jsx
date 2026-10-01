import { useState } from "react";
import { SafeAreaView, View, Text, TouchableOpacity, StyleSheet } from "react-native";
import EditarTarifaModal from "../modales/EditarTarifaModal";

const PantallaConfiguracion = ({ config, setConfig }) => {
    const [modalVisible, setModalVisible] = useState(false);

    const handleGuardar = (nuevaConfig) => {
        setConfig(nuevaConfig);
        setModalVisible(false);
    };

    return (
        <SafeAreaView style={styles.container}>
            <Text style={styles.titulo}>⚙️ Configuración</Text>

            <View style={styles.card}>
                <View style={styles.fila}>
                    <Text style={styles.label}>Negocio</Text>
                    <Text style={styles.valor}>{config.nombreNegocio}</Text>
                </View>
                <View style={styles.fila}>
                    <Text style={styles.label}>Tarifa por hora</Text>
                    <Text style={styles.valor}>${config.tarifaDefault} MXN</Text>
                </View>
                <View style={styles.fila}>
                    <Text style={styles.label}>IVA</Text>
                    <Text style={styles.valor}>{config.ivaDefault}%</Text>
                </View>
            </View>

            <TouchableOpacity style={styles.boton} onPress={() => setModalVisible(true)}>
                <Text style={styles.botonTexto}>Editar Configuración</Text>
            </TouchableOpacity>

            <EditarTarifaModal
                visible={modalVisible}
                config={config}
                onGuardar={handleGuardar}
                onCerrar={() => setModalVisible(false)}
            />
        </SafeAreaView>
    );
};

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: "#f2f2f2", padding: 20 },
    titulo: { fontSize: 22, fontWeight: "bold", textAlign: "center", marginVertical: 20 },
    card: {
        backgroundColor: "white",
        borderRadius: 10,
        padding: 16,
        marginBottom: 20,
    },
    fila: {
        flexDirection: "row",
        justifyContent: "space-between",
        paddingVertical: 8,
        borderBottomWidth: 1,
        borderBottomColor: "#eee",
    },
    label: { color: "#666" },
    valor: { fontWeight: "600" },
    boton: {
        backgroundColor: "#2c3e50",
        paddingVertical: 14,
        borderRadius: 8,
        alignItems: "center",
    },
    botonTexto: { color: "white", fontSize: 16, fontWeight: "600" },
});

export default PantallaConfiguracion;
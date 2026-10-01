import { useState, useEffect } from "react";
import { View, Text, TextInput, TouchableOpacity, Modal, StyleSheet } from "react-native";

const EditarTarifaModal = ({ visible, config, onGuardar, onCerrar }) => {
    const [tarifa, setTarifa] = useState(config.tarifaDefault);
    const [iva, setIva] = useState(config.ivaDefault);
    const [nombreNegocio, setNombreNegocio] = useState(config.nombreNegocio);

    // Sincroniza los campos cada vez que se abre el modal, por si config cambió
    useEffect(() => {
        if (visible) {
            setTarifa(config.tarifaDefault);
            setIva(config.ivaDefault);
            setNombreNegocio(config.nombreNegocio);
        }
    }, [visible]);

    const guardar = () => {
        const tarifaNum = parseFloat(tarifa);
        const ivaNum = parseFloat(iva);

        if (nombreNegocio.trim() === "") {
            alert("El nombre del negocio no puede estar vacío");
            return;
        }

        if (!tarifaNum || tarifaNum <= 0) {
            alert("Ingresa una tarifa válida");
            return;
        }

        if (isNaN(ivaNum) || ivaNum < 0 || ivaNum > 100) {
            alert("El IVA debe estar entre 0 y 100");
            return;
        }

        onGuardar({
            nombreNegocio: nombreNegocio.trim(),
            tarifaDefault: tarifa,
            ivaDefault: iva,
        });
    };

    return (
        <Modal
            animationType="slide"
            transparent={true}
            visible={visible}
            onRequestClose={onCerrar}
        >
            <View style={styles.centeredView}>
                <View style={styles.modalView}>
                    <Text style={styles.titulo}>Editar Configuración</Text>

                    <Text style={styles.label}>Nombre del negocio</Text>
                    <TextInput
                        style={styles.input}
                        value={nombreNegocio}
                        onChangeText={setNombreNegocio}
                    />

                    <Text style={styles.label}>Tarifa por hora por defecto (MXN)</Text>
                    <TextInput
                        style={styles.input}
                        keyboardType="numeric"
                        value={tarifa}
                        onChangeText={setTarifa}
                    />

                    <Text style={styles.label}>IVA por defecto (%)</Text>
                    <TextInput
                        style={styles.input}
                        keyboardType="numeric"
                        value={iva}
                        onChangeText={setIva}
                    />

                    <View style={styles.botones}>
                        <TouchableOpacity style={styles.botonCancelar} onPress={onCerrar}>
                            <Text style={styles.botonCancelarTexto}>Cancelar</Text>
                        </TouchableOpacity>
                        <TouchableOpacity style={styles.botonGuardar} onPress={guardar}>
                            <Text style={styles.botonGuardarTexto}>Guardar</Text>
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
    titulo: { fontSize: 20, fontWeight: "bold", marginBottom: 15, textAlign: "center" },
    label: { fontSize: 14, fontWeight: "600", marginBottom: 6, color: "#555" },
    input: {
        borderWidth: 1,
        borderColor: "#999",
        borderRadius: 8,
        padding: 10,
        fontSize: 16,
        marginBottom: 15,
    },
    botones: { flexDirection: "row", gap: 10, marginTop: 10 },
    botonCancelar: {
        flex: 1,
        paddingVertical: 12,
        borderRadius: 8,
        borderWidth: 1,
        borderColor: "#999",
        alignItems: "center",
    },
    botonCancelarTexto: { color: "#666", fontWeight: "600" },
    botonGuardar: {
        flex: 1,
        paddingVertical: 12,
        borderRadius: 8,
        backgroundColor: "#2c3e50",
        alignItems: "center",
    },
    botonGuardarTexto: { color: "white", fontWeight: "600" },
});

export default EditarTarifaModal;
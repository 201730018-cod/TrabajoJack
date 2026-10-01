import { useState } from "react";
import { SafeAreaView, ScrollView, Text, StyleSheet } from "react-native";
import FormularioCotizacion from "../componentes/FormularioCotizacion";
import ResumenModal from "../modales/ResumenModal";

const PantallaNuevaCotizacion = ({ config, agregarCotizacion }) => {
    const [modalVisible, setModalVisible] = useState(false);
    const [cotizacionActual, setCotizacionActual] = useState(null);

    const handleCalcular = (datos) => {
        setCotizacionActual(datos);
        setModalVisible(true);
    };

    const handleGuardar = () => {
        agregarCotizacion(cotizacionActual);
        setModalVisible(false);
    };

    return (
        <SafeAreaView style={styles.container}>
            <ScrollView>
                <Text style={styles.titulo}>📝 Nueva Cotización</Text>
                <FormularioCotizacion config={config} onCalcular={handleCalcular} />
            </ScrollView>

            <ResumenModal
                visible={modalVisible}
                cotizacion={cotizacionActual}
                onGuardar={handleGuardar}
                onCerrar={() => setModalVisible(false)}
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
        marginTop: 20,
        marginBottom: 10,
    },
});

export default PantallaNuevaCotizacion;
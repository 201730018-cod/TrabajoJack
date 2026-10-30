import { Modal, View, Text, FlatList, TouchableOpacity, StyleSheet, Alert } from 'react-native';

export default function ListaPersonas({ visible, personas, onBorrar, onCerrar }) {
  const confirmarBorrado = (persona) => {
    Alert.alert('Borrar persona', `¿Quitar a "${persona.nombre}" de la lista?`, [
      { text: 'Cancelar', style: 'cancel' },
      { text: 'Borrar', style: 'destructive', onPress: () => onBorrar(persona.nombre) },
    ]);
  };

  return (
    <Modal visible={visible} animationType="slide" onRequestClose={onCerrar}>
      <View style={styles.contenedor}>
        <Text style={styles.titulo}>Personas registradas ({personas.length})</Text>

        <FlatList
          data={personas}
          keyExtractor={(p) => p.nombre}
          ListEmptyComponent={
            <Text style={styles.vacio}>
              Todavía no hay nadie. Ponte frente a la cámara y toca "Registrar".
            </Text>
          }
          renderItem={({ item }) => (
            <View style={styles.fila}>
              <Text style={styles.nombre}>👤 {item.nombre}</Text>
              <TouchableOpacity style={styles.botonBorrar} onPress={() => confirmarBorrado(item)}>
                <Text style={styles.textoBoton}>Borrar</Text>
              </TouchableOpacity>
            </View>
          )}
        />

        <TouchableOpacity style={styles.botonCerrar} onPress={onCerrar}>
          <Text style={styles.textoBoton}>Volver a la cámara</Text>
        </TouchableOpacity>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  contenedor: { flex: 1, backgroundColor: '#0f172a', padding: 20, paddingTop: 48 },
  titulo: { color: '#fff', fontSize: 22, fontWeight: 'bold', marginBottom: 16 },
  vacio: { color: '#94a3b8', textAlign: 'center', marginTop: 32, fontSize: 16 },
  fila: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#1e293b',
    padding: 14,
    borderRadius: 12,
    marginBottom: 10,
  },
  nombre: { color: '#fff', fontSize: 17 },
  botonBorrar: {
    backgroundColor: '#ef4444',
    paddingVertical: 6,
    paddingHorizontal: 14,
    borderRadius: 8,
  },
  botonCerrar: {
    backgroundColor: '#6366f1',
    padding: 14,
    borderRadius: 12,
    alignItems: 'center',
    marginTop: 12,
  },
  textoBoton: { color: '#fff', fontWeight: 'bold' },
});

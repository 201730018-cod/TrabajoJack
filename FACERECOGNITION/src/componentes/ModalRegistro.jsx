import { useState } from 'react';
import { Modal, View, Text, TextInput, TouchableOpacity, StyleSheet } from 'react-native';

export default function ModalRegistro({ visible, onGuardar, onCerrar }) {
  const [nombre, setNombre] = useState('');

  const cerrar = () => {
    setNombre('');
    onCerrar();
  };

  const guardar = () => {
    const limpio = nombre.trim();
    if (!limpio) return;
    setNombre('');
    onGuardar(limpio);
  };

  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={cerrar}>
      <View style={styles.fondo}>
        <View style={styles.tarjeta}>
          <Text style={styles.titulo}>¿Cómo se llama esta persona?</Text>
          <TextInput
            style={styles.input}
            placeholder="Nombre"
            placeholderTextColor="#94a3b8"
            value={nombre}
            onChangeText={setNombre}
            onSubmitEditing={guardar}
            autoFocus
          />
          <View style={styles.fila}>
            <TouchableOpacity style={[styles.boton, styles.gris]} onPress={cerrar}>
              <Text style={styles.textoBoton}>Cancelar</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.boton, !nombre.trim() && styles.deshabilitado]}
              onPress={guardar}
              disabled={!nombre.trim()}
            >
              <Text style={styles.textoBoton}>Registrar</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  fondo: { flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(0,0,0,0.5)' },
  tarjeta: {
    backgroundColor: '#1e293b',
    padding: 20,
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
  },
  titulo: { color: '#fff', fontSize: 18, fontWeight: 'bold', marginBottom: 12 },
  input: {
    backgroundColor: '#0f172a',
    color: '#fff',
    borderRadius: 10,
    padding: 12,
    fontSize: 16,
    marginBottom: 16,
  },
  fila: { flexDirection: 'row', gap: 10 },
  boton: {
    flex: 1,
    backgroundColor: '#6366f1',
    padding: 12,
    borderRadius: 10,
    alignItems: 'center',
  },
  gris: { backgroundColor: '#475569' },
  deshabilitado: { opacity: 0.5 },
  textoBoton: { color: '#fff', fontWeight: 'bold', fontSize: 16 },
});

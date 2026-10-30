import { View, Text, Image, TouchableOpacity, StyleSheet, ActivityIndicator } from 'react-native';

export default function VistaFoto({ uri, guardando, onGuardar, onDescartar }) {
  return (
    <View style={styles.contenedor}>
      <Image source={{ uri }} style={styles.foto} resizeMode="contain" />

      <View style={styles.barra}>
        <TouchableOpacity
          style={[styles.boton, styles.descartar]}
          onPress={onDescartar}
          disabled={guardando}
        >
          <Text style={styles.texto}>🗑️ Descartar</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.boton} onPress={onGuardar} disabled={guardando}>
          {guardando ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <Text style={styles.texto}>💾 Guardar</Text>
          )}
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  contenedor: { flex: 1, backgroundColor: '#000' },
  foto: { flex: 1 },
  barra: {
    flexDirection: 'row',
    gap: 12,
    padding: 20,
    backgroundColor: '#0f172a',
  },
  boton: {
    flex: 1,
    backgroundColor: '#6366f1',
    padding: 14,
    borderRadius: 12,
    alignItems: 'center',
  },
  descartar: { backgroundColor: '#475569' },
  texto: { color: '#fff', fontWeight: 'bold', fontSize: 16 },
});

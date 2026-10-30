import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';

export default function TarjetaProducto({ item, seleccionado, onEditar, onEliminar }) {
  return (
    <View style={[styles.tarjeta, seleccionado && styles.seleccionado]}>
      <View style={styles.info}>
        <Text style={styles.nombre}>{item.producto}</Text>
        {!!item.descripcion && <Text style={styles.descripcion}>{item.descripcion}</Text>}
        <Text style={styles.precio}>${Number(item.precio).toFixed(2)}</Text>
      </View>

      <View style={styles.acciones}>
        <TouchableOpacity style={styles.botonEditar} onPress={() => onEditar(item)}>
          <Text style={styles.icono}>✏️</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.botonEliminar} onPress={() => onEliminar(item)}>
          <Text style={styles.icono}>🗑️</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  tarjeta: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff',
    borderRadius: 14,
    padding: 14,
    marginBottom: 10,
    borderWidth: 2,
    borderColor: 'transparent',
  },
  seleccionado: { borderColor: '#f59e0b' },
  info: { flex: 1, paddingRight: 8 },
  nombre: { fontSize: 16, fontWeight: 'bold', color: '#0f172a' },
  descripcion: { fontSize: 14, color: '#64748b', marginTop: 2 },
  precio: { fontSize: 16, fontWeight: 'bold', color: '#0d9488', marginTop: 4 },
  acciones: { flexDirection: 'row', gap: 8 },
  botonEditar: {
    backgroundColor: '#fef3c7',
    width: 42,
    height: 42,
    borderRadius: 21,
    alignItems: 'center',
    justifyContent: 'center',
  },
  botonEliminar: {
    backgroundColor: '#fee2e2',
    width: 42,
    height: 42,
    borderRadius: 21,
    alignItems: 'center',
    justifyContent: 'center',
  },
  icono: { fontSize: 18 },
});

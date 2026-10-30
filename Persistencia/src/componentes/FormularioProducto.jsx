import { useEffect, useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
  ActivityIndicator,
  Keyboard,
} from 'react-native';

export default function FormularioProducto({ editando, onGuardar, onCancelarEdicion }) {
  const [producto, setProducto] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [precio, setPrecio] = useState('');
  const [guardando, setGuardando] = useState(false);

  useEffect(() => {
    if (editando) {
      setProducto(editando.producto ?? '');
      setDescripcion(editando.descripcion ?? '');
      setPrecio(String(editando.precio ?? ''));
    } else {
      limpiar();
    }
  }, [editando]);

  const limpiar = () => {
    setProducto('');
    setDescripcion('');
    setPrecio('');
  };

  const enviar = async () => {
    const nombre = producto.trim();
    const numero = Number(precio.replace(',', '.'));

    if (!nombre) {
      Alert.alert('Falta el nombre', 'Escribe el nombre del producto.');
      return;
    }
    if (precio.trim() === '' || Number.isNaN(numero) || numero < 0) {
      Alert.alert('Precio inválido', 'Escribe un número, por ejemplo 149.90');
      return;
    }

    setGuardando(true);
    const ok = await onGuardar({
      producto: nombre,
      descripcion: descripcion.trim(),
      precio: numero,
    });
    setGuardando(false);

    if (ok) {
      Keyboard.dismiss();
      if (!editando) limpiar();
    }
  };

  return (
    <View style={[styles.tarjeta, editando && styles.tarjetaEditando]}>
      <Text style={styles.titulo}>
        {editando ? '✏️ Editando producto' : '🛒 Nuevo producto'}
      </Text>

      <TextInput
        style={styles.input}
        placeholder="Nombre del producto"
        value={producto}
        onChangeText={setProducto}
      />
      <TextInput
        style={[styles.input, styles.multilinea]}
        placeholder="Descripción (opcional)"
        value={descripcion}
        onChangeText={setDescripcion}
        multiline
      />
      <TextInput
        style={styles.input}
        placeholder="Precio"
        value={precio}
        onChangeText={setPrecio}
        keyboardType="decimal-pad"
      />

      <View style={styles.fila}>
        {editando && (
          <TouchableOpacity
            style={[styles.boton, styles.botonGris]}
            onPress={onCancelarEdicion}
            disabled={guardando}
          >
            <Text style={styles.textoGris}>Cancelar</Text>
          </TouchableOpacity>
        )}
        <TouchableOpacity
          style={[styles.boton, guardando && styles.deshabilitado]}
          onPress={enviar}
          disabled={guardando}
        >
          {guardando ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <Text style={styles.textoBoton}>{editando ? 'Actualizar' : 'Agregar'}</Text>
          )}
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  tarjeta: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 16,
    borderWidth: 2,
    borderColor: 'transparent',
    elevation: 2,
  },
  tarjetaEditando: { borderColor: '#f59e0b' },
  titulo: { fontSize: 18, fontWeight: 'bold', color: '#0f172a', marginBottom: 12 },
  input: {
    backgroundColor: '#f1f5f9',
    borderRadius: 10,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 16,
    marginBottom: 10,
  },
  multilinea: { minHeight: 64, textAlignVertical: 'top' },
  fila: { flexDirection: 'row', gap: 10 },
  boton: {
    flex: 1,
    backgroundColor: '#0d9488',
    borderRadius: 10,
    paddingVertical: 12,
    alignItems: 'center',
  },
  botonGris: { backgroundColor: '#e2e8f0' },
  deshabilitado: { opacity: 0.6 },
  textoBoton: { color: '#fff', fontWeight: 'bold', fontSize: 16 },
  textoGris: { color: '#0f172a', fontWeight: 'bold', fontSize: 16 },
});

import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  View,
  Text,
  TextInput,
  FlatList,
  StyleSheet,
  Alert,
  ActivityIndicator,
  RefreshControl,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { StatusBar } from 'expo-status-bar';
import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context';

import { configurado } from './src/lib/supabase';
import {
  obtenerProductos,
  crearProducto,
  actualizarProducto,
  eliminarProducto,
} from './src/servicios/productos';
import FormularioProducto from './src/componentes/FormularioProducto';
import TarjetaProducto from './src/componentes/TarjetaProducto';

export default function App() {
  return (
    <SafeAreaProvider>
      <SafeAreaView style={styles.pantalla} edges={['top', 'left', 'right']}>
        <StatusBar style="dark" />
        {configurado ? <Inventario /> : <SinConfiguracion />}
      </SafeAreaView>
    </SafeAreaProvider>
  );
}

function Inventario() {
  const [productos, setProductos] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [refrescando, setRefrescando] = useState(false);
  const [error, setError] = useState(null);
  const [editando, setEditando] = useState(null);
  const [busqueda, setBusqueda] = useState('');

  const cargar = useCallback(async () => {
    try {
      setProductos(await obtenerProductos());
      setError(null);
    } catch (e) {
      setError(e.message);
    }
  }, []);

  useEffect(() => {
    cargar().finally(() => setCargando(false));
  }, [cargar]);

  const refrescar = async () => {
    setRefrescando(true);
    await cargar();
    setRefrescando(false);
  };

  const guardar = async (datos) => {
    try {
      if (editando) {
        const actualizado = await actualizarProducto(editando.id, datos);
        setProductos((lista) => lista.map((p) => (p.id === actualizado.id ? actualizado : p)));
        setEditando(null);
        Alert.alert('Actualizado', `"${actualizado.producto}" se guardó.`);
      } else {
        const nuevo = await crearProducto(datos);
        setProductos((lista) => [nuevo, ...lista]);
      }
      return true;
    } catch (e) {
      Alert.alert('Error', e.message);
      return false;
    }
  };

  const confirmarEliminar = (item) => {
    Alert.alert('Eliminar', `¿Borrar "${item.producto}"? No se puede deshacer.`, [
      { text: 'Cancelar', style: 'cancel' },
      {
        text: 'Eliminar',
        style: 'destructive',
        onPress: async () => {
          try {
            await eliminarProducto(item.id);
            setProductos((lista) => lista.filter((p) => p.id !== item.id));
            if (editando?.id === item.id) setEditando(null);
          } catch (e) {
            Alert.alert('Error', e.message);
          }
        },
      },
    ]);
  };

  const visibles = useMemo(() => {
    const texto = busqueda.trim().toLowerCase();
    if (!texto) return productos;
    return productos.filter(
      (p) =>
        p.producto.toLowerCase().includes(texto) ||
        (p.descripcion ?? '').toLowerCase().includes(texto)
    );
  }, [productos, busqueda]);

  const total = useMemo(
    () => productos.reduce((suma, p) => suma + Number(p.precio || 0), 0),
    [productos]
  );

  const encabezado = (
    <View>
      <Text style={styles.titulo}>Mi inventario</Text>
      <Text style={styles.autor}>Jack Brandon Espinosa</Text>
      <Text style={styles.subtitulo}>
        {productos.length} productos · Valor total ${total.toFixed(2)}
      </Text>

      <FormularioProducto
        editando={editando}
        onGuardar={guardar}
        onCancelarEdicion={() => setEditando(null)}
      />

      <TextInput
        style={styles.buscador}
        placeholder="🔍 Buscar producto"
        value={busqueda}
        onChangeText={setBusqueda}
      />

      {error && <Text style={styles.error}>❌ {error}</Text>}
    </View>
  );

  if (cargando) {
    return (
      <View style={styles.centro}>
        <ActivityIndicator size="large" color="#0d9488" />
        <Text style={styles.subtitulo}>Cargando productos…</Text>
      </View>
    );
  }

  return (
    <KeyboardAvoidingView
      style={styles.pantalla}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
    >
      <FlatList
        data={visibles}
        keyExtractor={(item) => String(item.id)}
        ListHeaderComponent={encabezado}
        contentContainerStyle={styles.lista}
        keyboardShouldPersistTaps="handled"
        refreshControl={<RefreshControl refreshing={refrescando} onRefresh={refrescar} />}
        ListEmptyComponent={
          <Text style={styles.vacio}>
            {busqueda ? 'Ningún producto coincide con la búsqueda.' : 'Aún no hay productos.'}
          </Text>
        }
        renderItem={({ item }) => (
          <TarjetaProducto
            item={item}
            seleccionado={editando?.id === item.id}
            onEditar={setEditando}
            onEliminar={confirmarEliminar}
          />
        )}
      />
    </KeyboardAvoidingView>
  );
}

function SinConfiguracion() {
  return (
    <View style={styles.centro}>
      <Text style={styles.titulo}>Falta conectar Supabase</Text>
      <Text style={styles.subtitulo}>
        Copia el archivo .env.example como .env.local, pon la URL y la anon key de tu
        proyecto y reinicia con "npx expo start -c".
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  pantalla: { flex: 1, backgroundColor: '#f1f5f9' },
  centro: { flex: 1, alignItems: 'center', justifyContent: 'center', padding: 24, gap: 8 },
  lista: { padding: 16, paddingBottom: 40 },
  titulo: { fontSize: 26, fontWeight: 'bold', color: '#0f172a', textAlign: 'left' },
  autor: { fontSize: 13, color: '#0d9488', fontWeight: '600' },
  subtitulo: { fontSize: 14, color: '#64748b', marginBottom: 14 },
  buscador: {
    backgroundColor: '#fff',
    borderRadius: 12,
    paddingHorizontal: 14,
    paddingVertical: 10,
    fontSize: 16,
    marginTop: 16,
    marginBottom: 12,
  },
  error: { color: '#b91c1c', marginBottom: 12 },
  vacio: { textAlign: 'center', color: '#64748b', marginTop: 16, fontSize: 16 },
});

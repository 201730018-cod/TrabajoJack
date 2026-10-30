import { View, Text, TouchableOpacity, StyleSheet, Linking } from 'react-native';

export default function PantallaPermisos({ rechazados, onPedir }) {
  return (
    <View style={styles.contenedor}>
      <Text style={styles.icono}>📷</Text>
      <Text style={styles.titulo}>Permisos necesarios</Text>
      <Text style={styles.texto}>
        Para reconocer rostros la app usa la cámara, y para guardar tus fotos
        necesita acceso a la galería.
      </Text>

      {rechazados ? (
        <>
          <Text style={styles.aviso}>
            Rechazaste algún permiso. Actívalo desde los ajustes del celular.
          </Text>
          <TouchableOpacity style={styles.boton} onPress={() => Linking.openSettings()}>
            <Text style={styles.textoBoton}>Ir a ajustes</Text>
          </TouchableOpacity>
        </>
      ) : (
        <TouchableOpacity style={styles.boton} onPress={onPedir}>
          <Text style={styles.textoBoton}>Permitir</Text>
        </TouchableOpacity>
      )}

      <Text style={styles.autor}>Jack Brandon Espinosa</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  contenedor: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 28,
    backgroundColor: '#0f172a',
  },
  icono: { fontSize: 56, marginBottom: 8 },
  titulo: { color: '#fff', fontSize: 24, fontWeight: 'bold', marginBottom: 10 },
  texto: { color: '#cbd5e1', fontSize: 16, textAlign: 'center', marginBottom: 24 },
  aviso: { color: '#fbbf24', textAlign: 'center', marginBottom: 16 },
  boton: {
    backgroundColor: '#6366f1',
    paddingVertical: 12,
    paddingHorizontal: 32,
    borderRadius: 24,
  },
  textoBoton: { color: '#fff', fontSize: 16, fontWeight: 'bold' },
  autor: { position: 'absolute', bottom: 32, color: '#64748b', fontSize: 13 },
});

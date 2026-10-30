import { View, Text, StyleSheet } from 'react-native';

export default function MarcoRostro({ rostro }) {
  if (!rostro) return null;

  const { bounds, nombre, hayPersonas } = rostro;

  let color = '#38bdf8';
  let etiqueta = 'Rostro';
  if (nombre) {
    color = '#22c55e';
    etiqueta = nombre;
  } else if (hayPersonas) {
    color = '#ef4444';
    etiqueta = '¿Quién eres?';
  }

  return (
    <View
      pointerEvents="none"
      style={[
        styles.marco,
        {
          left: bounds.x,
          top: bounds.y,
          width: bounds.width,
          height: bounds.height,
          borderColor: color,
        },
      ]}
    >
      <View style={[styles.etiqueta, { backgroundColor: color }]}>
        <Text style={styles.textoEtiqueta}>{etiqueta}</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  marco: {
    position: 'absolute',
    borderWidth: 3,
    borderRadius: 16,
  },
  etiqueta: {
    position: 'absolute',
    bottom: -30,
    alignSelf: 'center',
    paddingHorizontal: 10,
    paddingVertical: 3,
    borderRadius: 12,
  },
  textoEtiqueta: { color: '#fff', fontWeight: 'bold', fontSize: 14 },
});

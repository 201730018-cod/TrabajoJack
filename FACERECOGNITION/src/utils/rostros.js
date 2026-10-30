import AsyncStorage from '@react-native-async-storage/async-storage';

export const SIMILITUD_MINIMA = 0.6;

const CLAVE_STORAGE = '@personas_registradas';

function normalizar(vector) {
  const largo = Math.sqrt(vector.reduce((suma, v) => suma + v * v, 0)) || 1;
  return vector.map((v) => v / largo);
}

export function embeddingDesdeRostro(rostro) {
  if (!rostro?.data || rostro.data.length === 0) return null;
  return normalizar(rostro.data.map(Number));
}

export function similitud(a, b) {
  if (!a || !b || a.length !== b.length) return 0;
  let producto = 0;
  for (let i = 0; i < a.length; i++) producto += a[i] * b[i];
  return producto;
}

export function promedio(muestras) {
  const suma = new Array(muestras[0].length).fill(0);
  for (const muestra of muestras) {
    muestra.forEach((v, i) => (suma[i] += v));
  }
  return normalizar(suma);
}

export function reconocer(embedding, personas) {
  let mejor = null;
  let mejorSimilitud = 0;

  for (const persona of personas) {
    const s = similitud(embedding, persona.embedding);
    if (s > mejorSimilitud) {
      mejorSimilitud = s;
      mejor = persona;
    }
  }

  return {
    nombre: mejor && mejorSimilitud >= SIMILITUD_MINIMA ? mejor.nombre : null,
    similitud: mejor ? mejorSimilitud : null,
  };
}

export async function leerPersonas() {
  try {
    const json = await AsyncStorage.getItem(CLAVE_STORAGE);
    return json ? JSON.parse(json) : [];
  } catch {
    return [];
  }
}

export async function guardarPersonas(personas) {
  try {
    await AsyncStorage.setItem(CLAVE_STORAGE, JSON.stringify(personas));
  } catch (error) {
    console.warn('No se pudieron guardar las personas:', error);
  }
}

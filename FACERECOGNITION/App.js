import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Alert,
  ActivityIndicator,
  useWindowDimensions,
} from 'react-native';
import { StatusBar } from 'expo-status-bar';
import { Camera, useCameraDevice, usePhotoOutput } from 'react-native-vision-camera';
import { initTensor, useFaceScannerOutput } from 'vision-camera-face-detection';
import * as MediaLibrary from 'expo-media-library/legacy';

import usePermisos from './src/hooks/usePermisos';
import PantallaPermisos from './src/componentes/PantallaPermisos';
import MarcoRostro from './src/componentes/MarcoRostro';
import ModalRegistro from './src/componentes/ModalRegistro';
import ListaPersonas from './src/componentes/ListaPersonas';
import VistaFoto from './src/componentes/VistaFoto';
import {
  SIMILITUD_MINIMA,
  embeddingDesdeRostro,
  promedio,
  reconocer,
  leerPersonas,
  guardarPersonas,
} from './src/utils/rostros';

const MODELO = 'facenet_512';
const MUESTRAS_PARA_REGISTRO = 5;

export default function App() {
  const permisos = usePermisos();
  const { width, height } = useWindowDimensions();

  const [lado, setLado] = useState('front');
  const [modeloListo, setModeloListo] = useState(false);
  const [rostro, setRostro] = useState(null);
  const [totalRostros, setTotalRostros] = useState(0);
  const [personas, setPersonas] = useState([]);
  const [registrando, setRegistrando] = useState(false);
  const [verLista, setVerLista] = useState(false);
  const [fotoUri, setFotoUri] = useState(null);
  const [tomandoFoto, setTomandoFoto] = useState(false);
  const [guardandoFoto, setGuardandoFoto] = useState(false);

  const personasRef = useRef([]);
  const muestras = useRef([]);
  const muestraParaRegistrar = useRef(null);
  const ultimaActualizacion = useRef(0);

  const camara = useCameraDevice(lado);
  const salidaFoto = usePhotoOutput();

  useEffect(() => {
    leerPersonas().then((lista) => {
      personasRef.current = lista;
      setPersonas(lista);
    });
  }, []);

  useEffect(() => {
    if (!permisos.concedidos) return;
    try {
      console.log(initTensor(MODELO));
      setModeloListo(true);
    } catch (error) {
      console.warn('No se encontró el modelo de reconocimiento:', error);
      setModeloListo(false);
    }
  }, [permisos.concedidos]);

  const actualizarPersonas = async (lista) => {
    personasRef.current = lista;
    setPersonas(lista);
    await guardarPersonas(lista);
  };

  const onRostros = useCallback((rostros) => {
    if (!rostros || rostros.length === 0) {
      muestras.current = [];
      setRostro(null);
      setTotalRostros(0);
      return;
    }

    const principal = rostros.reduce((a, b) =>
      b.bounds.width * b.bounds.height > a.bounds.width * a.bounds.height ? b : a
    );

    let resultado = { nombre: null, similitud: null };
    const embedding = embeddingDesdeRostro(principal);
    if (embedding) {
      muestras.current = [...muestras.current, embedding].slice(-MUESTRAS_PARA_REGISTRO);
      resultado = reconocer(embedding, personasRef.current);
    }

    const ahora = Date.now();
    if (ahora - ultimaActualizacion.current < 125) return;
    ultimaActualizacion.current = ahora;

    setTotalRostros(rostros.length);
    setRostro({
      bounds: principal.bounds,
      nombre: resultado.nombre,
      similitud: resultado.similitud,
      hayPersonas: personasRef.current.length > 0,
    });
  }, []);

  const opcionesDetector = useMemo(
    () => ({
      performanceMode: 'fast',
      runClassifications: true,
      windowWidth: width,
      windowHeight: height,
    }),
    [width, height]
  );

  const salidaRostros = useFaceScannerOutput({
    ...opcionesDetector,
    autoMode: true,
    cameraFacing: lado,
    onFaceScanned: onRostros,
    onError: (error) => console.warn('Error del detector:', error),
  });

  const empezarRegistro = () => {
    if (!modeloListo) {
      Alert.alert(
        'Falta el modelo',
        `Copia ${MODELO}.tflite en assets/modelos y vuelve a compilar la app (revisa el README).`
      );
      return;
    }
    if (muestras.current.length === 0) {
      Alert.alert('Sin rostro', 'Mira a la cámara hasta que aparezca el recuadro.');
      return;
    }
    muestraParaRegistrar.current = promedio(muestras.current);
    setRegistrando(true);
  };

  const registrarPersona = async (nombre) => {
    setRegistrando(false);
    const embedding = muestraParaRegistrar.current;
    muestraParaRegistrar.current = null;
    if (!embedding) return;

    const otras = personasRef.current.filter(
      (p) => p.nombre.toLowerCase() !== nombre.toLowerCase()
    );
    await actualizarPersonas([...otras, { nombre, embedding }]);
    Alert.alert('¡Listo!', `${nombre} quedó registrado.`);
  };

  const borrarPersona = (nombre) =>
    actualizarPersonas(personasRef.current.filter((p) => p.nombre !== nombre));

  const tomarFoto = async () => {
    if (tomandoFoto) return;
    setTomandoFoto(true);
    try {
      const foto = await salidaFoto.capturePhotoToFile({ flashMode: 'off' }, {});
      const ruta = foto.filePath;
      setFotoUri(ruta.startsWith('file://') ? ruta : `file://${ruta}`);
    } catch (error) {
      console.warn('Error al tomar la foto:', error);
      Alert.alert('Error', 'No se pudo tomar la foto.');
    } finally {
      setTomandoFoto(false);
    }
  };

  const guardarFoto = async () => {
    setGuardandoFoto(true);
    try {
      await MediaLibrary.saveToLibraryAsync(fotoUri);
      Alert.alert('Guardada', 'La foto está en tu galería.');
      setFotoUri(null);
    } catch (error) {
      console.warn('Error al guardar la foto:', error);
      Alert.alert('Error', 'No se pudo guardar la foto.');
    } finally {
      setGuardandoFoto(false);
    }
  };

  if (permisos.cargando) {
    return (
      <View style={styles.centro}>
        <ActivityIndicator size="large" color="#6366f1" />
      </View>
    );
  }

  if (!permisos.concedidos) {
    return <PantallaPermisos rechazados={permisos.rechazados} onPedir={permisos.pedir} />;
  }

  if (fotoUri) {
    return (
      <VistaFoto
        uri={fotoUri}
        guardando={guardandoFoto}
        onGuardar={guardarFoto}
        onDescartar={() => setFotoUri(null)}
      />
    );
  }

  if (!camara) {
    return (
      <View style={styles.centro}>
        <Text style={styles.textoClaro}>Este dispositivo no tiene cámara disponible.</Text>
      </View>
    );
  }

  let estado = 'Buscando rostros…';
  if (rostro?.nombre) estado = `✅ Hola, ${rostro.nombre}`;
  else if (rostro?.hayPersonas) estado = '❓ Rostro no registrado';
  else if (rostro) estado = '🙂 Rostro detectado';

  return (
    <View style={styles.contenedor}>
      <StatusBar style="light" />

      <Camera
        style={StyleSheet.absoluteFill}
        device={camara}
        isActive={!registrando && !verLista}
        orientationSource="device"
        outputs={[salidaRostros, salidaFoto]}
        onError={(error) => console.warn('Error de cámara:', error)}
      />

      <MarcoRostro rostro={rostro} />

      <View style={styles.panel}>
        <Text style={styles.estado}>{estado}</Text>
        <Text style={styles.detalle}>
          Rostros en cámara: {totalRostros} · Registrados: {personas.length}
        </Text>
        {rostro?.similitud != null && (
          <Text style={styles.detalle}>
            Parecido: {Math.round(rostro.similitud * 100)}% (mínimo{' '}
            {Math.round(SIMILITUD_MINIMA * 100)}%)
          </Text>
        )}
        {!modeloListo && (
          <Text style={styles.aviso}>
            Sin modelo de reconocimiento: solo se detectan rostros.
          </Text>
        )}
      </View>

      <View style={styles.barra}>
        <TouchableOpacity style={styles.botonChico} onPress={() => setVerLista(true)}>
          <Text style={styles.textoBotonChico}>👥</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.disparador, tomandoFoto && styles.deshabilitado]}
          onPress={tomarFoto}
          disabled={tomandoFoto}
        >
          <View style={styles.disparadorCentro} />
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.botonChico}
          onPress={() => setLado((l) => (l === 'front' ? 'back' : 'front'))}
        >
          <Text style={styles.textoBotonChico}>🔄</Text>
        </TouchableOpacity>
      </View>

      <TouchableOpacity style={styles.botonRegistrar} onPress={empezarRegistro}>
        <Text style={styles.textoRegistrar}>➕ Registrar rostro</Text>
      </TouchableOpacity>

      <ModalRegistro
        visible={registrando}
        onGuardar={registrarPersona}
        onCerrar={() => {
          muestraParaRegistrar.current = null;
          setRegistrando(false);
        }}
      />

      <ListaPersonas
        visible={verLista}
        personas={personas}
        onBorrar={borrarPersona}
        onCerrar={() => setVerLista(false)}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  contenedor: { flex: 1, backgroundColor: '#000' },
  centro: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#0f172a',
    padding: 24,
  },
  textoClaro: { color: '#fff', fontSize: 16, textAlign: 'center' },
  panel: {
    position: 'absolute',
    top: 48,
    left: 16,
    right: 16,
    backgroundColor: 'rgba(15,23,42,0.75)',
    borderRadius: 16,
    padding: 12,
  },
  estado: { color: '#fff', fontSize: 20, fontWeight: 'bold' },
  detalle: { color: '#cbd5e1', fontSize: 13, marginTop: 2 },
  aviso: { color: '#fbbf24', fontSize: 13, marginTop: 6 },
  botonRegistrar: {
    position: 'absolute',
    bottom: 140,
    alignSelf: 'center',
    backgroundColor: '#6366f1',
    paddingVertical: 10,
    paddingHorizontal: 22,
    borderRadius: 24,
  },
  textoRegistrar: { color: '#fff', fontWeight: 'bold', fontSize: 15 },
  barra: {
    position: 'absolute',
    bottom: 40,
    left: 0,
    right: 0,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-evenly',
  },
  botonChico: {
    width: 52,
    height: 52,
    borderRadius: 26,
    backgroundColor: 'rgba(15,23,42,0.7)',
    alignItems: 'center',
    justifyContent: 'center',
  },
  textoBotonChico: { fontSize: 24 },
  disparador: {
    width: 78,
    height: 78,
    borderRadius: 39,
    borderWidth: 4,
    borderColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  disparadorCentro: { width: 60, height: 60, borderRadius: 30, backgroundColor: '#fff' },
  deshabilitado: { opacity: 0.5 },
});

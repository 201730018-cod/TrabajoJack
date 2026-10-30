import { useState } from 'react';
import { useCameraPermission } from 'react-native-vision-camera';
import * as MediaLibrary from 'expo-media-library/legacy';

export default function usePermisos() {
  const camara = useCameraPermission();
  const [galeria, pedirGaleria] = MediaLibrary.usePermissions({ writeOnly: true });
  const [rechazados, setRechazados] = useState(false);

  const cargando = galeria === null;
  const concedidos = !!camara.hasPermission && !!galeria?.granted;

  const pedir = async () => {
    const okCamara = camara.hasPermission || (await camara.requestPermission());
    const okGaleria = galeria?.granted || (await pedirGaleria())?.granted;
    setRechazados(!(okCamara && okGaleria));
  };

  return { cargando, concedidos, rechazados, pedir };
}

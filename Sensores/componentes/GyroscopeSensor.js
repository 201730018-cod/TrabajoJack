import { useEffect, useState, useRef } from "react";
import { View, Text, StyleSheet, Animated, Dimensions } from "react-native";
import { Gyroscope } from "expo-sensors";

// Obtenemos el tamaño de la pantalla para que la pelota no se salga
const { width, height } = Dimensions.get("window");
const TAMANO_PELOTA = 50; 

// Calculamos los límites máximos hacia arriba/abajo e izquierda/derecha
const LIMITE_X = (width / 2) - (TAMANO_PELOTA / 2);
const LIMITE_Y = (height / 2) - (TAMANO_PELOTA / 2);

export default function GyroscopeSensor() {
    const [datos, setDatos] = useState({ x: 0, y: 0, z: 0 });

    // Valor animado que controlará el movimiento (X, Y) en la pantalla
    const posicionPelota = useRef(new Animated.ValueXY({ x: 0, y: 0 })).current;
    
    // Referencia interna para hacer matemáticas rápidas sin recargar toda la pantalla
    const coordenadasActuales = useRef({ x: 0, y: 0 });

    useEffect(() => {
        const subscribir = Gyroscope.addListener(measurements => {
            setDatos(measurements);

            // VELOCIDAD: Puedes subir o bajar este número para que la pelota se mueva más rápido o lento
            const VELOCIDAD = 15; 

            // Eje Y del giroscopio mueve de izquierda a derecha.
            // Eje X del giroscopio mueve de arriba a abajo.
            // (Si notas que va al revés, puedes cambiar el "+" por un "-")
            let nuevaX = coordenadasActuales.current.x + (measurements.y * VELOCIDAD);
            let nuevaY = coordenadasActuales.current.y + (measurements.x * VELOCIDAD);

            // Esto evita que la pelota cruce los límites de la pantalla (choca con los bordes)
            nuevaX = Math.max(-LIMITE_X, Math.min(LIMITE_X, nuevaX));
            nuevaY = Math.max(-LIMITE_Y, Math.min(LIMITE_Y, nuevaY));

            // Guardamos la nueva posición
            coordenadasActuales.current = { x: nuevaX, y: nuevaY };

            // Movemos visualmente la pelotita
            posicionPelota.setValue(coordenadasActuales.current);
        });

        // 30 milisegundos (~30 FPS) para que el movimiento de la pelota se vea muy fluido
        Gyroscope.setUpdateInterval(30);

        return () => {
            subscribir.remove();
        }
    }, []);

    return (
        <View style={styles.container}>
            {/* --- LA PELOTITA --- */}
            <Animated.View style={[
                styles.pelota, 
                { 
                    // Enlazamos las coordenadas matemáticas al movimiento de la vista
                    transform: [
                        { translateX: posicionPelota.x },
                        { translateY: posicionPelota.y }
                    ] 
                }
            ]} />
            
            {/* Interfaz original enviada al fondo usando zIndex para que la pelota pase por encima */}
            <View style={styles.uiContainer}>
                <Text style={styles.title}>Giroscopio</Text>
                <View style={styles.card}>
                    <Text style={styles.axis}>X</Text>
                    <Text style={styles.value}>{datos.x.toFixed(2)}</Text>
                </View>
                <View style={styles.card}>
                    <Text style={styles.axis}>Y</Text>
                    <Text style={styles.value}>{datos.y.toFixed(2)}</Text>
                </View>
                <View style={styles.card}>
                    <Text style={styles.axis}>Z</Text>
                    <Text style={styles.value}>{datos.z.toFixed(2)}</Text>
                </View>
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        backgroundColor: "#efefef",
    },
    // Estilos nuevos para la pelotita
    pelota: {
        position: 'absolute', // Absoluto para que flote sobre todo lo demás
        alignSelf: 'center',  // Inicia en el centro
        width: TAMANO_PELOTA,
        height: TAMANO_PELOTA,
        borderRadius: TAMANO_PELOTA / 2, // Hace que sea un círculo perfecto
        backgroundColor: '#FF5252', // Color de la pelota (Rojo)
        zIndex: 10, // Se asegura de que pase por encima de las tarjetas blancas
        elevation: 10, // Sombra en Android
        shadowColor: '#000', // Sombra en iOS
        shadowOffset: { width: 0, height: 4 },
        shadowOpacity: 0.3,
        shadowRadius: 4,
    },
    uiContainer: {
        padding: 25,
        zIndex: 1, // Mantiene la UI por debajo de la pelota
    },
    title: {
        fontSize: 35,
        textAlign: 'center',
        marginBottom: 35,
        color: "#3a4a5a"
    },
    card: {
        backgroundColor: "#fff",
        padding: 20,
        marginBottom: 20,
        flexDirection: 'row',
        justifyContent: 'space-between',
        borderRadius: 10,
    },
    axis: {
        fontSize: 24,
        fontWeight: 'bold',
    },
    value: {
        fontSize: 24,
        fontWeight: 'bold',
    },
});
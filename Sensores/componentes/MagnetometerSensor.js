import { useEffect, useState } from "react";
import { View, Text, StyleSheet } from "react-native";
import { Magnetometer } from "expo-sensors";

export default function MagnetometerSensor() {
    const [datos, setDatos] = useState({ x: 0, y: 0, z: 0 });
    const [anguloHeading, setAnguloHeading] = useState(0); // Hacia dónde apunta el teléfono
    const [direccionPunto, setDireccionPunto] = useState("N"); // Texto (N, S, E, O)

    useEffect(() => {
        const subscribir = Magnetometer.addListener(measurements => {
            setDatos(measurements);
            const { x, y } = measurements;

            // 1. Calculamos el ángulo en radianes y lo pasamos a grados
            let angle = Math.atan2(y, x) * (180 / Math.PI);

            // 2. Ajustamos la fórmula para que el Norte sea exactamente 0 grados
            let heading = 90 - angle;
            if (heading < 0) {
                heading += 360;
            }

            setAnguloHeading(heading);

            // 3. Calculamos la letra de la dirección (Norte, Sur, Este, Oeste)
            if (heading >= 337.5 || heading < 22.5) setDireccionPunto("N");
            else if (heading >= 22.5 && heading < 67.5) setDireccionPunto("NE");
            else if (heading >= 67.5 && heading < 112.5) setDireccionPunto("E");
            else if (heading >= 112.5 && heading < 157.5) setDireccionPunto("SE");
            else if (heading >= 157.5 && heading < 202.5) setDireccionPunto("S");
            else if (heading >= 202.5 && heading < 247.5) setDireccionPunto("SO");
            else if (heading >= 247.5 && heading < 292.5) setDireccionPunto("O");
            else if (heading >= 292.5 && heading < 337.5) setDireccionPunto("NO");
        });

        // 50ms es ideal para brújulas: fluido pero no sobrecarga el teléfono
        Magnetometer.setUpdateInterval(50);

        return () => {
            subscribir.remove();
        }
    }, []);

    // Si tu teléfono gira hacia los 90° (Este), la aguja debe rotar hacia los -90° para 
    // seguir apuntando al norte fijo.
    const rotacionAguja = `${360 - anguloHeading}deg`;

    return (
        <View style={styles.container}>
            <Text style={styles.title}>Brújula</Text>

            {/* --- UI DE LA BRÚJULA --- */}
            <View style={styles.compassFace}>
                {/* Puntos cardinales estáticos en el fondo de la brújula */}
                <Text style={[styles.marker, { top: 10 }]}>N</Text>
                <Text style={[styles.marker, { bottom: 10 }]}>S</Text>
                <Text style={[styles.marker, { right: 15 }]}>E</Text>
                <Text style={[styles.marker, { left: 15 }]}>O</Text>

                {/* Aguja que rota */}
                <View style={[styles.agujaContenedor, { transform: [{ rotate: rotacionAguja }] }]}>
                    <View style={styles.flechaNorte} />
                    <View style={styles.flechaSur} />
                </View>
            </View>

            {/* Texto de Grados y Dirección */}
            <Text style={styles.headingText}>
                {Math.round(anguloHeading)}° {direccionPunto}
            </Text>

            {/* --- TUS TARJETAS ORIGINALES (X, Y, Z) --- */}
            <View style={styles.datosContainer}>
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
        justifyContent: 'flex-start', // Cambiado para acomodar todo desde arriba
        paddingTop: 60, // Margen superior
        paddingHorizontal: 25,
        backgroundColor: "#efefef"
    },
    title: {
        fontSize: 35,
        textAlign: 'center',
        marginBottom: 20,
        color: "#3a4a5a",
        fontWeight: 'bold',
    },
    // --- ESTILOS DE LA BRÚJULA ---
    compassFace: {
        width: 250,
        height: 250,
        borderRadius: 125, // Círculo perfecto
        borderWidth: 6,
        borderColor: '#3a4a5a',
        backgroundColor: '#fff',
        justifyContent: 'center',
        alignItems: 'center',
        alignSelf: 'center', // Centrado en la pantalla
        elevation: 10,
        shadowColor: '#000',
        shadowOpacity: 0.1,
        shadowRadius: 10,
    },
    marker: {
        position: 'absolute',
        fontSize: 22,
        fontWeight: 'bold',
        color: '#888',
    },
    agujaContenedor: {
        position: 'absolute',
        width: 30,
        height: 200, // Largo de la aguja
        alignItems: 'center',
        justifyContent: 'center',
    },
    // Truco de bordes CSS para hacer un triángulo apuntando hacia arriba (Norte)
    flechaNorte: {
        width: 0,
        height: 0,
        borderLeftWidth: 15,
        borderRightWidth: 15,
        borderBottomWidth: 100, // Alto del triángulo
        borderLeftColor: 'transparent',
        borderRightColor: 'transparent',
        borderBottomColor: '#FF5252', // Rojo para el norte
    },
    // Triángulo apuntando hacia abajo (Sur)
    flechaSur: {
        width: 0,
        height: 0,
        borderLeftWidth: 15,
        borderRightWidth: 15,
        borderTopWidth: 100, // Alto del triángulo
        borderLeftColor: 'transparent',
        borderRightColor: 'transparent',
        borderTopColor: '#555', // Gris para el sur
    },
    headingText: {
        fontSize: 40,
        textAlign: 'center',
        fontWeight: 'bold',
        color: '#FF5252',
        marginVertical: 20,
    },
    // --- TUS ESTILOS ORIGINALES ---
    datosContainer: {
        flex: 1,
        justifyContent: 'flex-end',
        paddingBottom: 20,
    },
    card: {
        backgroundColor: "#fff",
        padding: 20,
        marginBottom: 10,
        flexDirection: 'row',
        justifyContent: 'space-between',
        borderRadius: 8,
    },
    axis: {
        fontSize: 24,
        fontWeight: 'bold',
        color: "#333",
    },
    value: {
        fontSize: 24,
        fontWeight: 'bold',
        color: "#333",
    },
});
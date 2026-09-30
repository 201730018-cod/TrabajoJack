import { useEffect, useState } from "react";
import { View, Text, StyleSheet } from "react-native";

import { Accelerometer } from "expo-sensors";

export default function AccelerometerSensor (){
    const [datos, setDatos]= useState({
        x:0,
        y:0,
        z:0,
    });


useEffect(
    ()=>{
        const subscribir = Accelerometer.addListener(
            measurements => {setDatos(measurements)}
        );

        Accelerometer.setUpdateInterval(100);

        return () => {
            subscribir.remove();
        }
    },[]

);

return (
    <View style={styles.container }>
        <Text style={styles.title}>
            Acelerómetro
        </Text>
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
);

}

const styles = StyleSheet.create({
    container:{
        flex:1,
        justifyContent: 'center',
        padding: 25,
        backgroundColor: "#efefef"
    },
    title: {
        fontSize: 35,
        textAlign: 'center',
        marginBottom: 35,
        color:"#3a4a5a"
    },
    card: {
        backgroundColor:"#fff",
        padding: 20,
        marginBottom: 20,
        flexDirection: 'row',
        justifyContent: 'space-between',
    },
    axis:{
        fontSize: 24,
        fontWeight: 'bold',
    },
    value: {
        fontSize: 24,
        fontWeight: 'bold',
    },

});
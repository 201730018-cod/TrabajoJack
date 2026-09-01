import React, {useState} from "react";
import { View, Text, TextInput, Button} from 'react-native';

export default function ImcScreen(){
    const [peso, setPeso]=useState('');
    const [altura, setAltura]=useState('');
    const [resultado, setResultado]=useState(null);

    const calcularIMC = () =>{
        if(!peso||!altura) return;
        const imc= (parseFloat(peso)/(parseFloat(altura)*parseFloat(altura))).toFixed(2);
        setResultado(imc)
    };
    return (
        <View style={{flex:1, justifyContent:'center', padding:20 }}>
            <Text>Peso (kg):</Text>
            <TextInput
                keyboardType="numeric"
                value={peso}
                onChangeText={setPeso}
                style={{borderWidth:1, marginBottom:10, padding: 5}}
            />
            <Text>Altura (metros):</Text>
            <TextInput
                keyboardType="numeric"
                value={altura}
                onChangeText={setAltura}
                style={{borderWidth:1, marginBottom:10, padding: 5}}
            />
            <Button
                title="Calcular IMC"
                onPress={calcularIMC}
            />
            {resultado && <Text style={{marginTop:20, fontSize:18}}> Tu IMC es: {resultado}</Text>}
        </View>
    );
}
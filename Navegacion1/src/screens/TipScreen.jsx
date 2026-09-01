import React, {useState} from "react";
import { View, Text, TextInput, Button} from 'react-native';

export default function TipScreen(){
    const [cuenta, setCuenta] = useState('');
    const [porcentaje, setPorcentaje] = useState("");
    const [resultado, setResultado] = useState(null);

    const calcularPropina = () =>{
        if(!cuenta || !porcentaje) return;
        const propina = (parseFloat(cuenta)*(parseFloat(porcentaje)/100)).toFixed(2);
        const total = (parseFloat(cuenta)+parseFloat(propina)).toFixed(2);
        setResultado({propina, total});
    };

    return(
        <View style={{flex:1, justifyContent:'center', padding:20}}>
            <Text>Monto de la cuenta:</Text>
            <TextInput
                keyboardType="numeric"
                value={cuenta}
                onChangeText={setCuenta}
                style={{borderWidth:1, marginBottom: 10, padding: 5}}
            />
            <Text>Monto de la propina:</Text>
            <TextInput
                keyboardType="numeric"
                value={porcentaje}
                onChangeText={setPorcentaje}
                style={{borderWidth:1, marginBottom: 10, padding: 5}}
            />
            <Button
                title="Calcular propina"
                onPress={calcularPropina}
            />
            {resultado && (<Text style={{marginTop:20, fontSize: 18}}>
                    Propina: ${resultado.propina} | Total: &{resultado.total}
                </Text>)}
        </View>
    );
};
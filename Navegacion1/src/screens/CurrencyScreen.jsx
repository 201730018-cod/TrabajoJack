import React, {useState} from "react";
import { View, Text, TextInput, Button} from 'react-native';

export default function CurrencyScreen(){
    const [usd, setUsd] = useState('');
    const [mxn, setMxn] = useState(null);

    const convertir =() =>{
        if (!usd) return; 
        setMxn((parseFloat(usd)*18).toFixed(2));
    };
    return (
        <View style={{flex: 1, justifyContent: 'center', padding:20}}>
            <Text> Cantidad en dolaros </Text>
            <TextInput
                keyboardType="numeric"
                valuew={usd}
                onChangeText={setUsd}
                style={{borderWidth: 1, marginBottom: 10, padding: 5}}
            />
            <Button
                title="Convertir a Mxn"
                onPress={convertir}
            />
            {mxn && <Text>{usd} USD ={mxn} MXN</Text>}


        </View>
    );
}

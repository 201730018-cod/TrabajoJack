import {StyleSheet, Text, View, TextInput, Button, ScrollView, Dimensions} from "react-native";
import Reac, {useState} from "react";

export default function RepText(){
    const [text, setText]=useState()
    const [enviar, setEnviar]=useState()


    return( 
        <View style={misEstilos.container}>
            <View style={{height:'50%'}}>
            <ScrollView style={misEstilos.scroll}>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>Hola Mundo</Text>
            <Text>Hola Mundo</Text>
            <Text>Hola Mundo</Text>
            <Text>Hola Mundo</Text>
            <Text>Hola Mundo</Text>
            <Text>Hola Mundo</Text>
            <Text>Hola Mundo</Text>
            <Text>Hola Mundo</Text>
            <Text>Hola Mundo</Text>
            <Text>Hola Mundo</Text>
            <Text>Hola Mundo</Text>
            <Text>Hola Mundo</Text>
            <Text>Hola Mundo</Text>
            <Text>Hola Mundo</Text>
            <Text>Hola Mundo</Text>
            <Text>Hola Mundo</Text>
            <Text>Hola Mundo</Text>
            </View>
            <View style={{height:'50%'}}>
            <TextInput
                style={misEstilos.input}
                placeholder="Escribe aqui..."
                defaultValue={text}
                onChangeText={t=>setText(t)}

            />
            </ScrollView>
            <Button
                title="Enviar"
                onPress={()=>{
                    setEnviar(text)
                    alert('Texto enviado con exito')
                }}
            />
            
            </View>
        </View>
    );
}

const misEstilos = StyleSheet.create({
    container:{
        flex:1,
        backgroundColor:"#ffff",
        alignItems: 'center',
        justifyContent: 'center',


    },
    input: {
        width: '100%',
        height: 40,
        backgroundColor: '#eee',
    },
    scroll: {
        width: Dimensions.get('window').width,
        height: Dimensions.get('window').height,
    },
});
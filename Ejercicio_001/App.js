import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import  Cat  from './componentes/Cat';
import Mensaje from './componentes/Mensaje';

export default function App() {
  return (
    <View style={styles.container}>
      <Text>Open up App.js to start working on your app!</Text>
      <Text style={styles.texto_rojo}>Esto es otro componente de texto</Text>
      <StatusBar style="auto" />
      <Cat></Cat>
      <Mensaje msg="Mi mensaje como propiedad" num="3000" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  texto_rojo:{
    color:'red',
    backgroundColor: 'gray'
  },
});

import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import DemoImagen from './componentes/DemoImagen';

export default function App() {
  return (
    <View style={styles.container}>
      <DemoImagen></DemoImagen>
      
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#2bf110',
    
  },
  panel1:{
    flex: 1,
    backgroundColor: '#d5fa06'
  },
  panel2:{
    flex: 1,
    backgroundColor: 'rgba(7, 128, 248, 0.93)'
  },
  panel3:{
    flex: 1,
    backgroundColor: 'rgba(241, 9, 9, 0.73)'
  },
});

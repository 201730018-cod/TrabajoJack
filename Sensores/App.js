import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import GyroscopeSensor from './componentes/GyroscopeSensor.js'
import PedometerSensor from './componentes/PedometerSensor.js'

export default function App() {
  return (
    
      <PedometerSensor/>
    
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});

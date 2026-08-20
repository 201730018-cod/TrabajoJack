import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import Banner from './components/Banner';
import RepText from './components/ReplicaTexto';

export default function App() {
  return (
    
    <View style={styles.container}>
      <RepText/>
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
});

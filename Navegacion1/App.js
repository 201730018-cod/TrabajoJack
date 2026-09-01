import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import CurrencyScreen from './src/screens/CurrencyScreen';
import ImcScreen from './src/screens/IMCScreen';
import TipScreen from './src/screens/TipScreen'
import HomeScreen from './src/screens/HomeScreen';
import {NavigationContainer} from'@react-navigation/native';
import {createNativeStackNavigator} from'@react-navigation/native-stack';
import * as React from 'react';

const Stack = createNativeStackNavigator();



export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName='Home'> 
        <Stack.Screen name='Home' component={HomeScreen} options={{title:'Menu principal'}}/>
        <Stack.Screen name='IMC' component={ImcScreen} options={{title:'Calculadora IMC'}}/>
        <Stack.Screen name='divisas' component={CurrencyScreen} options={{title:'Calculadora divisas'}}/>
        <Stack.Screen name='tips' component={TipScreen} options={{title:'Calculadora de propina'}}/>
      </Stack.Navigator>
    </NavigationContainer>
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

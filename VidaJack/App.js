import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import Dados from "./componentes/Dados";
import Divisas from "./componentes/Divisas";
import Propinas from "./componentes/Propinas";
import ListaSuper from "./componentes/ListaSuper";
import { NavigationContainer } from "@react-navigation/native";
import NavDrawer from "./navegacion/NavDrawer";

export default function App() {
    return (
        <NavigationContainer>
            <NavDrawer />
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

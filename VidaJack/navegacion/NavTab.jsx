import { createMaterialTopTabNavigator } from "@react-navigation/material-top-tabs";
import PantallaDados from "../pantallas/PantallaDados";
import PantallaDivisas from "../pantallas/PantallaDivisas";
import PantallaPropinas from "../pantallas/PantallaPropinas";
import PantallaListaSuper from "../pantallas/PantallaListaSuper";
import PantallaIMC from "../pantallas/PantallaIMC";
import PantallaTicTacToe from "../pantallas/PantallaTicTacToe";
import PantallaMemorama from "../pantallas/PantallaMemorama";

const Tab = createMaterialTopTabNavigator();

const NavTab = () => {
    return (
        <Tab.Navigator
            screenOptions={{
                headerShown: false,
                tabBarActiveTintColor: "#4A90E2",
                tabBarInactiveTintColor: "#999",
                tabBarLabelStyle: { fontSize: 10 },
                tabBarScrollEnabled: true, // permite deslizar las pestañas horizontalmente
            }}
        >
            <Tab.Screen name="Dados" component={PantallaDados} options={{ tabBarLabel: "🎲 Dados" }} />
            <Tab.Screen name="Divisas" component={PantallaDivisas} options={{ tabBarLabel: "💱 Divisas" }} />
            <Tab.Screen name="Propinas" component={PantallaPropinas} options={{ tabBarLabel: "💵 Propinas" }} />
            <Tab.Screen name="Lista" component={PantallaListaSuper} options={{ tabBarLabel: "🛒 Lista" }} />
            <Tab.Screen name="IMC" component={PantallaIMC} options={{ tabBarLabel: "⚖️ IMC" }} />
            <Tab.Screen name="TicTacToe" component={PantallaTicTacToe} options={{ tabBarLabel: "⭕ Gato" }} />
            <Tab.Screen name="Memorama" component={PantallaMemorama} options={{ tabBarLabel: "🧠 Memoria" }} />
        </Tab.Navigator>
    );
};

export default NavTab;
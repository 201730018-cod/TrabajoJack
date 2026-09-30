import { createDrawerNavigator } from "@react-navigation/drawer";
import PantallaInicio from "../pantallas/PantallaInicio";
import NavTab from "./NavTab";

const Drawer = createDrawerNavigator();

const NavDrawer = () => {
    return (
        <Drawer.Navigator
            screenOptions={{
                headerStyle: { backgroundColor: "#4A90E2" },
                headerTintColor: "white",
                drawerActiveTintColor: "#4A90E2",
            }}
        >
            <Drawer.Screen
                name="Inicio"
                component={PantallaInicio}
                options={{ drawerLabel: "🏠 Inicio" }}
            />
            <Drawer.Screen
                name="Herramientas"
                component={NavTab}
                options={{ drawerLabel: "🧰 Herramientas" }}
            />
        </Drawer.Navigator>
    );
};

export default NavDrawer;
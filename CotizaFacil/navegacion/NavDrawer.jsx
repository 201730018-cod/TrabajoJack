import { createDrawerNavigator } from "@react-navigation/drawer";
import PantallaInicio from "../pantallas/PantallaInicio";
import PantallaNuevaCotizacion from "../pantallas/PantallaNuevaCotizacion";
import PantallaHistorial from "../pantallas/PantallaHistorial";
import PantallaConfiguracion from "../pantallas/PantallaConfiguracion";

const Drawer = createDrawerNavigator();

const NavDrawer = ({ config, setConfig, cotizaciones, agregarCotizacion, eliminarCotizacion }) => {
    return (
        <Drawer.Navigator
            screenOptions={{
                headerStyle: { backgroundColor: "#2c3e50" },
                headerTintColor: "white",
                drawerActiveTintColor: "#2c3e50",
            }}
        >
            <Drawer.Screen name="Inicio" options={{ drawerLabel: "🏠 Inicio" }}>
                {() => <PantallaInicio config={config} cotizaciones={cotizaciones} />}
            </Drawer.Screen>

            <Drawer.Screen name="Nueva Cotización" options={{ drawerLabel: "📝 Nueva Cotización" }}>
                {() => (
                    <PantallaNuevaCotizacion
                        config={config}
                        agregarCotizacion={agregarCotizacion}
                    />
                )}
            </Drawer.Screen>

            <Drawer.Screen name="Historial" options={{ drawerLabel: "📋 Historial" }}>
                {() => (
                    <PantallaHistorial
                        cotizaciones={cotizaciones}
                        eliminarCotizacion={eliminarCotizacion}
                    />
                )}
            </Drawer.Screen>

            <Drawer.Screen name="Configuración" options={{ drawerLabel: "⚙️ Configuración" }}>
                {() => <PantallaConfiguracion config={config} setConfig={setConfig} />}
            </Drawer.Screen>
        </Drawer.Navigator>
    );
};

export default NavDrawer;
import { useState, useEffect } from "react";
import * as SplashScreen from "expo-splash-screen";
import { NavigationContainer } from "@react-navigation/native";
import NavDrawer from "./navegacion/NavDrawer";
import SplashPersonalizado from "./pantallas/SplashPersonalizado";

SplashScreen.preventAutoHideAsync(); // evita que el splash nativo se oculte solo

export default function App() {
    const [appListo, setAppListo] = useState(false);

    const [config, setConfig] = useState({
        nombreNegocio: "Mi Negocio Freelance",
        tarifaDefault: "250",
        ivaDefault: "16",
    });

    const [cotizaciones, setCotizaciones] = useState([]);

    useEffect(() => {
        // En cuanto el JS carga, oculta el splash nativo (imagen estática de Expo)
        // para que nuestro SplashPersonalizado tome el control de inmediato
        SplashScreen.hideAsync();
    }, []);

    const agregarCotizacion = (nuevaCotizacion) => {
        const conId = {
            ...nuevaCotizacion,
            id: Date.now().toString(),
            fecha: new Date().toLocaleDateString(),
        };
        setCotizaciones([conId, ...cotizaciones]);
    };

    const eliminarCotizacion = (id) => {
        setCotizaciones(cotizaciones.filter((c) => c.id !== id));
    };

    if (!appListo) {
        return <SplashPersonalizado onTerminado={() => setAppListo(true)} />;
    }

    return (
        <NavigationContainer>
            <NavDrawer
                config={config}
                setConfig={setConfig}
                cotizaciones={cotizaciones}
                agregarCotizacion={agregarCotizacion}
                eliminarCotizacion={eliminarCotizacion}
            />
        </NavigationContainer>
    );
}
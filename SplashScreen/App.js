import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import { useEffect, useState } from 'react';

function SplashScreen () {
  return(
    <View style={styles.splash}>
      <Text style={styles.logo}>
      🔥
      </Text>
      <Text style={styles.title}>
        Mi Aplicacion
      </Text>
      <Text>
        Cargando...
      </Text>
    </View>
  );
}

function HomeScreen() {
 return(
  <View style={styles.home}>
    <Text style={styles.homeText}>
      Bienvendio
    </Text>
  </View>
 ) ; 
}

export default function App() {
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setTimeout(() => {
      setLoading(false);
    }, 5000);
  }, []);

  if (loading) {
    return <SplashScreen />;
  }

  return <HomeScreen />;
}

const styles = StyleSheet.create({
  splash: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  home: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  logo: {
    fontSize: 80,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
  },
  homeText: {
    fontSize: 24,
    fontWeight: 'bold',
  },
});
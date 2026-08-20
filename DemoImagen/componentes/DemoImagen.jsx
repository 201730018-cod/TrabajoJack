import {View, ImageBackground, StyleSheet, Dimensions, Image, Text} from "react-native";

const DemoImagen = () => {
    return(
        <View style={styles.container}>
            <ImageBackground
                style={styles.fondo}
                source={require('../assets/fotopruebas.jpg')}            
            >
                <View 
                style={styles.container}
                
                >
                <Text style={styles.texto}>Gatitos</Text>
                <Image
                    
                    
                    style={styles.foto}
                    source={{uri:'https://http.cat/206'}}
                />
                </View>

                
            </ImageBackground>
        </View>
    );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'rgb(0,0,0,0)',
    alignItems: 'center',
    justifyContent: 'center',
    
  },
  fondo:{
    width: Dimensions.get("window").width,
    height: Dimensions.get("window").height,
  },
  foto:{
    width: 200,
    height: 200,
    borderRadius: 16,
    borderWidth: 10,
    borderColor: '#bd5',
    shadowColor: '#444',
    shadowOffset: {width: 0, height: 10},
    shadowRadius: 10,
    elevation: 8,

  },
  texto:{
    width: Dimensions.get("window").width,
    backgroundColor: 'rgb(0,0,0,0.5)',
    fontSize: 100,
    textAlign: 'center',
    color: '#ffff'
    
  }
  
});

export default DemoImagen;


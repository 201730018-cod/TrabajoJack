import React from "react";
import { useState } from "react";
import { View, StyleSheet, Button, SafeAreaView} from 'react-native';
import CustomModal from "./componentes/CustomModal";
import FlatListBasics from "./componentes/DemoFlatList";
import SectionListBasics from "./componentes/DemoSectionList";

export default function App(){
    const [modalVisible, SetModalVisible]=useState(false);
    const objetoContenido={
        valor:"Juan Perez",
    }

    return(
        <SafeAreaView style={styles.container}>
            <View style={styles.content}>
                <Button
                    title="Ver mensaje"
                    onPress={()=>SetModalVisible(true)}
                />
                <CustomModal
                    visible={modalVisible}
                        onClose={()=>SetModalVisible(false)}
                        contenido={objetoContenido}
                    
                />
                <FlatListBasics/>
        <SectionListBasics/>
            </View>
            
        </SafeAreaView>
        
    );
}

const styles= StyleSheet.create({
    container:{
        flex:1,
        backgroundColor: "#ddc725"

    },
    content: {
        flex: 1,
        justifyContent:"center",
        alignItems:"center"
    }
});
import React from "react";
import { Modal, View, Text, Button, StyleSheet } from "react-native";

const CustomModal = ({visible, onClose, contenido}) => {
    return(
        <Modal
            animationType="slide"
            transparent={true}
            visible={visible}
            onRequestClose={onClose}
        >

            <View>
                <View>
                    <Text>
                        Hola, te has inscrito al curso: {contenido ? contenido.valor: "Ninguno"} 
                    </Text>
                    <Button
                        title="Cerrar"
                        onPress={onClose}
                    />
                </View>
            </View>

        </Modal>
    );
};

const styles = StyleSheet.create({
    centeredView:{
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: 'rgba(0,0,0,0.5)'

    },
    modalView:{
        margin: 20,
        backgroundColor: 'white',
        borderRadius: 16,
        padding: 35,
        alignItems: 'center',

    },
    modalTexto:{
        marginBottom: 20,
        textAlign: 'center',
        fontSize: 20,
        color: '#333',
    },
});

export default CustomModal
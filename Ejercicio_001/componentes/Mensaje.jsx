import { View, Text, StyleSheet } from "react-native";

export default function Mensaje(props) {

    const double=n => n*2;
    return(
        <View>
            <Text style={styles.texto_rojos}>{props.msg}</Text>
            <Text>{props.num}</Text>
        </View>
    );
}

const styles = StyleSheet.create({
     texto_rojos:{
    color:'red',
    backgroundColor: 'gray'
  },
})
import { View, StyleSheet, Text } from 'react-native';

export default function Banner({ children, titulo, backgroundColor = '#4A90E2' }) {
  return (
    <View style={styles.texto}>
        <Text>{titulo}</Text>
      {children}
    </View>
  );
}

const styles = StyleSheet.create({
  texto: {
    color:"red"
  },
});
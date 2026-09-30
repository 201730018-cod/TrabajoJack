import { useEffect, useRef } from "react";
import { Animated, View, Text, Image } from "react-native";

export default function App2(){
    
    const scale= useRef(
        new Animated.Value(0)
    ).current;
    
    const opacity = useRef(
        new Animated.Value(0)
    ).current;
    

    const position = useRef(
        new Animated.Value(-200)
    ).current;

    useEffect(()=>{

    Animated.parallel([

        Animated.timing(
            scale,
            {
                toValue: 1,
                duration: 5000,
                useNativeDriver: true
            }
        ),

        Animated.timing(
            opacity,
            {
                toValue:1,
                duration: 5000,
                useNativeDriver: true
            }
        ),

        Animated.timing(
            position,
            {
                toValue: 0,
                duration: 5000,
                useNativeDriver: true
            }
        )

    ]).start()  
        
    

        


    },[]);

    return(
        <View
            style={{flex: 1, justifyContent: 'center', alignItems:'center'}}
        >
            <Animated.Text
                style={{
                    fontSize:80,
                    transform:[
                        {translateY:position},
                        {scale:scale    }
                    ],
                    opacity:opacity,
                }}
            >
                Hola 🚬
                <Image
                    source={require('./assets/icon.png')}
                />
            </Animated.Text>
        </View>
    );
}
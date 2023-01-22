import {type FC, Fragment, useRef, useState} from 'react'
import {Animated, Pressable, StyleSheet, Text, View} from 'react-native'

export interface TodoListItemProps {
    text: string
}

export const TodoListItem: FC<TodoListItemProps> = ({text}) => {
    const animatedDividerScale = useRef(new Animated.Value(styles.divider.width)).current
    const [selected, setSelected] = useState(false)

    const focusDividerAnimation = Animated.timing(animatedDividerScale, {
        toValue: 0,
        duration: 200,
        useNativeDriver: false,
    })

    const blurDividerAnimation = Animated.timing(animatedDividerScale, {
        toValue: styles.divider.width,
        duration: 200,
        useNativeDriver: false,
    })

    const onPress = () => {
        console.log('todo pressed')
    }

    const onPressIn = () => {
        console.log('todo pressed in')
        focusDividerAnimation.reset()
        focusDividerAnimation.start()
    }

    const onPressOut = () => {
        console.log('todo pressed out')
        blurDividerAnimation.reset()
        blurDividerAnimation.start()
        if (selected) {
            setSelected(false)
        }
    }

    const onLongPress = () => {
        console.log('todo long pressed')
        setSelected(true)
    }

    const animatedDividerStyle = {
        width: animatedDividerScale,
    }

    return (
        <Fragment>
            <Pressable
                onPress={onPress}
                onPressIn={onPressIn}
                onPressOut={onPressOut}
                onLongPress={onLongPress}>
                <View style={styles.container}>
                    <Text style={styles.text}>{text}</Text>
                </View>
            </Pressable>
            <Animated.View style={[styles.divider, animatedDividerStyle]}/>
        </Fragment>
    )
}

const styles = StyleSheet.create({
    container: {
        paddingVertical: 15,
    },
    text: {
        fontSize: 20,
        color: '#222',
    },
    divider: {
        width: 10,
        height: 1,
        backgroundColor: '#222',
    },
})

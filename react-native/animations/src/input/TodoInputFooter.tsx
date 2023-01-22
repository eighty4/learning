import {type FC, type ReactNode, useEffect, useRef} from 'react'
import {Animated, Platform, StyleSheet, View} from 'react-native'

import {onKeyboardHide, onKeyboardShow} from '../responsiveKeyboard'

export interface TodoInputFooterProps {
    children: ReactNode
}

export const TodoInputFooter: FC<TodoInputFooterProps> = ({children}) => {
    if (Platform.OS === 'ios') {
        return <TodoInputFooterIOS children={children}/>
    } else {
        return (
            <View style={styles.container}>
                {children}
            </View>
        )
    }
}

const TodoInputFooterIOS: FC<TodoInputFooterProps> = ({children}) => {
    const animatedTranslateY = useRef(new Animated.Value(0)).current

    useEffect(() => {
        if (Platform.OS !== 'ios') {
            return
        }
        const showSubscription = onKeyboardShow(keyboardHeight => {
            Animated.timing(animatedTranslateY, {
                toValue: -1 * keyboardHeight,
                duration: 250,
                useNativeDriver: true,
            }).start()
        })
        const hideSubscription = onKeyboardHide(() => {
            Animated.timing(animatedTranslateY, {
                toValue: 0,
                duration: 250,
                useNativeDriver: true,
            }).start()
        })
        return () => {
            showSubscription.remove()
            hideSubscription.remove()
        }
    }, [])

    const translateY = {
        transform: [{
            translateY: animatedTranslateY,
        }],
    }

    return <View style={styles.container}>
        <Animated.View style={translateY}>
            {children}
        </Animated.View>
    </View>
}

const styles = StyleSheet.create({
    container: {
        position: 'absolute',
        bottom: 0,
        left: 0,
        right: 0,
    },
})

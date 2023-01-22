import {type FC, useEffect, useRef, useState} from 'react'
import {Animated, Platform, Pressable, StyleSheet, View} from 'react-native'
import Svg, {Path} from 'react-native-svg'

import {useEditingActive} from './state/selectors'
import {useAppDimensions} from './appDimensions'
import {type ReceivesPanningUpdatesProps} from './PanningUpdate'
import {onKeyboardHide, onKeyboardShow} from './responsiveKeyboard'

const IOS_OFFSET = -78 // todo is this variable on hardware/ios version?

const SVG_HEIGHT = 70

const SVG_PATH = 'M97.56,.54C87.61-.99,.46,43.27,.5,99.5c.04,56.21,87.21,100.48,97.06,98.96,8.89-1.37-49.34-39.36-50-97.82C46.88,40.97,106.65,1.94,97.56,.54Z'

const calcPositionFromTop = (height: number) => (height - SVG_HEIGHT) / 2

const calcKeyboardClosedTranslateY = Platform.OS !== 'ios' ? () => 0 : () => IOS_OFFSET / 2

function calcKeyboardOpenTranslateY(listHeight: number, keyboardHeight: number): number {
    let contentHeight = listHeight - keyboardHeight - SVG_HEIGHT
    if (Platform.OS === 'ios') {
        contentHeight -= IOS_OFFSET
    }
    return -1 * (contentHeight / 2)
}

interface DayPanSvgProps extends ReceivesPanningUpdatesProps {
    onPress: () => void
}

export const Boomerang: FC<DayPanSvgProps> = ({onPress, panning}) => {
    const {listHeight, windowHeight} = useAppDimensions()
    const editing = useEditingActive()
    const [keyboardHeight, setKeyboardHeight] = useState(0)
    const animatedRotate = useRef(new Animated.Value(0)).current
    const animatedTranslateY = useRef(new Animated.Value(calcKeyboardClosedTranslateY())).current

    useEffect(() => {
        const showSubscription = onKeyboardShow(setKeyboardHeight)
        const hideSubscription = onKeyboardHide(() => setKeyboardHeight(0))
        return () => {
            showSubscription.remove()
            hideSubscription.remove()
        }
    }, [])

    useEffect(() => {
        const subscription = panning.subscribe(update => {
            if (update.panCommitted) {
                const toValue = update.toPane === 0 ? 0 : 1
                Animated.timing(animatedRotate, {
                    toValue,
                    duration: 250,
                    useNativeDriver: true,
                }).start()
            }
        })
        return () => subscription.unsubscribe()
    }, [])

    useEffect(() => {
        const toValue = keyboardHeight === 0
            ? calcKeyboardClosedTranslateY()
            : calcKeyboardOpenTranslateY(listHeight, keyboardHeight)
        Animated.timing(animatedTranslateY, {
            toValue,
            duration: 250,
            useNativeDriver: true,
        }).start()
    }, [listHeight, keyboardHeight])

    const rotate = {
        transform: [{
            rotate: animatedRotate.interpolate({
                inputRange: [0, 1],
                outputRange: ['0deg', '180deg'],
            }),
        }],
    }
    const translateY = {transform: [{translateY: animatedTranslateY}]}
    const position = {top: calcPositionFromTop(windowHeight)}
    const fill = editing ? '#fff' : '#000'

    return (
        <Pressable onPress={onPress}>
            <View style={styles.container}>
                <Animated.View style={[styles.positioned, translateY, position]}>
                    <Animated.View style={rotate}>
                        <Svg viewBox="0 0 99 199">
                            <Path d={SVG_PATH} fill={fill}/>
                        </Svg>
                    </Animated.View>
                </Animated.View>
            </View>
        </Pressable>
    )
}

const styles = StyleSheet.create({
    container: {
        justifyContent: 'center',
        alignItems: 'center',
        width: '100%',
        height: '100%',
    },
    positioned: {
        position: 'absolute',
        width: '35%',
        height: SVG_HEIGHT,
    },
})

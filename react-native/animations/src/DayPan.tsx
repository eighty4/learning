import {type FC, type ReactElement, useRef} from 'react'
import {Animated, Keyboard, PanResponder, StyleSheet, View} from 'react-native'
import {useDispatch} from 'react-redux'

import {type PaneIndex, setPaneIndex} from './state/panningState'
import {usePaneIndex} from './state/selectors'
import {useAppDimensions} from './appDimensions'
import {Boomerang} from './Boomerang'
import {DayViewProps} from './DayView'
import {ProducesPanningUpdatesProps} from './PanningUpdate'

function otherPaneIndex(di: PaneIndex): PaneIndex {
    return Math.abs(di - 1) as PaneIndex
}

interface DayPanProps extends ProducesPanningUpdatesProps {
    today: ReactElement<DayViewProps>
    tomorrow: ReactElement<DayViewProps>
}

export const DayPan: FC<DayPanProps> = ({panningDispatch, today, tomorrow}) => {
    const {windowWidth, gutterWidth, paneWidth} = useAppDimensions()
    const animatedX = useRef(new Animated.Value(0)).current
    const dispatch = useDispatch()
    const paneIndex = usePaneIndex()

    const swipeGestureThreshold = gutterWidth * 1.25

    const animatedViewStyles = [styles.container, {
        width: windowWidth + paneWidth,
        transform: [{
            translateX: animatedX,
        }],
    }]

    /**
     *
     * GestureResponderEvent
     *     e.nativeEvent.pageX - current coordinate of gesture event (useful before pan responder set)
     *
     *  PanResponderGestureState
     *      g.x0    - starting coordinate position of gesture (not available until after pan responder set)
     *      g.moveX - current coordinate position of gesture
     *      g.dx    - distance of gesture movement
     *
     */
    const panResponder = PanResponder.create({
        onMoveShouldSetPanResponder: (e, g) => {
            switch (paneIndex) {
                case 0:
                    return g.dx < 0 && e.nativeEvent.pageX > gutterWidth
                case 1:
                    return g.dx > 0 && e.nativeEvent.pageX < paneWidth
            }
            throw new Error('wtf')
        },
        onPanResponderMove: (e, g) => {
            console.log(paneIndex === 0 ? g.dx : g.dx - paneWidth)
            animatedX.setValue(paneIndex === 0 ? g.dx : g.dx - paneWidth)
        },
        onPanResponderRelease: (e, g) => {
            const continuousSwipeDirection = paneIndex === 0 ? g.dx < 0 : g.dx > 0
            const gestureDistanceX = Math.abs(g.dx)
            if (continuousSwipeDirection && gestureDistanceX > swipeGestureThreshold) {
                const towards = otherPaneIndex(paneIndex)
                animateDayTransition(towards)
                panningDispatch.panningCommit(towards, gestureDistanceX / windowWidth)
            } else {
                animateDayTransition(paneIndex)
            }
        },
        onPanResponderTerminate: () => {
            animateDayTransition(paneIndex)
        },
    })

    const animateDayTransition = (toPaneIndex: PaneIndex) => {
        Keyboard.dismiss()
        const toValue = toPaneIndex ? (paneWidth * -1) : 0
        const isPaneTransition = toPaneIndex !== paneIndex
        Animated.timing(animatedX, {
            toValue,
            duration: 250,
            useNativeDriver: true,
        }).start(isPaneTransition ? () => dispatch(setPaneIndex(toPaneIndex)) : undefined)
        if (isPaneTransition) {
            panningDispatch.panningCommit(toPaneIndex, -1)
        }
    }

    return <Animated.View style={animatedViewStyles} {...panResponder.panHandlers}>
        <View style={{width: paneWidth}}>
            {today}
        </View>
        <View style={{width: gutterWidth}}>
            <Boomerang panning={panningDispatch.observable}
                       onPress={() => animateDayTransition(otherPaneIndex(paneIndex))}/>
        </View>
        <View style={{width: paneWidth}}>
            {tomorrow}
        </View>
    </Animated.View>
}

const styles = StyleSheet.create({
    container: {
        flexDirection: 'row',
    },
})

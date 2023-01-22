import {type FC, type ReactNode, useEffect, useRef} from 'react'
import {Animated, StyleSheet} from 'react-native'

import {FOOTER_MARGIN, useAppDimensions} from '../appDimensions'
import {ReceivesPanningUpdatesProps} from '../PanningUpdate'

export interface TodoInputPanePositionedProps extends ReceivesPanningUpdatesProps {
    children: ReactNode
}

export const TodoInputPanePositioned: FC<TodoInputPanePositionedProps> = ({panning, children}) => {
    const {windowWidth, gutterWidth} = useAppDimensions()
    const animatedX = useRef(new Animated.Value(0)).current

    useEffect(() => {
        const subscription = panning.subscribe(update => {
            if (update.panCommitted) {
                Animated.timing(animatedX, {
                    toValue: update.toPane === 0 ? 0 : gutterWidth - FOOTER_MARGIN,
                    duration: 200,
                    useNativeDriver: true,
                }).start()
            }
        })
        return () => subscription.unsubscribe()
    }, [])

    const containerWidth = windowWidth - gutterWidth - FOOTER_MARGIN

    const dynamicStyles = {
        width: containerWidth,
        transform: [{
            translateX: animatedX,
        }],
    }

    return (
        <Animated.View style={[styles.container, dynamicStyles]}>
            {children}
        </Animated.View>
    )
}

const styles = StyleSheet.create({
    container: {
        marginBottom: FOOTER_MARGIN,
        marginLeft: FOOTER_MARGIN,
    },
})

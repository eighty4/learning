import {type FC} from 'react'
import {Pressable, StyleSheet, View} from 'react-native'

interface TodoInputCloseMask {
    active: boolean
    onMaskPress: () => void
}

export const TodoInputCloseMask: FC<TodoInputCloseMask> = ({active, onMaskPress}) => {

    if (!active) {
        return null
    }

    return <View style={[styles.container, styles.mask]}>
        <Pressable style={styles.mask} onPress={onMaskPress}>

        </Pressable>
    </View>
}

const styles = StyleSheet.create({
    container: {
        position: 'absolute',
        top: 0,
        left: 0,
    },
    mask: {
        height: '100%',
        width: '100%',
    },
})

import {type FC} from 'react'
import {Pressable, StyleSheet, Text, View} from 'react-native'

import {INPUT_HEIGHT} from '../appDimensions'

export interface TodoInputButtonProps {
    onPress: () => void
}

export const TodoInputButton: FC<TodoInputButtonProps> = ({onPress}) => {

    return (
        <View style={styles.container}>
            <Pressable onPress={onPress}>
                <View style={styles.pressable}>
                    <Text style={styles.label}>Add a todo</Text>
                </View>
            </Pressable>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        height: INPUT_HEIGHT,
        backgroundColor: '#000',
        paddingLeft: 20,
    },
    label: {
        fontSize: 20,
        color: '#fff',
    },
    pressable: {
        height: '100%',
        justifyContent: 'center',
    },
})

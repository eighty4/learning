import {type FC, Fragment, useEffect} from 'react'
import {Platform, StyleSheet, Text, TextInput, View} from 'react-native'
import type {NativeSyntheticEvent, TextInputSubmitEditingEventData} from 'react-native'
import {useDispatch} from 'react-redux'

import {addTodo} from '../state/todosState'
import {usePaneIndex} from '../state/selectors'
import {INPUT_HEIGHT} from '../appDimensions'
import {onKeyboardHide} from '../responsiveKeyboard'

interface TodoInputTextField {
    onBlur: () => void
}

export const TodoInputTextField: FC<TodoInputTextField> = ({onBlur: closeEditingMode}) => {
    const dispatch = useDispatch()
    const paneIndex = usePaneIndex()

    // Android software keyboard close button does not blur text input
    useEffect(() => {
        if (Platform.OS === 'android') {
            const subscription = onKeyboardHide(closeEditingMode)
            return () => subscription.remove()
        }
    }, [])

    const onBlur = () => {
        console.log('on blur')
        closeEditingMode()
    }

    const onSubmitEditing = (e: NativeSyntheticEvent<TextInputSubmitEditingEventData>) => {
        console.log('on submit editing')
        const todoText = e.nativeEvent.text.trim()
        if (todoText.length) {
            dispatch(addTodo({paneIndex, todoText}))
        }
    }

    return (
        <Fragment>
            <Text style={styles.label}>
                Adding a todo
            </Text>
            <View style={styles.container}>
                <TextInput
                    style={styles.input}
                    autoFocus={true}
                    onFocus={() => console.log('on focus')}
                    onBlur={onBlur}
                    onEndEditing={() => console.log('on end editing')}
                    onSubmitEditing={onSubmitEditing}
                    accessibilityLabel="Add a todo"
                />
            </View>
        </Fragment>
    )
}

const styles = StyleSheet.create({
    container: {
        backgroundColor: '#fff',
        paddingHorizontal: 20,
        paddingVertical: 10,
        height: INPUT_HEIGHT,
        justifyContent: 'center',
    },
    input: {
        color: '#000',
        backgroundColor: '#fff',
        fontSize: 20,
    },
    label: {
        marginLeft: 20,
        marginBottom: 5,
        color: '#fff',
        fontSize: 16,
    },
})

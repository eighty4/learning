import {type EmitterSubscription, Keyboard, Platform} from 'react-native'

const SHOW_EVENT = Platform.OS === 'ios' ? 'keyboardWillShow' : 'keyboardDidShow'

const HIDE_EVENT = Platform.OS === 'ios' ? 'keyboardWillHide' : 'keyboardDidHide'

export function onKeyboardShow(fn: (keyboardHeight: number) => void): EmitterSubscription {
    return Keyboard.addListener(SHOW_EVENT, (e) => {
        fn(e.endCoordinates.height)
    })
}

export function onKeyboardHide(fn: () => void): EmitterSubscription {
    return Keyboard.addListener(HIDE_EVENT, () => fn())
}

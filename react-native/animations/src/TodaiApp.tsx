import {type FC, useRef} from 'react'
import {Platform, SafeAreaView, StatusBar, StyleSheet, UIManager, useWindowDimensions, View} from 'react-native'
import {Provider} from 'react-redux'

import {TodoInput} from './input/TodoInput'
import {useEditingActive} from './state/selectors'
import {store} from './state/store'
import {DayPan} from './DayPan'
import {DayView} from './DayView'
import {PanningDispatch} from './PanningUpdate'
import {AppDimensionContext, calculateAppDimensions} from './appDimensions'

export const createTodaiApp = () => {
    if (Platform.OS === "android" && UIManager.setLayoutAnimationEnabledExperimental) {
        UIManager.setLayoutAnimationEnabledExperimental(true)
    }
    return (
        <Provider store={store}>
            <TodaiApp/>
        </Provider>
    )
}

export const TodaiApp: FC = () => {
    const appDimensions = calculateAppDimensions(useWindowDimensions())
    const editing = useEditingActive()
    const panningDispatch = useRef(new PanningDispatch()).current
    const backgroundColor = editing ? '#000' : '#fff'
    const barStyle = editing ? 'light-content' : 'dark-content'

    return (
        <SafeAreaView style={{backgroundColor}}>
            <StatusBar barStyle={barStyle} backgroundColor={backgroundColor}/>
            <AppDimensionContext.Provider value={appDimensions}>
                <View style={styles.container}>
                    <DayPan
                        panningDispatch={panningDispatch}
                        today={<DayView paneIndex={0} day="Today"/>}
                        tomorrow={<DayView paneIndex={1} day="Tomorrow"/>}
                    />
                    <TodoInput panning={panningDispatch.observable}/>
                </View>
            </AppDimensionContext.Provider>
        </SafeAreaView>
    )
}

const styles = StyleSheet.create({
    container: {
        height: '100%',
        width: '100%',
    },
})

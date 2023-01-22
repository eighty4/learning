import {type FC} from 'react'
import {StyleSheet, View} from 'react-native'

import {type PaneIndex} from './state/panningState'
import {TodoList} from './todos/TodoList'
import {DayHeader} from './DayHeader'
import {useAppDimensions} from './appDimensions'

export interface DayViewProps {
    day: 'Today' | 'Tomorrow'
    paneIndex: PaneIndex
}

export const DayView: FC<DayViewProps> = ({day, paneIndex}) => {
    const {listHeight: height} = useAppDimensions()
    return <View style={[styles.container, {height}]}>
        <DayHeader day={day}/>
        <TodoList paneIndex={paneIndex}/>
    </View>
}

const styles = StyleSheet.create({
    container: {
        width: '100%',
    },
})

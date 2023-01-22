import {FC} from 'react'
import {StyleSheet, Text, View} from 'react-native'

import {useAppDimensions} from './appDimensions'
import {useEditingActive} from './state/selectors'

interface DayHeaderProps {
    day: 'Today' | 'Tomorrow'
}

export const DayHeader: FC<DayHeaderProps> = ({day}) => {
    const {headerHeight: height} = useAppDimensions()
    const editing = useEditingActive()
    const color = editing ? '#fff' : '#000'

    return (
        <View style={[styles.container, {height}]}>
            <Text style={[styles.header, {color}]}>{day}</Text>
            <View style={[styles.divider, {borderBottomColor: color}]}></View>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        display: 'flex',
        justifyContent: 'flex-end',
    },
    header: {
        fontSize: 45,
        fontWeight: '400',
        paddingLeft: 20,
    },
    divider: {
        paddingTop: 5,
        borderBottomColor: '#000',
        borderBottomWidth: 1,
    },
})

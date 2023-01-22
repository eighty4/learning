import {type FC} from 'react'
import {FlatList, StyleSheet, View} from 'react-native'

import {type PaneIndex} from '../state/panningState'
import {useTodos} from '../state/selectors'
import {TodoListItem} from './TodoListItem'
import {useAppDimensions} from '../appDimensions'

interface TodoListProps {
    paneIndex: PaneIndex
}

export const TodoList: FC<TodoListProps> = ({paneIndex}) => {
    const {listHeight: height} = useAppDimensions()
    const todos = useTodos(paneIndex)
    return (
        <View style={[styles.container, {height}]}>
            <FlatList data={todos}
                      renderItem={({item: todo}) => <TodoListItem text={todo.text}/>}
                      keyExtractor={({id}) => id}
                      showsVerticalScrollIndicator={false}
            />
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        paddingLeft: 40,
        paddingVertical: 20,
        flexDirection: 'row',
    },
})

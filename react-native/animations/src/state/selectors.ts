import {useSelector} from 'react-redux'

import {type PaneIndex} from './panningState'
import {type TodaiState} from './store'
import {type Todo} from './todosState'

export function useEditingActive(): boolean {
    return useSelector<TodaiState, boolean>(state => state.editing.active)
}

export function usePaneIndex(): PaneIndex {
    return useSelector<TodaiState, PaneIndex>(state => state.panning.paneIndex)
}

export function useTodos(paneIndex: PaneIndex): Array<Todo> {
    return useSelector<TodaiState, Array<Todo>>(state => state.todos[paneIndex ? 'tomorrow' : 'today'])
}

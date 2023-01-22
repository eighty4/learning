import {createSlice} from '@reduxjs/toolkit'

const makeId = () => {
    const letters = 'abcdefghijklmnopqrstuvwxyz'
    let str = ''
    for (let i = 0; i < 4; i++) {
        str += letters.charAt(Math.floor(Math.random() * 26))
    }
    return str
}

export interface Todo {
    id: string
    text: string
}

export interface TodosState {
    today: Array<Todo>
    tomorrow: Array<Todo>
}

const initialState: TodosState = {
    today: [],
    tomorrow: [],
}

const todosSlice = createSlice({
    name: 'todos',
    initialState,
    reducers: {
        addTodo: (state, {payload}) => {
            const {paneIndex, todoText: text} = payload
            const id = makeId()
            state[paneIndex ? 'tomorrow' : 'today'].push({id, text})
        }
    }
})

export const reducer = todosSlice.reducer
export const {addTodo} = todosSlice.actions

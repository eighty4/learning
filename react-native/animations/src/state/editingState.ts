import {createSlice} from '@reduxjs/toolkit'

interface EditingState {
    active: boolean
}

const initialState: EditingState = {
    active: false
}

const editingSlice = createSlice({
    name: 'editing',
    initialState,
    reducers: {
        setEditing: (state, {payload: isEditing}) => {
            state.active = isEditing
        },
    },
})

export const reducer = editingSlice.reducer
export const {setEditing} = editingSlice.actions

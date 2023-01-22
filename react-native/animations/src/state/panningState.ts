import {createSlice} from '@reduxjs/toolkit'

export type PaneIndex = 0 | 1

export interface PanningState {
    paneIndex: PaneIndex
}

const initialState: PanningState = {
    paneIndex: 0,
}

const panningSlice = createSlice({
    name: 'panning',
    initialState,
    reducers: {
        setPaneIndex: (state, action) => {
            state.paneIndex = action.payload
        },
    },
})

export const reducer = panningSlice.reducer
export const {setPaneIndex} = panningSlice.actions

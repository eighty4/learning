import {configureStore} from '@reduxjs/toolkit'

import {reducer as editing} from './editingState'
import {reducer as panning} from './panningState'
import {reducer as todos} from './todosState'

export const store = configureStore({
    reducer: {
        editing,
        panning,
        todos,
    },
})

export type TodaiState = ReturnType<typeof store.getState>

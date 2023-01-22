import {type FC, Fragment} from 'react'
import {useDispatch} from 'react-redux'

import {setEditing} from '../state/editingState'
import {useEditingActive} from '../state/selectors'
import {type ReceivesPanningUpdatesProps} from '../PanningUpdate'
import {TodoInputButton} from './TodoInputButton'
import {TodoInputCloseMask} from './TodoInputCloseMask'
import {TodoInputFooter} from './TodoInputFooter'
import {TodoInputPanePositioned} from './TodoInputPanePositioned'
import {TodoInputTextField} from './TodoInputTextField'

export const TodoInput: FC<ReceivesPanningUpdatesProps> = ({panning}) => {
    const dispatch = useDispatch()
    const editing = useEditingActive()

    const onOpen = () => {
        if (!editing) {
            dispatch(setEditing(true))
        }
    }

    const onClose = () => {
        if (editing) {
            dispatch(setEditing(false))
        }
    }

    return (
        <Fragment>
            <TodoInputCloseMask active={editing} onMaskPress={onClose}/>
            <TodoInputFooter>
                <TodoInputPanePositioned panning={panning}>
                    {editing ? <TodoInputTextField onBlur={onClose}/> : <TodoInputButton onPress={onOpen}/>}
                </TodoInputPanePositioned>
            </TodoInputFooter>
        </Fragment>
    )
}

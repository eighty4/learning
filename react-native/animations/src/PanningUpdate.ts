import {type Observable, Subject} from 'rxjs'

import {type PaneIndex} from './state/panningState'

export type UpdateType = 0 | -1 | 1 | 2

const UpdateTypes: Record<string, UpdateType> = {
    ABORT: -1,
    GESTURE: 0,
    COMMIT: 1,
}

export class PanningUpdate {

    constructor(private readonly _type: UpdateType,
                private readonly _toPane: PaneIndex | null,
                private readonly _pannedRatio: number) {}

    get panCommitted(): boolean {
        return this._type === UpdateTypes.COMMIT
    }

    get panAborted(): boolean {
        return this._type === UpdateTypes.ABORT
    }

    get gestureCompleted(): boolean {
        return this.panCommitted || this.panAborted
    }

    get pannedRatio(): number {
        return this._pannedRatio
    }

    get toPane(): PaneIndex | null {
        return this._toPane
    }
}

export interface ReceivesPanningUpdatesProps {
    panning: Observable<PanningUpdate>
}

export interface ProducesPanningUpdatesProps {
    panningDispatch: PanningDispatch
}

export class PanningDispatch {

    private readonly subject: Subject<PanningUpdate> = new Subject<PanningUpdate>()

    panningGestureMove(towards: PaneIndex, pannedRatio: number) {
        this.subject.next(new PanningUpdate(UpdateTypes.GESTURE, towards, pannedRatio))
    }

    panningCommit(towards: PaneIndex, pannedRatio: number) {
        this.subject.next(new PanningUpdate(UpdateTypes.COMMIT, towards, pannedRatio))
    }

    panningAbort(pannedRatio: number) {
        this.subject.next(new PanningUpdate(UpdateTypes.ABORT, null, pannedRatio))
    }

    get observable(): Observable<PanningUpdate> {
        return this.subject.asObservable()
    }
}

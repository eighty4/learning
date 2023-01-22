import {createContext, useContext} from 'react'
import {ScaledSize} from 'react-native'

export const FOOTER_HEIGHT = 90 // marginBottom 20 in PanePositioned + height 70 in Button and TextField
export const FOOTER_MARGIN = 20
export const INPUT_HEIGHT = FOOTER_HEIGHT - FOOTER_MARGIN
const HEADER_PROPORTION = .15
const GUTTER_PROPORTION = .2

export interface AppDimensions {
    headerHeight: number
    listHeight: number
    footerHeight: number
    windowHeight: number
    windowWidth: number
    gutterWidth: number
    paneWidth: number
}

export function calculateAppDimensions({height: windowHeight, width: windowWidth}: ScaledSize): AppDimensions {
    const headerHeight = Math.floor(windowHeight * HEADER_PROPORTION)
    const listHeight = windowHeight - headerHeight - FOOTER_HEIGHT
    const gutterWidth = windowWidth * GUTTER_PROPORTION
    const paneWidth = windowWidth - gutterWidth
    return {
        headerHeight,
        listHeight,
        footerHeight: INPUT_HEIGHT,
        windowHeight,
        windowWidth,
        gutterWidth,
        paneWidth,
    }
}

export function useAppDimensions(): AppDimensions {
    return useContext(AppDimensionContext)
}

export const AppDimensionContext = createContext<AppDimensions>({
    headerHeight: 0,
    listHeight: 0,
    footerHeight: 0,
    windowHeight: 0,
    windowWidth: 0,
    gutterWidth: 0,
    paneWidth: 0,
})

import 'react-native'
import {render} from '@testing-library/react-native'

import {createTodaiApp} from './TodaiApp'

it('renders correctly', () => {
    render(createTodaiApp())
})

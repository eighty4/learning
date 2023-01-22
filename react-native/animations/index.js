import {AppRegistry} from 'react-native'

import {createTodaiApp} from './src/TodaiApp'
import {name as appName} from './app.json'

AppRegistry.registerComponent(appName, () => createTodaiApp)

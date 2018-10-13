import { combineReducers } from 'redux';
import storage from 'redux-persist/es/storage'
import { persistReducer } from 'redux-persist'
import loginInfo from './login'
import theme from './theme'

const userconfig = {
    key: 'loginInfo',
    storage,
    debug: false,
    blacklist: ['status']
}
const themeconfig = {
    key: 'themeInfo',
    storage,
    debug: false,
    blacklist: ['status']
}
export default combineReducers({
    loginInfo: persistReducer(userconfig, loginInfo),
    themeInfo: theme
});

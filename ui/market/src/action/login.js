import * as type from './type';
import httpServer from 'api/httpServer';
import urls from 'api/urls';


function loginSuccess(data) {
    return {
        type: type.LOGIN_SUCCESS,
        payload: data
    }
}
function loginFailure(error) {
    return {
        type: type.LOGIN_FAILURE,
        payload: error
    }
}
function logoutSuccess(data) {
    return {
        type: type.LOGOUT_SUCCSSS,
        payload: null
    }
}
// 登录action
export const login = (params) => dispatch => {
    return httpServer.get(urls.login,
        params || {},
        (response) => { dispatch(loginSuccess(response)) },
        (error) => { dispatch(loginFailure(error)) })
}
// 登出action
export const logout = () => dispatch => {
    return httpServer.get(urls.logout,
        {},
        (response) => { dispatch(logoutSuccess(response)) },
        (error) => { console.log(error) })
}

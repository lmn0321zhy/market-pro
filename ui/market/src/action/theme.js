import * as type from './type';


function CHANGE_THEME(data) {
    return {
        type: type.CHANGE_THEME,
        payload: data
    }
}
// 更新主题
export const changeTheme = (data) => dispatch => {
    return dispatch(CHANGE_THEME(data))
}

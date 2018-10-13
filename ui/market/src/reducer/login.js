import * as type from '../action/type';

const loginInfo = (state = {}, action) => {
    console.log(action, state)
    switch (action.type) {
        case type.LOGIN_SUCCESS:
            return {
                ...state,
                userInfo: action.payload,
                loginerror: null
            }
        case type.LOGIN_FAILURE:
            return {
                ...state,
                loginerror: action.payload,
                userInfo: null
            };
        case type.LOGOUT_SUCCSSS:
            return {
                ...state,
                userInfo: null,
                loginerror: null
            };
        default:
            return { ...state };
    }
};
export default loginInfo;

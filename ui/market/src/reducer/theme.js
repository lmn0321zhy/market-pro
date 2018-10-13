import * as type from '../action/type';

const themeInfo = (state = {}, action) => {
    switch (action.type) {
        case type.CHANGE_THEME:
            return {
                ...state,
                theme: action.payload
            }
        default:
            return { ...state };
    }
};
export default themeInfo;

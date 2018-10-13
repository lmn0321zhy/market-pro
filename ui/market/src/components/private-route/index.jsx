import React from 'react';
import { Route, Redirect, withRouter } from 'react-router-dom';
import PropTypes from 'prop-types';
// import storage from 'utils/storage.js';
// 使用
// const  PrivateRoute =  HocPrivateRoute(Route);

function CreatePrivateRoute(WrappedComponent, hocProps) {
    if (!!!WrappedComponent) {
        throw new Error('缺少组件参数');
        return false;
    }
    //withRouter 也是一个高阶组件 传递 history
    return withRouter(
        class extends React.Component {
            constructor(props) {
                super(props);
            }

            componentWillMount() {
                let isAuthenticated = storage.getItem('token') ? true : false;
                this.setState({ isAuthenticated: isAuthenticated })
                if (!isAuthenticated) {
                    const { history } = this.props;
                    setTimeout(() => {
                        history.replace('/login');
                    }, 1000)
                }
            }

            render() {
                return this.state.isAuthenticated ? (
                    <WrappedComponent {...hocProps} />
                ) : ('请重新登录');
            }
        }
    )
}


export default CreatePrivateRoute;

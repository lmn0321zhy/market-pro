import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
// import './style/lib/animate.css';
import { Provider } from 'react-redux';
import thunk from 'redux-thunk';
import { createStore, applyMiddleware } from 'redux';
import reducer from './reducer';
// import { AppContainer } from 'react-hot-loader';
// import Page from './Page';

// redux 注入操作
const middleware = [thunk];
const store = createStore(reducer, applyMiddleware(...middleware));

const render = Component => {   // 增加react-hot-loader保持状态刷新操作，如果不需要可去掉并把下面注释的打开
    ReactDOM.render(
        <Provider store={store}>
            <Component store={store} />
        </Provider>
        ,
        document.getElementById('app')
    );
};

render(App);


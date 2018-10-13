import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import { Provider } from 'react-redux';
import { AppContainer } from 'react-hot-loader';
import { PersistGate } from 'redux-persist/integration/react'
import { applyMiddleware, createStore, compose } from 'redux'
import thunk from 'redux-thunk';
import { persistStore, persistCombineReducers, persistReducer } from 'redux-persist'
import reconciler from 'redux-persist/lib/stateReconciler/autoMergeLevel2'
import storage from 'redux-persist/es/storage'
import devToolsEnhancer from 'remote-redux-devtools';
import rootReducers from './reducer'
import Perf from 'react-addons-perf'

const logger = store => next => action => {
    if (typeof action === 'function') console.log('dispatching a function');
    else console.log('loggerMiddleware dispatch', action);
    let result = next(action);
    console.log('loggerMiddleware next state', store.getState());
    return result;
}
const crashReporter = store => next => action => {
    try {
        return next(action)
    } catch (err) {
        console.error('crashReporterMiddleware Caught an exception!', err)
        Raven.captureException(err, {
            extra: {
                action,
                state: store.getState()
            }
        })
        throw err
    }
}

const middlewares = [
    logger,
    crashReporter,
    thunk
];

const config = {
    key: 'root',
    storage,
    stateReconciler: reconciler, //合并模式
    debug: false
}

const reducers = persistReducer(config, rootReducers)
const enhances = [applyMiddleware(...middlewares)]

const configureStore = (initialState) => {
    // let store = createStore(reducers, initialState, compose(...enhances, devToolsEnhancer({ realtime: true, port: 8080 })));
    const store = createStore(rootReducers, initialState, compose(...enhances))
    return store
}
const store = configureStore()
const persistor = persistStore(store);

const render = Component => {   // 增加react-hot-loader保持状态刷新操作，如果不需要可去掉并把下面注释的打开
    ReactDOM.render(
        <AppContainer>
            <Provider store={store}>
                <PersistGate loading={null} persistor={persistor}>
                    <Component />
                </PersistGate>
            </Provider>
        </AppContainer>
        ,
        document.getElementById('app')
    );
};

render(App);

if (module.hot) {
    // 隐藏You cannot change <Router routes>; it will be ignored 错误提示
    // react-hot-loader 使用在react-router 3.x上引起的提示，react-router 4.x不存在
    // 详情可参照https://github.com/gaearon/react-hot-loader/issues/298
    // const orgError = console.error; // eslint-disable-line no-console
    console.error = (...args) => { // eslint-disable-line no-console
        if (args && args.length === 1 && typeof args[0] === 'string' && args[0].indexOf('You cannot change <Router routes>;') > -1) {
            // React route changed
        } else {
            // Log the error as normally
            orgError.apply(console, args);
        }
    };
    module.hot.accept(App, () => {
        render(App);
    })
}

console.log(1111111111111111111111111111)
window.Perf = Perf

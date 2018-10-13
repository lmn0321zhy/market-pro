import axios from 'axios';
import { message } from 'antd';

const toType = (obj) => {
    return ({}).toString.call(obj).match(/\s([a-zA-Z]+)/)[1].toLowerCase()
}
const filterNull = (o) => {
    for (let key in o) {
        if (o[key] === null) {
            delete o[key]
        }
        if (toType(o[key]) === 'string') {
            o[key] = o[key].trim()
        } else if (toType(o[key]) === 'object') {
            o[key] = filterNull(o[key])
        } else if (toType(o[key]) === 'array') {
            o[key] = filterNull(o[key])
        }
    }
    return o
}

axios.interceptors.request.use(config => {
    // 请求拦截，使用本地mock数据
    const mock = /^(\/mock\/)(.*)(.json)$/
    const url = config.url;
    if (config.method === 'post' && mock.test(url)) {
        return Object.assign({}, config, {
            method: 'get'
        })
    } else {
        return config
    }
}, error => {
    return Promise.reject(error)
})


axios.interceptors.response.use(response => {
    return response
}, error => {
    return Promise.resolve(error.response)
})

const errorState = (data) => {
    console.log(data)
    message.error(data.msg)
    //隐藏loading
    // console.log(response)
    // // 如果http状态码正常，则直接返回数据
    // if (response && (response.status === 200 || response.status === 304 || response.status === 400)) {
    //     return response
    //     // 如果不需要除了data之外的数据，可以直接 return response.data
    // } else {
    //     Vue.prototype.$msg.alert.show({
    //         title: '提示',
    //         content: '网络异常'
    //     })
    // }

}

const successState = (res) => {
    // message.error(res.msg)
    // 隐藏loading
    // 统一判断后端返回的错误码
    if (res.data.statusCode === '000002') {
        // Vue.prototype.$msg.alert.show({
        //     title: '提示',
        //     content: res.data.errDesc || '网络异常',
        //     onShow() {
        //     },
        //     onHide() {
        //         console.log('确定')
        //     }
        // })
    } else if (res.data.statusCode !== '000002' && res.data.statusCode !== '000000') {
        // Vue.prototype.$msg.alert.show({
        //     title: '提示',
        //     content: res.data.errDesc || '网络异常',
        //     onShow() {

        //     },
        //     onHide() {
        //         console.log('确定')
        //     }
        // })
    }
}

const apiAxios = (method, url, params) => {
    if (params) {
        params = filterNull(params)
    }
    const httpDefaultOpts = {
        method: method,
        url: url,
        data: method === 'POST' || method === 'PUT' ? params : null,
        params: method === 'GET' || method === 'DELETE' ? params : null,
        // baseURL: root,
        withCredentials: false
    }
    let promise = new Promise(function (resolve, reject) {
        axios(httpDefaultOpts).then(
            (res) => {
                console.log('res', res)
                if (res.status === 200 && res.data.statusCode === '000000') {
                    successState(res.data)
                    resolve(res.data.data)
                } else {
                    errorState(res.data)
                }
            }
        ).catch(
            (res) => {
                errorState(res)
                reject(res)
            }
        )

    })
    return promise
}

export default {
    get: function (url, params, resolve, reject) {
        return apiAxios('GET', url, params).then((data) => resolve(data), (data) => reject(data))
    },
    post: function (url, params, resolve, reject) {
        return apiAxios('POST', url, params).then((data) => resolve(data), (data) => reject(data))
    },
    put: function (url, params, resolve, reject) {
        return apiAxios('PUT', url, params).then((data) => resolve(data), (data) => reject(data))
    },
    delete: function (url, params, resolve, reject) {
        return apiAxios('DELETE', url, params).then((data) => resolve(data), (data) => reject(data))
    }
}

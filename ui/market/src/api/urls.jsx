import apiUrl from 'api/apiUrl';

const hostApi = {
    production: {
        host: '',
        prefix: '/api',
        suffix: ''
    },
    // develop: {
    //     host: '',
    //     prefix: '/api',
    //     suffix: ''
    // },
    develop: {
        host: '',
        prefix: '/mock',
        suffix: '.json'
    }
}

const urls = {}
const host = hostApi[process.env.NODE_ENV];


Object.keys(apiUrl).forEach((key) => {
    urls[key] = host.prefix + apiUrl[key] + host.suffix;
});
export default urls;

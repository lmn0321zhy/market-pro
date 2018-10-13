const path = require('path');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
// 4、提取第三方JS庫
const VENDOR = [
    "react",
    "react-dom"
];

module.exports = {
    entry: {
        app: './src/index.test.js',
        //1.1
        vendor: VENDOR
    },
    output: {
        path: path.resolve(__dirname, 'dist'),
        // filename: 'app_[chunkhash].js'
        // 加上/js就会输出到js文件夹下面
        filename: 'js/[name]_[chunkhash].js'
    },
    resolve: {
        //自动扩展文件后缀名，意味着我们require模块可以省略不写后缀名
        extensions: ['.js', '.jsx'],
        alias: {
            "components": path.resolve(__dirname, 'src/components'),
            "api": path.resolve(__dirname, 'src/api'),
            "container": path.resolve(__dirname, 'src/container'),
            "assets": path.resolve(__dirname, 'src/assets'),
            "styles": path.resolve(__dirname, 'src/styles'),
            "action": path.resolve(__dirname, 'src/action'),
            "route": path.resolve(__dirname, 'src/route')
        }
    },
    module: {
        rules: [
            {
                test: /\.css$/,
                use: [
                    { loader: "style-loader" },
                    { loader: "css-loader" },
                ]
            },
            {
                test: /\.less$/,
                exclude: [/node_modules/],
                use: [
                    { loader: "style-loader" },
                    { loader: "css-loader", options: { modules: true } },
                    {
                        loader: "less-loader"

                    }
                ]
            },
            //2 处理图片,图片路径需是相对路径才能看到效果
            {
                test: /\.(jpg|png|gif|svg)$/,
                use: {
                    loader: 'url-loader',
                    options: {
                        limit: 10000,
                        // 默认打包到dist下的img文件夹
                        name: 'img/[name].[hash:7].[ext]'
                    }
                }
            },
            //3 编译es6和编译jsx
            {
                test: /(\.jsx|\.js)$/,
                exclude: /node_modules/,
                use: [
                    { loader: "babel-loader" },
                    { loader: "eslint-loader" }
                ]
            },
            //4 处理字体
            {
                test: /\.(woff2?|eot|ttf|otf)(\?.*)?$/,
                use: {
                    loader: 'url-loader',
                    options: {
                        limit: 10000,
                        // fonts/打包到dist下的fonts文件夹
                        name: 'fonts/[name].[hash:7].[ext]'
                    }
                }
            }

        ]
    },
    plugins: [
        //5、提取css到单独的文件夹
        // new ExtractTextPlugin({
        //     //加上/css就会输出到css文件夹下面
        //     filename: 'css/app_[hash].css',
        //     // filename:'app_[chunkhash].css',
        //     disable: false,
        //     allChunks: true
        // }),
        new HtmlWebpackPlugin({
            filename: 'index.html',
            template: 'index.html'
        }),
    ]
};
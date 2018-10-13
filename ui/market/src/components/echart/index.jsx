import React from 'react'
import ReactEcharts from 'echarts-for-react';
import options from './options'

export default class CommonEchart extends React.Component {
    constructor(props) {
        super(props)

    }
    componentDidMount() {

    }
    getOption = () => {
        const data = [
            { value: 335, name: '直接访问' },
            { value: 310, name: '邮件营销' },
            { value: 234, name: '联盟广告' },
            { value: 135, name: '视频广告' },
            { value: 1548, name: '搜索引擎' }
        ]
        return options[this.props.type].setData(data);
    }

    render() {
        return (
            <ReactEcharts
                option={this.getOption()}
                {...this.props}
            />
        )
    }
}

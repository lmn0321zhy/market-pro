import React from 'react'
import { Layout } from 'antd';
import { Route, Switch, Redirect } from 'react-router-dom';
import SiderMenu from 'components/sider-menu';
import GlobalHeader from 'components/global-header';
import NotFound from 'container/404'
import { connect } from 'react-redux';
import Admin from 'route/admin'
import styles from './index.less';

const { Content } = Layout;

class Main extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
        }
    }
    componentDidMount() {

    }
    render() {
        const userInfo = this.props.userInfo;
        const layout = <div className={styles.container}>
            <Layout style={{ height: '100%' }}>
                <SiderMenu collapsed={false} />
                <Layout>
                    <GlobalHeader />
                    <Content>
                        <Admin />
                    </Content>
                </Layout>
            </Layout>
        </div>
        return (
            <div style={{ height: '100%' }}>
                {!userInfo ? <Redirect to='/login' /> : layout}
            </div>
        )
    }
}

// 传入所有state，返回指定的state数据，放入到当前组件props中
const mapStateToProps = (state) => {
    return state.loginInfo ?
        {
            userInfo: state.loginInfo.userInfo,
            loginerror: state.loginInfo.loginerror
        } : {
            userInfo: null,
            loginerror: null
        }
};

export default connect(mapStateToProps)(Main);

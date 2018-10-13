import React, { PureComponent } from 'react';
import { Menu, Icon, Layout, Badge, Button } from 'antd';
import { connect } from 'react-redux';
import { logout } from 'action/login'
import { changeTheme } from 'action/theme'
import { bindActionCreators } from 'redux';
import styles from './index.less';
const { Header } = Layout;
const SubMenu = Menu.SubMenu;
const MenuItemGroup = Menu.ItemGroup;

class GlobalHeader extends PureComponent {
    constructor(props) {
        super(props)
        this.state = {
            loading: false,
            user: null,
            error: null
        }
    }
    componentDidMount() {
        // const { AA } = this.props;
        // console.log(AA)
        // if (AA) {
        //     this.props.AA.login('LOGIN');
        // }
    }
    logOut = () => {
        const { events } = this.props;
        console.log(events)
        if (events) {
            events.logout();
        }
    }
    render() {
        const { userInfo, theme } = this.props;
        return (
            <Header className={styles.globalHeader} style={{ background: theme }}>
                <span style={{ paddingLeft: '15px', color: '#ff0000', fontSize: '25px' }}>管理系统</span>
                <div className={styles.globalHeaderUser}>
                    <Menu
                        mode='horizontal'
                        style={{ lineHeight: '64px' }}
                        onClick={this.menuClick}
                    >
                        <Menu.Item key='1'>
                            <Badge count={25} overflowCount={10} style={{ marginLeft: 10 }}>
                                <Icon type='notification' />
                            </Badge>
                        </Menu.Item>
                        <SubMenu title={<span>{userInfo.userName}<i className='on bottom b-white' /></span>}>
                            <MenuItemGroup title='用户中心'>
                                <Menu.Item key='setting:1'>你好 - 刘梦南</Menu.Item>
                                <Menu.Item key='setting:2'>个人信息</Menu.Item>
                                <Menu.Item key='logout'><span onClick={this.logout}>退出登录</span></Menu.Item>
                            </MenuItemGroup>
                            <MenuItemGroup title='设置中心'>
                                <Menu.Item key='setting:3'>个人设置</Menu.Item>
                                <Menu.Item key='setting:4'>系统设置</Menu.Item>
                            </MenuItemGroup>
                        </SubMenu>
                    </Menu>
                    <Button className={styles.globalHeaderLogout} onClick={this.logOut} type='primary' icon='logout' size='large' />
                </div>
            </Header>
        )
    }
}
// 传入所有state，返回指定的state数据，放入到当前组件props中
const mapStateToProps = (state) => {
    console.log('state', state)
    return {
        userInfo: state.loginInfo.userInfo,
        loginerror: state.loginInfo.loginerror,
        theme: state.themeInfo.theme
    }
};
//传入dispatch，返回使用bindActionCreators()绑定的action方法
const mapDispatchToProps = (dispatch) => ({
    events: bindActionCreators(Object.assign({}, { changeTheme, logout }), dispatch)
})
export default connect(mapStateToProps, mapDispatchToProps)(GlobalHeader);

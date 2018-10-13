import React, { PureComponent } from 'react';
import styles from './index.less'
import classNames from 'classnames'

export default class Tabs extends PureComponent {
    constructor(props) {
        super(props)
        this.state = {
            data: props.data || [],
            tabPosition: props.tabPosition || 'left',
            activeKey: props.defaultActiveKey || 0
        }
    }
    componentWillReceiveProps(nextProps) {
        this.setState({ activeKey: nextProps.activeKey || 0 });
    }
    handleChick = (item, index) => {
        this.setState({
            activeKey: index
        }, () => {
            if (this.props.handleChick && typeof this.props.handleChick === 'function') { this.props.handleChick(item) }
        })
    }
    render() {
        const { data, tabPosition, activeKey } = this.state;
        const { tabBarStyle, contentStyle } = this.props;
        const dull_tabs = 'dull_tabs_' + tabPosition;
        const dull_tabs_tabsBar = 'dull_tabs_tabsBar_' + tabPosition;
        const dull_tabs_tabsItem = 'dull_tabs_tabsItem_' + tabPosition;
        const dull_tabs_tabsItem_active = 'dull_tabs_tabsItem_active_' + tabPosition;
        const dull_tabs_content = 'dull_tabs_content_' + tabPosition;
        const dull_tabs_tabsItem_active_className = classNames(styles[dull_tabs_tabsItem_active], tabBarStyle)
        const dull_tabs_tabsItem_className = classNames(styles[dull_tabs_tabsItem], tabBarStyle)
        const dull_tabs_content_classname = classNames(styles[dull_tabs_content], contentStyle)
        const tabsContent = data.map((item, index) => {
            return <div key={index} className={index === activeKey ? dull_tabs_tabsItem_active_className : dull_tabs_tabsItem_className} onClick={() => this.handleChick(item, index)}>
                {item.name}
            </div>
        })
        return (
            <div className={styles[dull_tabs]}>
                <div className={styles[dull_tabs_tabsBar]}>
                    {tabsContent}
                </div>
                <div className={dull_tabs_content_classname}>
                    111
                </div>
            </div>
        )
    }
}

import React from 'react'
import { Select } from 'antd';
import { changeTheme } from 'action/theme';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
const Option = Select.Option;

class Demo extends React.Component {
  state = {
    theme: 'red',
  }

  handleChangeTheme = (theme) => {
    console.log(this.props)
    this.setState({
      theme: theme
    }, () => {
      if (this.props.events && typeof this.props.events.changeTheme === 'function') {
        this.props.events.changeTheme(theme)
      }
    })

  }

  render() {
    return (
      <div>
        <div style={{ marginBottom: 16 }}>
          <Select
            value={this.state.theme}
            onChange={this.handleChangeTheme}
            dropdownMatchSelectWidth={false}
          >
            <Option value='red'>红</Option>
            <Option value='yellow'>黄</Option>
            <Option value='blue'>蓝</Option>
          </Select>
        </div>
      </div>
    );
  }
}

const mapStateToProps = (state, ownProps) => {
  return {
    theme: state.themeInfo.theme
  }
};
//传入dispatch，返回使用bindActionCreators()绑定的action方法
const mapDispatchToProps = (dispatch) => ({
  events: bindActionCreators(Object.assign({}, { changeTheme }), dispatch)
})

export default connect(mapStateToProps, mapDispatchToProps)(Demo);


import React from 'react'
import { Form, Icon, Input, Button, Checkbox } from 'antd';
import { Redirect } from 'react-router-dom';
import { DocumentTitle } from 'react-document-title';
import styles from './index.less';
import { login } from 'action/login';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

const FormItem = Form.Item;

class NormalLoginForm extends React.Component {
  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) {
        if (this.props.events && typeof this.props.events.login === 'function') {
          this.props.events.login(values)
        }
      }
    });
  }
  render() {
    const { getFieldDecorator } = this.props.form;
    const userInfo = this.props.userInfo;
    console.log(userInfo)
    const layout = <div className={styles.login}>
      <div className={styles.loginForm} >
        <div className={styles.loginLogo}>
          <span>React Admin</span>
        </div>
        <Form onSubmit={this.handleSubmit} style={{ maxWidth: '300px' }}>
          <FormItem>
            {getFieldDecorator('userName', {
              rules: [{ required: true, message: '请输入用户名!' }],
            })(
              <Input prefix={<Icon type='user' style={{ fontSize: 13 }} />} placeholder='管理员输入admin, 游客输入guest' />
            )}
          </FormItem>
          <FormItem>
            {getFieldDecorator('password', {
              rules: [{ required: true, message: '请输入密码!' }],
            })(
              <Input prefix={<Icon type='lock' style={{ fontSize: 13 }} />} type='password' placeholder='管理员输入admin, 游客输入guest' />
            )}
          </FormItem>
          <FormItem>
            {getFieldDecorator('remember', {
              valuePropName: 'checked',
              initialValue: true,
            })(
              <Checkbox>记住我</Checkbox>
            )}
            <a className='login-form-forgot' href='' style={{ float: 'right' }}>忘记密码</a>
            <Button type='primary' htmlType='submit' className='login-form-button' style={{ width: '100%' }}>
              登录
                </Button>
            <p style={{ display: 'flex', justifyContent: 'space-between' }}>
              <a href=''>或 现在就去注册!</a>
              <a onClick={this.gitHub} ><Icon type='github' />(第三方登录)</a>
            </p>
          </FormItem>
        </Form>
      </div>
    </div>
    return (
      // <DocumentTitle title='登录'>
      <div style={{ height: '100%' }}>
        {userInfo ? <Redirect to='/dashboard' /> : layout}
      </div>
      // </DocumentTitle>
    );
  }
}
const WrappedNormalLoginForm = Form.create()(NormalLoginForm)
// 传入所有state，返回指定的state数据，放入到当前组件props中
const mapStateToProps = (state, ownProps) => {
  console.log(state)
  return {
    userInfo: state.loginInfo.userInfo,
    loginerror: state.loginInfo.loginerror
  }
};
//传入dispatch，返回使用bindActionCreators()绑定的action方法
const mapDispatchToProps = (dispatch) => ({
  events: bindActionCreators(Object.assign({}, { login }), dispatch)
})

export default connect(mapStateToProps, mapDispatchToProps)(WrappedNormalLoginForm);

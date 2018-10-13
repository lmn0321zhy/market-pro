import React from 'react'
import { Route, Switch } from 'react-router-dom';
import Dashboard from 'container/main/pages/dashboard'
import NotFound from 'container/404'
import Demo from 'container/demo'

export default class Admin extends React.Component {
    render() {
        return (
            <Switch>
                {/* <Route exact path='/' component={Dashboard} /> */}
                <Route exact path='/dashboard' component={Dashboard} />
                <Route exact path='/demo' component={Demo} />
                <Route component={NotFound} />
            </Switch>
        )
    }
}

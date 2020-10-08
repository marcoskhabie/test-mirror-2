import React from 'react';
import { Route, Redirect } from 'react-router-dom';
import {isLoggedIn} from './utils';

type PrivateRouteProps = {
    component: (props: any) => {},
    path: string
}

const PrivateRoute = ({component, path, ...rest}: PrivateRouteProps) => {
    return (
        <Route {...rest} path={path} render={(props) => (
            isLoggedIn() ?
                component(props)
                : <Redirect to="/" />
        )} />
    );
};

export default PrivateRoute;

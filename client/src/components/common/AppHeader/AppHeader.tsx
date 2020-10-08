import React from 'react';
import { NavLink, withRouter} from 'react-router-dom';
import './AppHeader.css';
import logo from '../../../assets/TrendzLogo.png';

const AppHeader = ({history}: any) => {

    const handleLogout = () => {
        localStorage.removeItem('token');
        history.push('/');
    }

    return (
        <header className={"app-header"}>
            <div className={"app-branding"}>
                <img src={logo} alt={''}/>
            </div>
            <div className={"app-options"}>
                <div style={{display: 'flex', flexDirection: 'row', height: '100%'}}>
                    <NavLink to={"/main/home"} className={"app-option"}>Home</NavLink>
                    <NavLink to={"/main/profile"} className={"app-option"}>Profile</NavLink>
                </div>
                <div className={'logout'} onClick={() => handleLogout()}>Logout</div>
            </div>
        </header>
    )
}

export default withRouter(AppHeader);

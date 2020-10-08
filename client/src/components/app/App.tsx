import React, { Component } from 'react'
import './App.css'
import { Switch, Route } from 'react-router-dom'
import Register from "../Register/Register";
import PrivateRoute from "../Routing/PrivateRoute";
import {AppFrame} from "../Routing/AppFrame";
import Profile from "../Profile/Profile";
import Login from "../Login/Login";
import EditProfile from "../EditProfile/EditProfile";
import Home from "../Home/Home";
import CreateTopic from "../CreateTopic/CreateTopic";
import CreatePost from "../CreatePost/CreatePost";
import EditPost from "../EditPost/EditPost";
import Topic from "../Topic/Topic";
type MatchProps = {
  match: {
    url: string
  }
}

class App extends Component{
  render() {
    return (
      <div className="app">
        <Switch>
          <Route exact={true} path="/" component={Login}/>
          <Route exact={true} path="/register" component={Register}/>
          <PrivateRoute path={'/main'} component={({match}: MatchProps) => ([
              <AppFrame>
                <Switch>
                  <Route exact path={`${match.url}/profile`} component={Profile}/>
                  <Route exact path={`${match.url}/home`} component={Home}/>
                  <Route exact path={`${match.url}/topic`} component={Topic}/>
                  <Route exact path={`${match.url}/editProfile`} component={EditProfile}/>
                  <Route exact path={`${match.url}/createTopic`} component={CreateTopic}/>
                  <Route exact path={`${match.url}/createPost`} component={CreatePost}/>
                  <Route exact path={`${match.url}/editPost`} component={EditPost}/>
                </Switch>
              </AppFrame>
          ])}/>
        </Switch>
      </div>
    )
  }
}

export default App

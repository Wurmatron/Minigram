/* 
	Main Component that handles the routes to other components as well as sending them their props, no actual content is put here
*/

import './App.css';
import React, {Component} from 'react';
import {BrowserRouter as Router , Route , Switch , Link , Redirect } from 'react-router-dom'
import LoginPage from './Components/LoginPage'
import Profile from './Components/Profile'
import RegisterPage from './Components/RegisterPage'
import PostPage from './Components/PostPage'

class App extends React.Component {
	constructor(props) {
        super(props);

        this.state = {
            sessionToken : "",
			loggedInId : "",
			email : "",
			pass : "",
        };
		this.setToken = this.setToken.bind(this);
		this.setLoggedInId = this.setLoggedInId.bind(this);
		this.setEmailAndPass = this.setEmailAndPass.bind(this);
	}

	setToken = (token) => {
		this.setState({sessionToken : token});
	}

	setLoggedInId = (id) => {
		this.setState({loggedInId : id});
	}

	setEmailAndPass = (email , pass) => {
		this.setState({
			email : email,
			pass : pass
		})
	}

	render(){
		return (
			<Router >
				<div>
					<Route exact path="/" >
						<LoginPage setToken = {this.setToken} setLoggedInId = {this.setLoggedInId} setEmailAndPass = {this.setEmailAndPass}/>
					</Route>

					<Route path="/register" >
						<RegisterPage email = {this.state.email} pass = {this.state.pass} setToken = {this.setToken} setLoggedInId = {this.setLoggedInId}/>
					</Route>

					<Route path="/profile">
						<Profile sesToken = {this.state.sessionToken} loggedId={this.state.loggedInId}/>
					</Route>

					<Route path="/post">
						<PostPage loggedId = {this.state.loggedInId}/>
					</Route>
				</div>
			</Router>
		);
	}
}

export default App;

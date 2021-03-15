/* 
	Main Component that handles the routes to other pages, do not put any content here it should be ran through other components in the Components folder
*/

import './App.css';
import React, {Component} from 'react';
import {BrowserRouter as Router , Route , Switch , Link , Redirect } from 'react-router-dom'
import LoginPage from './Components/LoginPage'

class App extends React.Component {
	constructor(props) {
        super(props);

        this.state = {
            sessionToken : "",
        };
		this.setToken = this.setToken.bind(this);
	}

	setToken = (token) => {
		this.setState({sessionToken : token});
	}

	render(){
		return (
		<Router >
			{/*Sets the default page to LoginPage */}
			<Route path="/" >
				<LoginPage setToken = {this.setToken}/>
			</Route>
		</Router>
		);
	}
}

export default App;

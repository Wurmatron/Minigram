/* 
	Main Component that handles the routes to other pages, do not put any content here it should be ran through other components in the Components folder
*/

import logo from './logo.svg';
import './App.css';
import React, {Component} from 'react';
import {BrowserRouter as Router , Route , Switch , Link , Redirect } from 'react-router-dom'
import LoginPage from './Components/LoginPage'

class App extends React.Component {

	render(){
		return (
		<Router >
			{/*Sets the default page to LoginPage */}
			<Route path="/" component={LoginPage} />
		</Router>
		);
	}
}

export default App;

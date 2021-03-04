/*
    Component for the Login page, this is the first thing any user should see when going to the site
*/

import React, {Component} from 'react';
import '../App.css'


class LoginPage extends React.Component{
    constructor(props) {
        super(props);
    }


    render(){
		return (
            <div>
                <div className="sticky-top">
                    <nav className="navbar navbar-light bg-light border-bottom border-dark">
                        <span className="navbar-text headerText text-dark">Minigram </span>
                    </nav>
                </div>
                <div className="container h-100 mt-2">
                    <div className="row h-100 justify-content-center align-items-center">
                        <form className="col-12">
                            <div className="form-group">
                                <label for="username">Username</label>
                                <input type="text" className="form-control" id="username"/>
                            </div>
                            <div className="form-group">
                                <label for="password">Password</label>
                                <input type="password" class="form-control" id="password"/>
                            </div>
                            <button type="submit" class="btn btn-primary">Login</button>
                            <button type="submit" class="btn btn-primary float-right">Register</button>
                        </form>
                    </div>
                </div>
            </div>
		);
	}
}

export default LoginPage;
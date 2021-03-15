/*
    Component for the Login page, this is the first thing any user should see when going to the site
*/

import React, {Component} from 'react';
import axios from "axios";
import '../App.css'


class LoginPage extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            email: '',
            password: '',
        };

        this.loginHandler = this.loginHandler.bind(this);
        this.registerHandler = this.registerHandler.bind(this);
        this.changeHandler = this.changeHandler.bind(this);
    }

    tokenHandler(token) {
        this.props.setToken(token);
    }

    changeHandler = (e) => {
        let name = e.target.id;
        let val = e.target.value;
        this.setState({[name] : val});
    }

    //Handles login for the user
    loginHandler = () => {
        //Need to assign this to a variable or you can't call method after request
        let self = this;
        const response = axios.post('http://localhost:8080/login' , {email: this.state.email , password_hash: this.state.password})
            .then(function (response){
                if(response.status === 200){
                    self.setState({token : response.data.data.token});
                    self.tokenHandler(self.state.token);
                }
            })
            .catch(function (error) {
                console.log(error);
            });
        this.tokenHandler(this.state.token);
    }


    //Handles register on a succesful register will login the user by calling loginHandler
    registerHandler = () => {
        let self = this;
        let newName = prompt('Please enter your name');
        const response =   axios.post('http://localhost:8080/register' , {name: newName , email: this.state.email , password_hash: this.state.password})
            .then(function (response){
                if(response.status === 200){
                    self.loginHandler()
                }
            })
            .catch(function (error) {
                console.log(error);
            });
    }

        
    render(){
		return (
            <div>
                <div className="sticky-top">
                    <nav className="navbar navbar-light bg-light border-bottom border-dark">
                        <span className="navbar-text headerText text-dark">Minigram</span>
                    </nav>
                </div>
                <div className="container h-100 mt-2">
                    <div className="row h-100 justify-content-center align-items-center">
                        <form className="col-12">
                            <div className="form-group">
                                <label for="email">Email</label>
                                <input type="text" className="form-control" id="email" onChange={this.changeHandler}/>
                            </div>
                            <div className="form-group">
                                <label for="password">Password</label>
                                <input type="password" class="form-control" id="password" onChange={this.changeHandler}/>
                            </div>
                            <button type="button" class="btn btn-primary" onClick={this.loginHandler}>Login</button>
                            <button type="button" class="btn btn-primary float-right" onClick={this.registerHandler}>Register</button>
                        </form>
                    </div>
                </div>
            </div>
		);
	}
}

export default LoginPage;
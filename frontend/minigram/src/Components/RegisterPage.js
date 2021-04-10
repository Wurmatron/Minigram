/*
    Near identical to LoginPage, a user is sent here after they select to register for an account. 
*/


import React, {Component} from 'react';
import axios from "axios";
import { withRouter } from 'react-router-dom'
import '../App.css'

class RegisterPage extends React.Component {
    constructor(props){
        super(props)

        this.state = {
            name : "",
        }

        this.loginHandler = this.loginHandler.bind(this);
        this.registerHandler = this.registerHandler.bind(this);
        this.changeHandler = this.changeHandler.bind(this);
    }

    //Updates state as user enters in on a form
    changeHandler = (e) => {
        let name = e.target.id;
        let val = e.target.value;
        this.setState({[name] : val});
    }

    //The next two both update state in the App.js component tokenHandler also 
    tokenHandler(token) {
        this.props.setToken(token);
        this.props.history.push("/profile")
    }

    idHandler(id){
        this.props.setLoggedInId(id);
        this.props.setProfile(id);
    }

    loginHandler = () => {
        //Need to assign this to a variable or you can't call method after request
        let self = this;
        const response = axios.post('http://localhost:8080/login' , {email: this.props.email , password_hash: this.props.pass})
            .then(function (response){
                if(response.status === 200){
                    self.tokenHandler(response.data.data.token);
                    self.idHandler(response.data.data.id);
                }
            })
            .catch(function (error) {
                console.log(error);
            });
        this.tokenHandler(this.state.token);
    }

    registerHandler = () => {
        let self = this;
        const response = axios.post('http://localhost:8080/register' , {name: this.state.name , email: this.props.email , password_hash: this.props.pass})
            .then(function (response){
                console.log(response)
                if(response.status === 201){
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
                                <input type="text" className="form-control" id="email" placeholder={this.props.email} readOnly/>
                            </div>
                            <div className="form-group">
                                <label for="password">Password</label>
                                <input type="password" class="form-control" id="password" readOnly/>
                            </div>
                            <div className="form-group">
                                <label for="name">Name</label>
                                <input type="text" class="form-control" id="name" onChange={this.changeHandler}/>
                            </div>
                            <button type="button" class="btn btn-primary" onClick={this.registerHandler}>Register</button>
                        </form>
                    </div>
                </div>
            </div>
		);
	}
}

export default withRouter(RegisterPage);
/*
    Navbar component, should be present in every other component, but LoginPage as Login Page just needs the text from the bar
*/

import React, {Component} from 'react';
import { withRouter } from 'react-router-dom'
import axios from "axios";

class Navbar extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            toFollow : "",
        }

        this.postHandler = this.postHandler.bind(this);
        this.homePage = this.homePage.bind(this);
        this.followUser = this.followUser.bind(this);
        this.updateText = this.updateText.bind(this);
    }

    postHandler() {
        this.props.history.push("/post")
    }

    homePage() {
        if(this.props.location.pathname === "/profile"){
            this.props.history.push("/feed")
        }else{
            this.props.setProfile(this.props.loggedId)
            this.props.history.push("/profile")
        }
    }

    followUser = async (e) => {
        if(this.state.followUser !== ""){
            e.preventDefault();
            let users = await axios.get('http://localhost:8080/account');
            users = users.data.data;
            console.log(users);
            let id = ""
            users.forEach(user => {
                if(user.name === this.state.toFollow)
                    id = user.id
            });
            let url = 'http://localhost:8080/accounts/follow/' + id;
            await axios.post(url , {id : id} , {headers : {accept: "application/json" , 'token' : this.props.sesToken}})
        }
    }

    updateText = (e) => {
        this.setState({toFollow : e.target.value});
    }


    render(){
        return(
        <div className="sticky-top">
            <nav className="navbar navbar-light bg-light border-bottom border-dark">
                <span className="navbar-text headerText text-dark" onClick={this.homePage}>Minigram</span>
                <form className="form-inline my-2 my-lg-0">
                    <input className="form-control mr-sm-2" type="search" placeholder="Account Name" aria-label="Follow Account" onChange={this.updateText}/>
                    <button className="btn btn-outline-info my-2 my-sm-0" onClick={this.followUser}>Follow</button>
                </form>
                <button type="button" className="btn btn-outline-success btn-sm" onClick={this.postHandler}>Post</button>
            </nav>
        </div>
        )
    }
}

export default withRouter(Navbar);
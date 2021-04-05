/*
    Navbar component, should be present in every other component, but LoginPage as Login Page just needs the text from the bar
*/

import React, {Component} from 'react';
import { withRouter } from 'react-router-dom'

class Navbar extends React.Component{
    constructor(props) {
        super(props);

        this.postHandler = this.postHandler.bind(this);
        this.homePage = this.homePage.bind(this);
    }

    postHandler() {
        this.props.history.push("/post")
    }

    homePage() {
        if(this.props.location.pathname === "/profile"){
            this.props.history.push("/feed")
        }else{
            this.props.history.push("/profile")
        }
    }

    render(){
        return(
        <div className="sticky-top">
            <nav className="navbar navbar-light bg-light border-bottom border-dark">
                <span className="navbar-text headerText text-dark" onClick={this.homePage}>Minigram</span>
                <form className="form-inline my-2 my-lg-0">
                    <input className="form-control mr-sm-2" type="search" placeholder="Account Name" aria-label="Follow Account"/>
                    <button className="btn btn-outline-info my-2 my-sm-0">Follow</button>
                </form>
                <button type="button" className="btn btn-outline-success btn-sm" onClick={this.postHandler}>Post</button>
            </nav>
        </div>
        )
    }
}

export default withRouter(Navbar);
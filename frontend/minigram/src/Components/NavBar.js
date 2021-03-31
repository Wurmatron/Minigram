/*
    Navbar component, should be present in every other component, but LoginPage as Login Page just needs the text from the bar
*/

import React, {Component} from 'react';
import { withRouter } from 'react-router-dom'

class Navbar extends React.Component{
    constructor(props) {
        super(props);

        this.postHandler = this.postHandler.bind(this);
    }

    postHandler() {
        this.props.history.push("/post")
    }

    render(){
        return(
        <div className="sticky-top">
            <nav className="navbar navbar-light bg-light border-bottom border-dark">
                <span className="navbar-text headerText text-dark">Minigram</span>
                <button type="button" className="btn btn-outline-success btn-sm" onClick={this.postHandler}>Post</button>
            </nav>
        </div>
        )
    }
}

export default withRouter(Navbar);
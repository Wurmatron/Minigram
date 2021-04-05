import React, {Component} from 'react';
import { withRouter } from 'react-router-dom'
import axios from "axios";
import '../App.css'
import Navbar from './NavBar'



class Feed extends React.Component{
    constructor(props){
        super(props)
    }


    render(){
        return(
            <div>
                <Navbar/>
                <h1>
                    Hello World
                </h1>
            </div>
            )
    }
}

export default Feed;
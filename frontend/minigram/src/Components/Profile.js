/*
    Component for an indivdual users profile
*/

import React, {Component} from 'react';
import { withRouter } from 'react-router-dom'
import axios from "axios";
import '../App.css'
import Navbar from './NavBar'
import testImg from '../testImg.webp'
import profilePic from '../profilePic.jpg'

class Profile extends React.Component {
    constructor(props){
        super(props)

        this.state = {
            profName : " ",
            postTotal : " ",
            followerTotal : " ",
            followingTotal : " ",
            posts : [],
        }

        this.setUpNameAndFollowing = this.setUpNameAndFollowing.bind(this);
    }

    //Sets up profName and followingTotal from call to get account data
    setUpNameAndFollowing = (response) => {
        this.setState({profName : response.data.name})
        //No following still has a length of 1 so check if first is empty instead of just using length
        if(response.data.following_ids[0] === ""){
            this.setState({followingTotal : "0"})
        }else{
            this.setState({followingTotal : response.data.following_ids.length})
        }
    }

    setUpFollwers = (response) => {
        this.setState({followerTotal : response.data.length})
    }

    setUpPosts = (posts) => {
        const half = Math.ceil(posts.length / 2)
        const firstHalf = posts.splice(0 , half)
        const secHalf = posts.splice(-half)

        let firstRow = []
        let secRow =[]

        firstHalf.forEach(post => {
            let base64 = "data:image/png;base64," + post.image
            firstRow.push(
                <div className="col-sm text-center my-2">
                    <img src={base64}  className="profileImgs"/>
                </div>
            )
        })

        secHalf.forEach(post => {
            let base64 = "data:image/png;base64," + post.image
            secRow.push(
                <div className="col-sm text-center my-2">
                    <img src={base64}  className="profileImgs"/>
                </div>
            ) 
        })

        this.setState({
            firstRow : firstRow,
            secRow : secRow,
        })

    }

    //This runs after the profile component receives its props from App.js
    componentDidUpdate(prevProps){
        if(this.props != prevProps && this.props.loggedId !== ""){
                let url = 'http://localhost:8080/account/' + this.props.loggedId;
                let self = this;
                axios.get(url , {id: this.props.loggedId})
                    .then(function (response){
                        if(response.status === 200){
                            self.setUpNameAndFollowing(response);
                        }
                    })
                    .catch(function (error){
                        console.log(error)
                    });
                url ='http://localhost:8080/accounts/' + this.props.loggedId + "/followers"
                axios.get(url , {id : this.props.loggedId , token : this.props.sesToken})
                    .then(function (response){
                        if(response.status === 200){
                            self.setUpFollwers(response);
                        }
                    })
                    .catch(function (error){
                        console.log(error);
                    });
                url = 'http://localhost:8080/posts?accountID=' + this.props.loggedId
                axios.get(url)
                    .then(function (response){
                        if(response.status === 200){
                            self.setUpPosts(response.data.data);
                        }
                    })
                    .catch(function (error){
                        console.log(error);
                    })
        }
    }

    componentDidMount(){
        if(this.props.loggedId !== ""){
            let url = 'http://localhost:8080/account/' + this.props.loggedId;
                let self = this;
                axios.get(url , {id: this.props.loggedId})
                    .then(function (response){
                        if(response.status === 200){
                            self.setUpNameAndFollowing(response);
                        }
                    })
                    .catch(function (error){
                        console.log(error)
                    });
                url ='http://localhost:8080/accounts/' + this.props.loggedId + "/followers"
                axios.get(url , {id : this.props.loggedId , token : this.props.sesToken})
                    .then(function (response){
                        if(response.status === 200){
                            self.setUpFollwers(response);
                        }
                    })
                    .catch(function (error){
                        console.log(error);
                    });
                url = 'http://localhost:8080/posts?accountID=' + this.props.loggedId
                axios.get(url)
                    .then(function (response){
                        if(response.status === 200){
                            self.setUpPosts(response.data.data);
                        }
                    })
                    .catch(function (error){
                        console.log(error);
                    })
        }
    }

    render() {
        return( 
            <div>
                <Navbar/>
                <div className="container">
                    <div className="row align-items-center">
                        <div className="align-left">
                            <img src={profilePic} className="profilePic float-left" />
                        </div>
                        <div className="row ml-1 align-items-center justify-content-center">
                            <p className="h2 ml-2">{this.state.profName}</p>
                            <button className="btn btn-primary btn-sm mx-5">Follow</button>
                            <p className="mx-3">{this.state.followerTotal} Followers</p>
                            <p className="mx-3">{this.state.followingTotal + " Following"}</p>
                        </div>
                    </div>

                </div>
                <div className="container h-100 justify-content-center align-items-center">
                    <div className="row mt-5">
                        {this.state.firstRow}
                        <div className="w-100 my-5"></div>
                        {this.state.secRow}
                    </div>
                </div>
            </div>
        )
    }
}

export default Profile;
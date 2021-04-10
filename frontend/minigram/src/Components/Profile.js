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
        }

        this.setUpNameAndFollowing = this.setUpNameAndFollowing.bind(this);
        this.isFollowing = this.isFollowing.bind(this);
        this.followUnfollow = this.followUnfollow.bind(this);
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
        this.setState({
            followerTotal : response.data.length,
            followers : response.data
        })
    }

    setUpPosts = (posts) => {
        if(this.props.loggedId !== this.props.profileToGoTo){
            posts = posts.data.filter(post => post.posted_by_id === this.props.profileToGoTo)
        }
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

    isFollowing = async () => {
        if(this.state.followers === [] || this.state.followers === undefined){
            return false;
        }else{
            this.state.followers.forEach(follower => {
                if(follower.following_ids.includes(this.props.profileToGoTo))
                    return true;
            })
        }
        return false;
    }

    followUnfollow = async (e) => {
        if(e.target.innerHTML === "Following"){
            e.target.classList.remove("active")
            e.target.innerHTML = "Follow"
            let url = "http://localhost:8080/accounts/unfollow/" + this.props.profileToGoTo
            await axios.post(url , {id : this.props.profileToGoTo} ,  {headers : {accept: "application/json" , 'token' : this.props.sesToken}})
                .then(function(response){
                    console.log(response)
                })
        }else{
            e.target.classList.add("active")
            e.target.innerHTML = "Following"
            let url = "http://localhost:8080/accounts/follow/" + this.props.profileToGoTo;
            await axios.post(url , {id : this.props.profileToGoTo} , {headers : {accept: "application/json" , 'token' : this.props.sesToken}})
                .then(function(response){
                    console.log(response)
                })
        }
    }

    //This runs after the profile component receives its props from App.js
    componentDidUpdate(prevProps){
        if(this.props != prevProps && this.props.loggedId !== ""){
                let url = ""
                let ownProfile = false;
                let self = this;
                if(this.props.loggedId === this.props.profileToGoTo){
                    url = 'http://localhost:8080/account/' + this.props.loggedId;
                    ownProfile = true
                    axios.get(url , {id: this.props.loggedId})
                    .then(function (response){
                        if(response.status === 200){
                            self.setUpNameAndFollowing(response);
                        }
                    })
                    .catch(function (error){
                        console.log(error)
                    });
                }else{
                    url = 'http://localhost:8080/account/' + this.props.profileToGoTo;
                    axios.get(url , {id: this.props.profileToGoTo})
                    .then(function (response){
                        if(response.status === 200){
                            self.setUpNameAndFollowing(response);
                        }
                    })
                    .catch(function (error){
                        console.log(error)
                    });
                }
                if(ownProfile){
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
                    url = 'http://localhost:8080/feed?followers=false';
                    axios.get(url , {headers: {'token' : this.props.sesToken , accept: "application/json"}})
                        .then(function (response){
                            if(response.status === 201){
                                self.setUpPosts(response.data);
                            }
                        })
                        .catch(function (error){
                            console.log(error);
                        })
                }else{
                    url ='http://localhost:8080/accounts/' + this.props.profileToGoTo + "/followers"
                    axios.get(url , {id : this.props.profileToGoTo , token : this.props.sesToken})
                        .then(function (response){
                            if(response.status === 200){
                                self.setUpFollwers(response);
                            }
                        })
                        .catch(function (error){
                            console.log(error);
                        });
                    url = 'http://localhost:8080/posts/';
                    axios.get(url , {headers: {'token' : this.props.sesToken , accept: "application/json"}})
                        .then(function (response){
                            if(response.status === 200){
                                self.setUpPosts(response.data);
                            }
                        })
                        .catch(function (error){
                            console.log(error);
                        })
                }
                
        }
    }

    componentDidMount(){
        if(this.props.loggedId !== ""){
            let ownProfile = this.props.loggedId === this.props.profileToGoTo;
            if(ownProfile){
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
                    url = 'http://localhost:8080/feed?followers=false'
                    axios.get(url , {headers: {'token' : this.props.sesToken , accept: "application/json"}})
                        .then(function (response){
                            if(response.status === 201){
                                self.setUpPosts(response.data);
                            }
                        })
                        .catch(function (error){
                            console.log(error);
                        })
            }else{
                let url = 'http://localhost:8080/account/' + this.props.profileToGoTo;
                    let self = this;
                    axios.get(url , {id: this.props.profileToGoTo})
                        .then(function (response){
                            if(response.status === 200){
                                self.setUpNameAndFollowing(response);
                            }
                        })
                        .catch(function (error){
                            console.log(error)
                        });
                    url ='http://localhost:8080/accounts/' + this.props.profileToGoTo + "/followers"
                    axios.get(url , {id : this.props.profileToGoTo , token : this.props.sesToken})
                        .then(function (response){
                            if(response.status === 200){
                                self.setUpFollwers(response);
                            }
                        })
                        .catch(function (error){
                            console.log(error);
                        });
                    url = 'http://localhost:8080/posts/'
                    axios.get(url , {headers: {'token' : this.props.sesToken , accept: "application/json"}})
                        .then(function (response){
                            if(response.status === 200){
                                self.setUpPosts(response.data);
                            }
                        })
                        .catch(function (error){
                            console.log(error);
                        })
            }
        }
    }


    render() {
        if(this.props.loggedId === this.props.profileToGoTo){
            return( 
                <div>
                    <Navbar setProfile = {this.props.setProfile} loggedId={this.props.loggedId}/>
                    <div className="container">
                        <div className="row align-items-center">
                            <div className="align-left">
                                <img src={profilePic} className="profilePic float-left" />
                            </div>
                            <div className="row ml-1 align-items-center justify-content-center">
                                <p className="h2 ml-2">{this.state.profName}</p>
                                <button className="btn btn-primary btn-sm mx-5" disabled>Following</button>
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
        }else{
            if(this.isFollowing()){
                return( 
                    <div>
                        <Navbar setProfile = {this.props.setProfile} loggedId={this.props.loggedId}/>
                        <div className="container">
                            <div className="row align-items-center">
                                <div className="align-left">
                                    <img src={profilePic} className="profilePic float-left" />
                                </div>
                                <div className="row ml-1 align-items-center justify-content-center">
                                    <p className="h2 ml-2">{this.state.profName}</p>
                                    <button className="btn btn-outline-primary active btn-sm mx-5" onClick={this.followUnfollow}>Following</button>
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
            }else{
                return( 
                    <div>
                        <Navbar setProfile = {this.props.setProfile} loggedId={this.props.loggedId}/>
                        <div className="container">
                            <div className="row align-items-center">
                                <div className="align-left">
                                    <img src={profilePic} className="profilePic float-left" />
                                </div>
                                <div className="row ml-1 align-items-center justify-content-center">
                                    <p className="h2 ml-2">{this.state.profName}</p>
                                    <button className="btn btn-outline-primary btn-sm mx-5" onClick={this.followUnfollow}>Follow</button>
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
    }
}

export default Profile;
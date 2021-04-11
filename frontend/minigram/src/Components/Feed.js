import React, {Component} from 'react';
import { withRouter } from 'react-router-dom'
import axios from "axios";
import '../App.css'
import Navbar from './NavBar'



class Feed extends React.Component{
    constructor(props){
        super(props)

        this.state = {
            feedPosts : [],
            lastPostedBy : " ",
            likedBy : [],
            caption : " ",
        };

        this.likeHandler = this.likeHandler.bind(this);
        this.goToProfile = this.goToProfile.bind(this);
    }

    //Get the post first then edit the likedID to add or remove the loggedIn user then put with the update likes
    likeHandler = async (e) => {
        let postID = e.target.id;
        const post = await axios.get('http://localhost:8080/posts/' + postID , {headers: {'token' : this.props.sesToken}})
            .then(function(response){
                return response.data
            })
            .catch(function (error){
                console.log(error);
            });
        let postData = post.data;
        
        if(e.target.classList.contains("active")){
            e.target.classList.remove("active")
            e.target.innerHTML = "Like"
            //TODO add unlike behavior here
            postData.likes_ids[0] = postData.likes_ids[0].replaceAll(this.props.loggedId , "");
            if(postData.likes_ids === []){
                postData.likes_ids.push(" ")
            }
            await axios.put('http://localhost:8080/posts/' + postData.id , {id: postData.id , likes_ids: postData.likes_ids} , {headers: {'token' : this.props.sesToken}})
        }else{
            e.target.classList.add("active")
            e.target.innerHTML = "Liked"
            postData.likes_ids[0] = postData.likes_ids[0] + this.props.loggedId;
            await axios.put('http://localhost:8080/posts/' + postData.id , {id: postData.id , likes_ids: postData.likes_ids} , {headers: {'token' : this.props.sesToken}})
                .catch(function (error){
                    console.log(error)
                })
        }
    }

    goToProfile = (e) => {
        this.props.setProfile(e.target.id);
        this.props.history.push("/profile")
    }

    async componentDidMount(){
        if(this.props.loggedId !== ""){
            let url = "http://localhost:8080/feed?followers=true";
            let self = this;
            const posts = await axios.get(url ,  {headers: {'token' : this.props.sesToken}})
                .then(function (response){
                    return response.data
                })
                .catch(function (error){
                    console.log(error)
                })
            
            let postArray = [];
            for(const post of posts){
                let base64 = "data:image/png;base64," + post.image;
                let self = this;
                let response = await axios.get("http://localhost:8080/account/" + post.posted_by_id , {headers: {'token' : this.props.sesToken}})
                    .then( function (response){
                        self.setState({ 
                            lastPostedBy : response.data.name ,
                            likedBy : response.data.likes_ids,
                            caption : response.data.text,
                            postId : response.data.postID,
                            })
                    })
                if(!post.likes_ids[0].includes(this.props.loggedId)){
                    postArray.push(
                        <div className="card my-5 mx-auto text-center feedCards">
                            <div className="card-header" id={post.posted_by_id} onClick={this.goToProfile}>
                                    {this.state.lastPostedBy}
                            </div>
                            <img className="card-img-top feedImgs" src={base64} />
                            <div className="card-body">
                                <p className="card-text text-center mt-2">{post.text}</p>
                                <button className="btn btn-outline-danger btn-sm" id={post.postID} onClick={this.likeHandler}>Like</button>
                            </div>
                        </div>            
                    )
                }else{
                    postArray.push(
                        <div className="card my-5 mx-auto text-center feedCards">
                            <div className="card-header" id={post.posted_by_id} onClick={this.goToProfile}>
                                    {this.state.lastPostedBy}
                            </div>
                            <img className="card-img-top feedImgs" src={base64} />
                            <div className="card-body">
                                <p className="card-text text-center mt-2">{post.text}</p>
                                <button className="btn btn-outline-danger btn-sm active" id={post.postID} onClick={this.likeHandler}>Liked</button>
                            </div>
                        </div>            
                    )
                }
                
            }
            this.setState({feedPosts : postArray})
        }
    }

    render(){

        if(this.state != null){
            return(
                <div>
                    <Navbar setProfile = {this.props.setProfile} loggedId={this.props.loggedId} sesToken = {this.props.sesToken}/>
                    <div className="container h-100 justify-content-center align-items-center">
                        <div className="col-lg justify-content-center align-items-center">
                            {this.state.feedPosts}
                        </div>
                    </div>
                </div>
            )
        }else{
            return(
                <div>
                    <Navbar setProfile = {this.props.setProfile} loggedId={this.props.loggedId} sesToken = {this.props.sesToken}/>
                    <div className="container h-100 justify-content-center align-items-center">

                    </div>
                </div>
            )
        }
        
    }
}

export default withRouter(Feed);
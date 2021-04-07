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

        
    }

    async componentDidMount(){
        if(this.props.loggedId !== ""){
            let url = "http://localhost:8080/feed";
            let self = this;
            const posts = await axios.get(url ,  {headers: {'token' : this.props.sesToken}} , {start: 0 , end: 20})
                .then(function (response){
                    console.log(response)
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
                            })
                    })
                if(!post.likes_ids.includes(this.props.loggedId)){
                    postArray.push(
                        <div className="card my-5 mx-auto text-left feedCards">
                            <div className="card-header">
                                    {this.state.lastPostedBy}
                            </div>
                            <img className="card-img-top feedImgs" src={base64} />
                            <div className="card-body">
                                <p className="card-text text-center mt-2">{post.text}</p>
                                <button className="btn btn-outline-danger btn-sm ">Like</button>
                            </div>
                        </div>            
                    )
                }else{
                    postArray.push(
                        <div className="card my-5 mx-auto text-center feedCards">
                            <div className="card-header">
                                    {this.state.lastPostedBy}
                            </div>
                            <img className="card-img-top feedImgs" src={base64} />
                            <div className="card-body">
                                <p className="card-text text-center mt-2">{post.text}</p>
                                <button className="btn btn-outline-danger btn-sm active">Liked</button>
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
                    <Navbar/>
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
                    <Navbar/>
                    <div className="container h-100 justify-content-center align-items-center">

                    </div>
                </div>
            )
        }
        
    }
}

export default Feed;
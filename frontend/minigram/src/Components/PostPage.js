/*
    Componenet that handles the creation and upload of posts
*/

import React, {Component} from 'react';
import axios from "axios";
import { withRouter } from 'react-router-dom'
import '../App.css'
import Navbar from './NavBar'

class PostPage extends React.Component{
    constructor(props){
        super(props)

        this.state = {
            postText : '',
            image : "",
        };

        this.changeHandler = this.changeHandler.bind(this);
        this.imageHandler = this.imageHandler.bind(this);
        this.uploadPost = this.uploadPost.bind(this);
    }

    successfulUpload(){
        this.props.history.push("/feed")
    }

    uploadPost = () => {
        let self = this;
        const response = axios.post('http://localhost:8080/posts' , {text: this.state.postText , image: this.state.image , posted_by_id: this.props.loggedId})
            .then(function (response){
                if(response.status == 201){
                    self.successfulUpload()
                }
            })
            .catch(function (error){
                console.log(error)
            });
    }

    imageHandler = (e) => {
        let self = this;
        console.log(e.target.files[0])
        let reader = new FileReader();
        reader.readAsDataURL(e.target.files[0])
        reader.onload = function () {
            console.log(reader.result)
            let base64Img = reader.result.split(',')[1];
            self.setState({image : base64Img});
        }
    }


    //Updates state as user enters in on a form
    changeHandler = (e) => {
        let name = e.target.id;
        let val = e.target.value;
        this.setState({[name] : val});
    }

    render(){
        return(
            <div>
                <Navbar setProfile = {this.props.setProfile} loggedId={this.props.loggedId}/>
                <div className="container h-100 mt-2">
                    <div className="row h-100 justify-content-center align-items-center">
                        <form className="col-12">
                            <div className="form-group">
                                <label for="image">Post Image</label>
                                <input type="file" className="form-control-file" id="image" onChange={this.imageHandler}/>
                            </div>
                            <div className="form-group">
                                <label for="postText">Caption</label>
                                <input type="text" class="form-control" id="postText" onChange={this.changeHandler}/>
                            </div>
                            <button type="button" class="btn btn-primary" onClick={this.uploadPost}>Post</button>
                        </form>
                    </div>
                </div>
            </div>
        )
        
    }

}

export default withRouter(PostPage);
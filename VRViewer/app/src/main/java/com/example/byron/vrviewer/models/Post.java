package com.example.byron.vrviewer.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

/**
 * Created by Byron on 11/24/2016.
 */

public class Post {

    private String title;
    private String description;
    private String username;
    private String imageLink;
    private int likes = 0;
    @Exclude
    private String postRef;

    public Post() {
    }

    public Post(String title, String description, String username, String imageLink) {
        this.title = title;
        this.description = description;
        this.username = username;
        this.imageLink = imageLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getPostRef() {
        return postRef;
    }

    public void setPostRef(String postRef) {
        this.postRef = postRef;
    }
}

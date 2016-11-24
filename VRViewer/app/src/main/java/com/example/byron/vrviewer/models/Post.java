package com.example.byron.vrviewer.models;

/**
 * Created by Byron on 11/24/2016.
 */

public class Post {

    public String title;
    public String description;
    public String username;
    public String imageLink;
    public int likes = 0;

    public Post(){

    }

    public Post(String title, String description, String username, String imageLink) {
        this.title = title;
        this.description = description;
        this.username = username;
        this.imageLink = imageLink;
    }
}

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
}

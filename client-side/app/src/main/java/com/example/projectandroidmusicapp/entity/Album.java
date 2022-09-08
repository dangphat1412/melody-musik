package com.example.projectandroidmusicapp.entity;

import java.io.Serializable;
import java.util.HashMap;

public class Album implements Serializable {
    private String id;
    private String albumName;
    private String urlImage;
    private HashMap<String, Song> songs;

    public Album() {
    }

    public Album(String id, String albumName, String urlImage, HashMap<String, Song> songs) {
        this.id = id;
        this.albumName = albumName;
        this.urlImage = urlImage;
        this.songs = songs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public HashMap<String, Song> getSongs() {
        return songs;
    }

    public void setSongs(HashMap<String, Song> songs) {
        this.songs = songs;
    }
}

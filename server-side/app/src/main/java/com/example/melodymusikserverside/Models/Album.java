package com.example.melodymusikserverside.Models;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Album {
    public String id;
    public String albumName;
    public String urlImage;
    public HashMap<String, Song> songs;

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

    public Album() {
    }

    public Album(String id, String albumName, String urlImage, HashMap<String, Song> songs) {
        this.id = id;
        this.albumName = albumName;
        this.urlImage = urlImage;
        this.songs = songs;
    }
}

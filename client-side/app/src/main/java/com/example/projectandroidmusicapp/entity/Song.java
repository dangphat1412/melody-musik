package com.example.projectandroidmusicapp.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Song implements Serializable, Comparable<Song>{
    private String id;
    private String title;
    private HashMap<String,Artist> artists;
    private String urlBanner;
    private String urlImage;
    private String urlSong;
    private String category;

    public Song() {
    }

    public Song(String id, String title, HashMap<String,Artist> artists, String urlBanner, String urlImage, String urlSong, String category) {
        this.id = id;
        this.title = title;
        this.artists = artists;
        this.urlBanner = urlBanner;
        this.urlImage = urlImage;
        this.urlSong = urlSong;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HashMap<String,Artist> getArtists() {
        return artists;
    }

    public void setArtists(HashMap<String,Artist> artists) {
        this.artists = artists;
    }

    public String getUrlBanner() {
        return urlBanner;
    }

    public void setUrlBanner(String urlBanner) {
        this.urlBanner = urlBanner;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getUrlSong() {
        return urlSong;
    }

    public void setUrlSong(String urlSong) {
        this.urlSong = urlSong;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int compareTo(Song o) {
        return o.getId().compareTo(this.getId());
    }
}

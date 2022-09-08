package com.example.melodymusikserverside.Models;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class Song {
    public String id;
    public String title;
    public HashMap<String, Artist> artists;
    public String urlSong;
    public String urlImage;
    public String urlBanner;
    public String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HashMap<String, Artist> getArtists() {
        return artists;
    }

    public void setArtists(HashMap<String, Artist> artists) {
        this.artists = artists;
    }

    public String getUrlSong() {
        return urlSong;
    }

    public void setUrlSong(String urlSong) {
        this.urlSong = urlSong;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getUrlBanner() {
        return urlBanner;
    }

    public void setUrlBanner(String urlBanner) {
        this.urlBanner = urlBanner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Song(String id, String title, HashMap<String, Artist> artists, String urlSong, String urlImage, String urlBanner, String category) {
        this.id = id;
        this.title = title;
        this.artists = artists;
        this.urlSong = urlSong;
        this.urlImage = urlImage;
        this.urlBanner = urlBanner;
        this.category = category;
    }

    public Song() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song song = (Song) o;
        return Objects.equals(id, song.id) && Objects.equals(getTitle(), song.getTitle()) && Objects.equals(getArtists(), song.getArtists()) && Objects.equals(getUrlSong(), song.getUrlSong()) && Objects.equals(getUrlImage(), song.getUrlImage()) && Objects.equals(getUrlBanner(), song.getUrlBanner()) && Objects.equals(getCategory(), song.getCategory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getTitle(), getArtists(), getUrlSong(), getUrlImage(), getUrlBanner(), getCategory());
    }
}

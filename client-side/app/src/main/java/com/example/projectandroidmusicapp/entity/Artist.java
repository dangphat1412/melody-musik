package com.example.projectandroidmusicapp.entity;

import java.io.Serializable;
import java.util.Objects;

public class Artist implements Serializable {
    private String id;
    private String name;
    private String urlImage;

    public Artist() {
    }

    public Artist(String id, String name, String urlImage) {
        this.id = id;
        this.name = name;
        this.urlImage = urlImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artist)) return false;
        Artist artist = (Artist) o;
        return Objects.equals(name, artist.name) && Objects.equals(urlImage, artist.urlImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, urlImage);
    }
}

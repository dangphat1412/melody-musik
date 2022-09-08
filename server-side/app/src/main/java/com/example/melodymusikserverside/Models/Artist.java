package com.example.melodymusikserverside.Models;

import java.util.Objects;

public class Artist {
    public String id;
    public String name;
    public String urlImage;

    public Artist(String id, String name, String urlImage) {
        this.id = id;
        this.name = name;
        this.urlImage = urlImage;
    }

    public Artist() {

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artist)) return false;
        Artist artist = (Artist) o;
        return Objects.equals(getId(), artist.getId()) && Objects.equals(getName(), artist.getName()) && Objects.equals(getUrlImage(), artist.getUrlImage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getUrlImage());
    }
}

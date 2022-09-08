package com.example.melodymusikserverside.Models;

public class Catogories {
    private String name;
    private String urlImage;

    public Catogories(String name, String urlImage) {
        this.name = name;
        this.urlImage = urlImage;
    }

    public Catogories() {

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
}

package com.example.projectandroidmusicapp.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Playlist implements Serializable {
    private String id;
    private String name;
    private HashMap<String, Song> songs;
    private HashMap<String, User> users;

    public Playlist() {
    }

    public Playlist(String id, String name, HashMap<String, Song> songs, HashMap<String, User> users) {
        this.id = id;
        this.name = name;
        this.songs = songs;
        this.users = users;
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

    public HashMap<String, Song> getSongs() {
        return songs;
    }

    public void setSongs(HashMap<String, Song> songs) {
        this.songs = songs;
    }

    public HashMap<String, User> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, User> users) {
        this.users = users;
    }
}

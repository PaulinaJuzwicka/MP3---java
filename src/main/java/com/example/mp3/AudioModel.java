package com.example.mp3;

import java.io.Serializable;

public class AudioModel implements Serializable {
    private String path;
    private String title;
    private String duration;

    public AudioModel(String path, String title, String duration) {
        this.path = path;
        this.title = title;
        this.duration = duration;
    }

    public AudioModel(String title, long id, String artist, long duration) {
        this.title = title;
        this.duration = String.valueOf(duration);
        // Jeśli masz pola `path` lub inne, które chcesz uzupełnić, zrób to tutaj
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }
}

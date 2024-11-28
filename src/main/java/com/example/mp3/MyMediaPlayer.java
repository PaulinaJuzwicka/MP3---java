package com.example.mp3;

import android.media.MediaPlayer;

public class MyMediaPlayer {
    public static int currentIndex = -1;
    static MediaPlayer instance;

    public static MediaPlayer getInstance() {
        if (instance == null) {
            instance = new MediaPlayer();
        }
        return instance;
    }
}

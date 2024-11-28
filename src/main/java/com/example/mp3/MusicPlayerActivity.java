package com.example.mp3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {
    private static final String CURRENT_SONG_INDEX_KEY = "current_song_index";
    private static final String CURRENT_POSITION_KEY = "current_position";

    private TextView titleTv, currentTimeTv, totalTimeTv;
    private SeekBar seekBar;
    private ImageView pausePlay, nextBtn, previousBtn, musicIcon;
    private ArrayList<AudioModel> songsList;
    private AudioModel currentSong;
    private MediaPlayer mediaPlayer;
    private int x = 0;
    private int currentSongPosition = 0;

    @SuppressLint("DefaultLocale")
    public static String convertToMMSS(String duration) {
        long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);

        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        if (savedInstanceState != null) {
            int savedIndex = savedInstanceState.getInt(CURRENT_SONG_INDEX_KEY, -1);
            if (savedIndex != -1) {
                MyMediaPlayer.currentIndex = savedIndex;
            }
            currentSongPosition = savedInstanceState.getInt(CURRENT_POSITION_KEY, 0);
        }

        setResourcesWithMusic();

        runOnUiThread(new Runnable() {
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    currentTimeTv.setText(convertToMMSS(String.valueOf(currentPosition)));

                    pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                    musicIcon.setRotation(x++);
                } else {
                    pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                    musicIcon.setRotation(0);
                }

                runOnUiThread(this);
            }
        });

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MusicPlayerActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Dodajemy obserwatora, który będzie nasłuchiwał zmian rozmiaru pola tytułu
        titleTv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                // Sprawdzamy, czy tekst jest za długi, aby zmieścić się w polu tytułu
                if (isTextOverflowing(titleTv)) {
                    // Rozpoczynamy animację przesuwania tekstu
                    startTextScrollAnimation(titleTv);
                } else {
                    // Wyłączamy animację przesuwania tekstu, jeśli nie jest potrzebna
                    stopTextScrollAnimation(titleTv);
                }
            }
        });
    }

    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_SONG_INDEX_KEY, MyMediaPlayer.currentIndex);
        outState.putInt(CURRENT_POSITION_KEY, mediaPlayer.getCurrentPosition());
    }

    void setResourcesWithMusic() {
        currentSong = songsList.get(MyMediaPlayer.currentIndex);

        titleTv.setText(currentSong.getTitle());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        pausePlay.setOnClickListener(v -> pausePlay());
        nextBtn.setOnClickListener(v -> playNextSong());
        previousBtn.setOnClickListener(v -> playPreviousSong());

        playMusic();
        mediaPlayer.seekTo(currentSongPosition);
    }

    private void playMusic() {
        mediaPlayer = MyMediaPlayer.getInstance();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playNextSong() {
        if (MyMediaPlayer.currentIndex == songsList.size() - 1)
            return;

        MyMediaPlayer.currentIndex += 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void playPreviousSong() {
        if (MyMediaPlayer.currentIndex == 0)
            return;

        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void pausePlay() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }

    private boolean isTextOverflowing(TextView textView) {
        int lineCount = textView.getLineCount();
        int maxLines = textView.getMaxLines();
        return lineCount > maxLines;
    }

    private void startTextScrollAnimation(final TextView textView) {
        final int SCROLL_SPEED = 1; // Prędkość przesuwania tekstu (w pikselach)
        final int SCROLL_DELAY = 30; // Opóźnienie między kolejnymi krokami animacji (w milisekundach)

        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setMarqueeRepeatLimit(-1);
        textView.setHorizontallyScrolling(true);

        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    textView.post(new Runnable() {
                        public void run() {
                            textView.scrollBy(SCROLL_SPEED, 0);
                        }
                    });
                    try {
                        Thread.sleep(SCROLL_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void stopTextScrollAnimation(TextView textView) {
        textView.setEllipsize(null);
        textView.setMarqueeRepeatLimit(0);
        textView.setHorizontallyScrolling(false);
    }
}

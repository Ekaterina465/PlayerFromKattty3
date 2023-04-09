package com.example.player2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

    public class MainActivity extends AppCompatActivity implements Runnable {

        private MediaPlayer mediaPlayer = null;
        private SeekBar seekBar, seekBarVolume;
        private boolean wasPlaying = false;
        private FloatingActionButton fabPlayPause;
        private TextView seekBarHint;

        AudioManager audioManager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


            fabPlayPause = findViewById(R.id.fabPlayPause);
            seekBarHint = findViewById(R.id.seekBarHint);
            seekBar = findViewById(R.id.seekBar);

            seekBarVolume = findViewById(R.id.seekBarVolume);

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

            int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            seekBarVolume.setMax(maxVolume);
            seekBarVolume.setProgress(curVolume);



            seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    }

            });


            fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));

            fabPlayPause.setOnClickListener(view -> {
                if (getMediaPlayer().isPlaying()) {
                    getMediaPlayer().pause();
                    fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
                } else {
                    getMediaPlayer().seekTo(seekBar.getProgress());
                    getMediaPlayer().start();
                    fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_pause));
                }
            });



            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                    seekBarHint.setVisibility(View.VISIBLE);
                    int seconds = (int) Math.ceil(progress/1000f);

                    long MM = (seconds % 3600) / 60;
                    long SS = seconds % 60;
                    String timeInHHMMSS = String.format("%02d:%02d", MM, SS);
                    seekBarHint.setText(timeInHHMMSS);
                    double percentTrack = progress / (double) seekBar.getMax();
                    seekBarHint.setX(seekBar.getX() + Math.round(seekBar.getWidth()*percentTrack*0.92));

                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    seekBarHint.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (getMediaPlayer() != null && getMediaPlayer().isPlaying()) {
                        getMediaPlayer().seekTo(seekBar.getProgress());
                    }
                }
            });

            getMediaPlayer();
            new Thread(this).start();
        }


        @Override
        protected void onDestroy() {
            super.onDestroy();
            clearMediaPlayer();
        }


        private void clearMediaPlayer() {
            getMediaPlayer().stop();
            getMediaPlayer().release();
            mediaPlayer = null;
        }


        @Override
        public void run() {
            while (true) {
                try {
                    if (getMediaPlayer().isPlaying()) {
                        seekBar.setProgress(getMediaPlayer().getCurrentPosition());
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                } catch (Exception e) {
                    return;
                }
            }
        }


        MediaPlayer getMediaPlayer() {
            if (mediaPlayer==null) {
                try {
                    mediaPlayer = new MediaPlayer();
                    AssetFileDescriptor descriptor = getAssets().openFd("Dominic Fike — 3 Nights (www.lightaudio.ru).mp3");
                    mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                    descriptor.close();

                    mediaPlayer.prepare();
                    mediaPlayer.setLooping(false);
                    seekBar.setMax(mediaPlayer.getDuration());

                } catch (Exception ex) {}
            }
            return mediaPlayer;
        }

    }

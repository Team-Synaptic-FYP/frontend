package com.example.lungsoundclassification;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioPlayer {
    private MediaPlayer mediaPlayer;
    private File tempFile;

    public AudioPlayer(Context context, ByteArrayInputStream byteArrayInputStream) {
        this.mediaPlayer = new MediaPlayer();

        try {
            tempFile = File.createTempFile("temp_audio", ".wav", context.getCacheDir());

            FileOutputStream fos = new FileOutputStream(tempFile);

            // Read data from the ByteArrayInputStream and write to the temp file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = byteArrayInputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
            fos.close();

            mediaPlayer.setDataSource(tempFile.getAbsolutePath());
            mediaPlayer.prepare();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // Play the audio
    public void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    // Pause the audio
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    // Seek to a specific position (in milliseconds)
    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    // Release the MediaPlayer resources
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        // Delete the temporary file
        if (tempFile != null) {
            tempFile.delete();
        }
    }

    // Get the current position of the audio (in milliseconds)
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    // Get the duration of the audio (in milliseconds)
    public int getDuration() {
        return mediaPlayer.getDuration();
    }
}

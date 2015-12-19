package com.example.zhangyao.musicplayer.activity.implement;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.zhangyao.musicplayer.activity.model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyao on 15-12-2.
 */
public class MusicPlayer {

    MediaPlayer player = new MediaPlayer();
    int currentItem = 0;
    ArrayList<String> playList = new ArrayList<>();
    ;

    Context context;
    Cursor cursor;
    Cursor trueCursor;

    public MusicPlayer(Context context) {
        this.context = context;
    }

    public MediaPlayer getPlayer()
    {
        return this.player;
    }

    public ArrayList<String> getPlayList() {

        ContentResolver contentResolver = context.getContentResolver();

        cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

//        assert  cursor != null ;


        if (cursor != null) {
            for (int i=0 ; i < cursor.getCount() ; i++)
            {
                cursor.moveToPosition(i);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(cursor.getString(1)))));
            }
        }


        trueCursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (trueCursor != null)
        {
            trueCursor.moveToFirst();
            playList.add(trueCursor.getString(1));

            while (trueCursor.moveToNext())
            {
                playList.add(trueCursor.getString(1));
            }
        }
        return playList;
    }


    public Song getInformation() {
        Song song = new Song();
        trueCursor.moveToPosition(currentItem);
        song.setName(trueCursor.getString(2).substring(0, trueCursor.getString(2).lastIndexOf(".")));
        song.setSinger(trueCursor.getString(25));

        return song;
    }

    public void play(int position) {

        currentItem = position;
//        Log.i("musicPath", playList.get(position));

        if (player.isPlaying()) {
            player.stop();
        }
        player.reset();

        try {
            player.setDataSource(playList.get(position));
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.start();
        }
    }

    public void playNext() {
//        Log.i("currentItem", String.valueOf(currentItem));

        if (++currentItem >= playList.size()) {
            currentItem = 0;
        }

        play(currentItem);

    }

    public void playLast() {
        if (--currentItem <= 0) {
            currentItem = 0;
        }

        play(currentItem);
    }

    public void destroy() {
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();

        cursor.close();
        trueCursor.close();


    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public int getDuration()
    {
        return player.getDuration();
    }

}

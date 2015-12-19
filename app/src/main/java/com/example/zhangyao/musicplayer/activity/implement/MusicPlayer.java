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

    //新建并实例化播放器
    MediaPlayer player = new MediaPlayer();

    //当前曲目
    int currentItem = 0;

    //音乐播放列表
    ArrayList<String> playList = new ArrayList<>();

    Context context;

    // 存放查询结果集的cursor
    Cursor cursor;

    //存放强制刷新之后的cursor
    Cursor trueCursor;

    //构造方法
    public MusicPlayer(Context context) {
        this.context = context;
    }

    //获取播放器
    public MediaPlayer getPlayer()
    {
        return this.player;
    }

    //获取播放列表
    public ArrayList<String> getPlayList() {

        // ContentResolver 能够实现不同app 之间的数据共享
        ContentResolver contentResolver = context.getContentResolver();

        //查询 MediaStore 数据库 ,获取音乐列表
        cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);



        // 如果查询到,马上要求MediaStore 更新一次
        /*
        * 有用,但是待优化
        * */
        if (cursor != null) {
            for (int i=0 ; i < cursor.getCount() ; i++)
            {
                cursor.moveToPosition(i);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(cursor.getString(1)))));
            }
        }


        //再获取一次播放器列表
        trueCursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        // 如果结果集不空
        if (trueCursor != null)
        {
            // 从cursor 里面获取歌曲名,添加到arrayList
            trueCursor.moveToFirst();
            playList.add(trueCursor.getString(1));

            while (trueCursor.moveToNext())
            {
                playList.add(trueCursor.getString(1));
            }
        }
        return playList;
    }


    // 获取当前播放的曲目的信息
    public Song getInformation() {
        Song song = new Song();
        trueCursor.moveToPosition(currentItem);
        song.setName(trueCursor.getString(2).substring(0, trueCursor.getString(2).lastIndexOf(".")));
        song.setSinger(trueCursor.getString(25));

        return song;
    }

    // 播放
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

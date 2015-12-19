package com.example.zhangyao.musicplayer.activity.model;

/**
 * Created by zhangyao on 15-12-3.
 */
public class Song
{
    public String getName() {
        return name;
    }

    public Song setName(String name) {
        this.name = name;
        return this;
    }

    public String getSinger() {
        return singer;
    }

    public Song setSinger(String singer) {
        this.singer = singer;
        return this;
    }

    private String name ;
    private String singer;
}

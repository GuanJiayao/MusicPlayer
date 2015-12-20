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

    // 歌曲名称
    private String name ;
    //歌手信息
    private String singer;
}

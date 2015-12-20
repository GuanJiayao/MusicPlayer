package com.example.zhangyao.musicplayer.activity.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zhangyao.musicplayer.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by zhangyao on 15-12-1.
 */
public class MusicListAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> data;
    private int currentItem = 0;

    //  构造函数
  //liyunfeng
    public MusicListAdapter(Context context, ArrayList<String> data) {
        this.context = context;
        this.data = data;
    }


    // 获取数据的长度
    /*
    * 自定义adapter 中，重写的方法 ，默认是返回0
    * 但是重写之后一定要更改返回正确的大小，否则无法绘制视图
    * 导致没有显示数据
    *
    * */
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    // 重写的getView 方法
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //定义一个用于显示歌曲名称的textView
        TextView textView ;

        //定义一个LayoutInflater
        LayoutInflater inflater = LayoutInflater.from(context);

        /*
        * 以下判断奇偶行，实现不同的显示效果
        *
        * */

        //如果是偶数行
        if (position%2 == 0) {
            // 绑定布局
            convertView = inflater.inflate(R.layout.layout_music_list, null);
        }
        else
        {
            // 绑定另一布局
            convertView = inflater.inflate(R.layout.layout_music_list_2, null);
        }

        // 获取当前View 的背景
        Drawable drawable = convertView.getBackground();

        Log.i("currentItem & position" ,String.valueOf(currentItem) + "?" + String.valueOf(position));

        // 如果 当前的itemId 跟 position 是一样的
        // 表示选中了这个item 那么改变这个item 的样式
        if (currentItem == position)
        {
            // 设置选中的item 的背景颜色
            convertView.setBackgroundColor(Color.parseColor("#70A23CAC"));
        }
        //否则恢复默认的背景
        else
        {
            convertView.setBackground(drawable);
        }

        //绑定控件
        textView = (TextView) convertView.findViewById(R.id.tx_name);

        // 设置文字信息
        textView.setText(data.get(position));

        //返回最终绘制的视图
        return convertView;
    }


    // 获取当前的item的id
    public void setCurrentItem(int item)
    {
        this.currentItem = item ;
    }



}

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

    public MusicListAdapter(Context context, ArrayList<String> data) {
        this.context = context;
        this.data = data;
    }

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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView textView ;

        LayoutInflater inflater = LayoutInflater.from(context);

        if (position%2 == 0) {
            convertView = inflater.inflate(R.layout.layout_music_list, null);
        }
        else
        {
            convertView = inflater.inflate(R.layout.layout_music_list_2, null);
        }

        Drawable drawable = convertView.getBackground();

        Log.i("currentItem & position" ,String.valueOf(currentItem) + "?" + String.valueOf(position));

        if (currentItem == position)
        {
            convertView.setBackgroundColor(Color.parseColor("#70A23CAC"));
        }
        else
        {
            convertView.setBackground(drawable);
        }

        textView = (TextView) convertView.findViewById(R.id.tx_name);

        textView.setText(data.get(position));

        return convertView;
    }


    public void setCurrentItem(int item)
    {
        this.currentItem = item ;
    }



}

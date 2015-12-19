package com.example.zhangyao.musicplayer.activity.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhangyao.musicplayer.R;
import com.example.zhangyao.musicplayer.activity.adapter.MusicListAdapter;
import com.example.zhangyao.musicplayer.activity.implement.MusicPlayer;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ListView.OnItemClickListener {

    private ListView musicList;
    private ImageView love;
    private ImageView search;
    private ImageView lastOne;
    private ImageView nextOne;
    private ImageView play_Pause;
    private TextView title;
    private TextView singer;
    private EditText keyWords;
    private MusicListAdapter adapter;
    private Chronometer chronometer ;


    private CharSequence chronometerTime ;

    private ArrayList<String> arrayList = new ArrayList<>();

    private ArrayList<String> datas;


    private int currentItem = 0;

    private int fistTime = 1 ;


    private SeekBar seekBar ;


    MusicPlayer musicPlayer = new MusicPlayer(this);

    Handler handler = new Handler();

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {

            Log.i("SystemClock",String.valueOf(SystemClock.elapsedRealtime()));

            seekBar.setMax(musicPlayer.getDuration());

            Log.i("getDuration()", String.valueOf(musicPlayer.getDuration()));

            Log.i("run", String.valueOf(musicPlayer.getPlayer().getCurrentPosition()));

            seekBar.setProgress(musicPlayer.getPlayer().getCurrentPosition());

            handler.postDelayed(thread, 100);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        love = (ImageView) findViewById(R.id.image_love);
        search = (ImageView) findViewById(R.id.image_search);
        lastOne = (ImageView) findViewById(R.id.img_last);
        nextOne = (ImageView) findViewById(R.id.img_next);
        play_Pause = (ImageView) findViewById(R.id.img_play_pause);

        title = (TextView) findViewById(R.id.tx_title);
        singer = (TextView) findViewById(R.id.tx_singer);

        keyWords = (EditText) findViewById(R.id.ed_key_word);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        chronometer = (Chronometer) findViewById(R.id.chronometer);

        chronometer.setFormat("%s");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    musicPlayer.getPlayer().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        datas = musicPlayer.getPlayList();


        for (String s : datas) {
            arrayList.add(s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf(".")));
        }

        adapter = new MusicListAdapter(this, arrayList);

        musicList = (ListView) findViewById(R.id.list_music_list);


        musicList.setAdapter(adapter);

        play_Pause.setOnClickListener(this);
        nextOne.setOnClickListener(this);
        lastOne.setOnClickListener(this);

        musicList.setOnItemClickListener(this);
        love.setOnClickListener(this);
        search.setOnClickListener(this);


        musicPlayer.getPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                seekBar.setProgress(0);
                handler.removeCallbacks(thread);
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
            }
        });


    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_play_pause: {

                if (musicPlayer.isPlaying()) {
                    play_Pause.setImageResource(R.drawable.play);
                    musicPlayer.pause();
                    fistTime = 0 ;
                    handler.removeCallbacks(thread);

                    chronometerTime = chronometer.getText();

                    Log.i("currentTime" , chronometerTime.toString() + "/" + String.valueOf(chronometer.getBase()));

                    chronometer.stop();

                    Log.i("prosess & time" , String.valueOf(seekBar.getProgress()) + "?" + String.valueOf(chronometer.getBase()));


                } else {
                    play_Pause.setImageResource(R.drawable.icon_love);

                    if (fistTime == 1)
                    {
                        musicPlayer.play(0);
                        updateShow();
                        fistTime = 0 ;
                    }
                    else
                    {
                        musicPlayer.pause();
                        handler.post(thread);
                        chronometer.start();
                    }
                }

            break;
        }

        case R.id.img_next: {


            if (++currentItem >= arrayList.size()) {
                currentItem = 0;
            }


            musicPlayer.playNext();
            updateShow();

            break;
        }

        case R.id.img_last: {


            if (--currentItem < 0) {
                currentItem = 0;
            }



            musicPlayer.playLast();

            updateShow();

            break;
        }

        case R.id.image_love: {
//            love.setBackgroundColor(Color.parseColor("#D53E3E"));
//            Intent intent = new Intent();
//            intent.setClass(this, Main2Activity.class);
//
//            startActivity(intent);

            break;
        }
        case R.id.image_search: {
            if (!keyWords.isEnabled()) {
                keyWords.setEnabled(true);
                keyWords.setBackgroundColor(Color.parseColor("#502138EF"));
            } else {
                keyWords.setEnabled(false);
                keyWords.setBackgroundColor(Color.TRANSPARENT);
            }

            break;
        }
    }

}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("click", datas.get(position));

        currentItem = position;
        musicPlayer.play(position);
        updateShow();
    }

    void updateShow() {



        chronometer.stop();

        chronometer.setBase(SystemClock.elapsedRealtime());

        title.setText(musicPlayer.getInformation().getName());
        singer.setText(musicPlayer.getInformation().getSinger());


        play_Pause.setImageResource(R.drawable.icon_love);

        adapter.setCurrentItem(currentItem);
        adapter.notifyDataSetChanged();

        musicList.setSelection(currentItem);


        chronometer.start();
        handler.post(thread);


    }

    long startTime = 0;

    @Override
    public void onBackPressed() {

        long currentTime = System.currentTimeMillis();
        if ((currentTime - startTime) >= 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            startTime = currentTime;
        } else {
            musicPlayer.destroy();
            handler.removeCallbacks(thread);
            finish();
        }
    }
}

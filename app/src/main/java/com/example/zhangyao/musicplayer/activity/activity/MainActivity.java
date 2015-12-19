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


    // 定义控件
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


    // 新建计时器
    private CharSequence chronometerTime ;

    //ListView 的数据
    private ArrayList<String> arrayList = new ArrayList<>();

    //从 MediaStore 获取的歌曲列表
    private ArrayList<String> datas;

    //当前播放的曲目
    private int currentItem = 0;

    //是否是第一次播放
    private int fistTime = 1 ;


    //新建进度条
    private SeekBar seekBar ;


    //实例化自定义的MusicPlayer 对象
    MusicPlayer musicPlayer = new MusicPlayer(this);

    //新建Handler
    Handler handler = new Handler();

    //新建线程 , 更新进度条的进度
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {

            Log.i("SystemClock",String.valueOf(SystemClock.elapsedRealtime()));

            // 设置进度条的最大值, 必须设置,否者是不会动的
            seekBar.setMax(musicPlayer.getDuration());

            Log.i("getDuration()", String.valueOf(musicPlayer.getDuration()));

            Log.i("run", String.valueOf(musicPlayer.getPlayer().getCurrentPosition()));

            // 设置进度条的进度是在播放的音乐的进度
            seekBar.setProgress(musicPlayer.getPlayer().getCurrentPosition());

            //每100毫秒执行一次
            handler.postDelayed(thread, 100);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 绑定控件
        love = (ImageView) findViewById(R.id.image_love);
        search = (ImageView) findViewById(R.id.image_search);
        lastOne = (ImageView) findViewById(R.id.img_last);
        nextOne = (ImageView) findViewById(R.id.img_next);
        play_Pause = (ImageView) findViewById(R.id.img_play_pause);

        title = (TextView) findViewById(R.id.tx_title);
        singer = (TextView) findViewById(R.id.tx_singer);

        keyWords = (EditText) findViewById(R.id.ed_key_word);

        musicList = (ListView) findViewById(R.id.list_music_list);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        chronometer = (Chronometer) findViewById(R.id.chronometer);

        //设置计时器的显示格式:MM:SS
        chronometer.setFormat("%s");

        // 设置进度条进度改变的事件
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            //进度改变,歌曲也从当前的进度播放
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

        // 调用方法,返回获取到的播放列表
        datas = musicPlayer.getPlayList();


        //遍历list 过滤掉文件路径和后缀,只留下文件名,之后添加到arrayList 显示出来
        for (String s : datas) {
            arrayList.add(s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf(".")));
        }

        //实例化 adapter
        adapter = new MusicListAdapter(this, arrayList);

        //给ListView 设置adapter
        musicList.setAdapter(adapter);



        //添加监听
        play_Pause.setOnClickListener(this);
        nextOne.setOnClickListener(this);
        lastOne.setOnClickListener(this);

        musicList.setOnItemClickListener(this);
        love.setOnClickListener(this);
        search.setOnClickListener(this);


        // 监听播放结束的事件
        musicPlayer.getPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                // 设置进度 0
                seekBar.setProgress(0);

                // 结束进程
                handler.removeCallbacks(thread);

                //停止计时器计时
                chronometer.stop();

                //计时器清零
                chronometer.setBase(SystemClock.elapsedRealtime());
            }
        });


    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            // 播放/暂停按钮
            case R.id.img_play_pause: {

                //判断是否正在播放
                if (musicPlayer.isPlaying()) {
                    // 显示播放按钮
                    play_Pause.setImageResource(R.drawable.play);

                    // 暂停播放
                    musicPlayer.pause();

                    //不是第一次播放
                    fistTime = 0 ;

                    //停止线程
                    handler.removeCallbacks(thread);

//                    chronometerTime = chronometer.getText();
//
//                    Log.i("currentTime" , chronometerTime.toString() + "/" + String.valueOf(chronometer.getBase()));
//
//                    chronometer.stop();
//
//                    Log.i("prosess & time" , String.valueOf(seekBar.getProgress()) + "?" + String.valueOf(chronometer.getBase()));


                }
                //没有在播放
                else {
                    //显示暂停按钮
                    play_Pause.setImageResource(R.drawable.icon_love);

                    //如果是第一次播放
                    if (fistTime == 1)
                    {
                        //播放第一首歌曲
                        musicPlayer.play(0);
                        updateShow();
                        fistTime = 0 ;
                    }
                    else
                    {
                        //继续播放当前曲目
                        musicPlayer.pause();
                        //开始线程
                        handler.post(thread);
                        //开始计时

                        /*
                        * 这个地方还不对
                        * 调用chronometer.stop()方法之后,计时器还在后台计时,只是停止了在前台的显示,待解决
                        * */
                        chronometer.start();
                    }
                }

            break;
        }
            //播放下一曲
        case R.id.img_next: {


            // 貌似这个地方没哈用
            //如果已经是最后一首歌
            if (++currentItem >= arrayList.size()) {
                //跳回到第一首
                currentItem = 0;
            }


            // 播放下一首
            musicPlayer.playNext();

            //更改显示
            updateShow();

            break;
        }

        //播放上一曲
        case R.id.img_last: {

            // 貌似这个地方没哈用
            //如果已经是第一首歌
            if (--currentItem < 0) {
                currentItem = 0;
            }

            //播放上一首
            musicPlayer.playLast();

            //更改显示
            updateShow();

            break;
        }

        // 喜欢
        case R.id.image_love: {
//            love.setBackgroundColor(Color.parseColor("#D53E3E"));
//            Intent intent = new Intent();
//            intent.setClass(this, Main2Activity.class);
//
//            startActivity(intent);

            break;
        }

        //搜索
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

    // 列表Item的 点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("click", datas.get(position));

        // 获取点击的位置
        currentItem = position;

        //播放当前位置的曲目
        musicPlayer.play(position);

        //更改显示
        updateShow();
    }


    // 用于更改显示的方法
    void updateShow() {

        //停止计时器
        chronometer.stop();

        //计时器清零
        chronometer.setBase(SystemClock.elapsedRealtime());

        //获取当前播放曲目的标题
        title.setText(musicPlayer.getInformation().getName());
        //获取歌手信息
        singer.setText(musicPlayer.getInformation().getSinger());


        // 设置按钮为播放
        play_Pause.setImageResource(R.drawable.icon_love);

        //设置当前的item
        adapter.setCurrentItem(currentItem);

        //通知数据变化,重新绘制view
        adapter.notifyDataSetChanged();

        //设置选中的曲目
        musicList.setSelection(currentItem);

        //开始计时器
        chronometer.start();

        //开始线程
        handler.post(thread);


    }

    //设置第一次点击的时间
    long startTime = 0;

    @Override
    public void onBackPressed() {

        //获取当前的系统时间
        long currentTime = System.currentTimeMillis();

        // 如果超过两秒点击
        if ((currentTime - startTime) >= 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            startTime = currentTime;
        }
        //两秒内点击,退出程序
        else {
            musicPlayer.destroy();
            handler.removeCallbacks(thread);
            finish();
        }
    }
}

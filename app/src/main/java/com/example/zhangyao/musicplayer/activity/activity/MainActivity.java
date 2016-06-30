package com.example.zhangyao.musicplayer.activity.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
        , ListView.OnItemClickListener
        , MediaPlayer.OnCompletionListener {


    // 定义控件
    private ListView musicList;
    private ImageView love;
    private ImageView search;
    private ImageView lastOne;
    private ImageView nextOne;
    private ImageView play_Pause;
    private TextView title;
    private TextView singer;
    private TextView tx_model;
    private EditText keyWords;
    private MusicListAdapter adapter;
    private MediaPlayer player;
    private TextView tx_time;
    private String time;

    //ListView 的数据
    private ArrayList<String> arrayList = new ArrayList<>();

    //从 MediaStore 获取的歌曲列表
    private ArrayList<String> datas;

    //当前播放的曲目
    private int currentItem = 0;

    //是否是第一次播放
    private int fistTime = 1;


    private int model = 1;


    //新建进度条
    private SeekBar seekBar;


    //实例化自定义的MusicPlayer 对象
    MusicPlayer musicPlayer = new MusicPlayer(this);

    //新建Handler
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    tx_time.setText(time);
                    break;
                }
            }
            return false;
        }
    });

    //新建线程 , 更新进度条的进度
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {

            // 设置进度条的最大值, 必须设置,否者是不会动的
            seekBar.setMax(musicPlayer.getDuration());

            Log.d("totle", String.valueOf(musicPlayer.getDuration()));

            int length = musicPlayer.getPlayer().getCurrentPosition();

            // 设置进度条的进度是在播放的音乐的进度
            seekBar.setProgress(length);

            time = String.valueOf(length / 60000);
            time = time + ":" + String.valueOf(length % 60000 / 1000);


            handler.sendEmptyMessage(1);


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
        lastOne = (ImageView) findViewById(R.id.img_last);//上一首
        nextOne = (ImageView) findViewById(R.id.img_next);//下一首
        play_Pause = (ImageView) findViewById(R.id.img_play_pause);//暫停键

        title = (TextView) findViewById(R.id.tx_title);
        singer = (TextView) findViewById(R.id.tx_singer);
        tx_model = (TextView) findViewById(R.id.tx_model);

        keyWords = (EditText) findViewById(R.id.ed_key_word);

        musicList = (ListView) findViewById(R.id.list_music_list);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        tx_time = (TextView) findViewById(R.id.tx_time);

        // 设置进度条进度改变的事件
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            //进度改变,歌曲也从当前的进度播放
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
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
        tx_model.setOnClickListener(this);

        musicList.setOnItemClickListener(this);
        love.setOnClickListener(this);
        search.setOnClickListener(this);

        player = musicPlayer.getPlayer();

        player.setOnCompletionListener(this);
    }

    public void playNext() {

        //如果已经是最后一首歌
        if (++currentItem >= arrayList.size()) {
            //跳回到第一首
            currentItem = 0;
        }


        // 播放下一首
        musicPlayer.playNext();

        //更改显示
        updateShow();
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
                    fistTime = 0;

                    //停止线程
                }
                //没有在播放
                else {
                    //显示暂停按钮
                    play_Pause.setImageResource(R.drawable.icon_love);

                    //如果是第一次播放
                    if (fistTime == 1) {
                        //播放第一首歌曲
                        musicPlayer.play(0);
                        updateShow();
                        fistTime = 0;
                    } else {
                        //继续播放当前曲目
                        musicPlayer.pause();
                        //开始线程
                        handler.post(thread);
                    }
                }

                break;
            }
            //播放下一曲
            case R.id.img_next: {

                playNext();

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
            case R.id.tx_model: {
                /**
                 * 1 单曲循环 1
                 * 2 顺序播放 Order
                 * 3 列表循环 Loop
                 * 4 随机播放 Random
                 * */
                if (tx_model.getText().toString().equals("1")) {
                    model = 2;
                    tx_model.setText("Order");
                } else if (tx_model.getText().toString().equals("Order")) {
                    model = 3;
                    tx_model.setText("Loop");
                } else if (tx_model.getText().toString().equals("Loop")) {
                    model = 4;
                    tx_model.setText("Random");
                } else if (tx_model.getText().toString().equals("Random")) {
                    model = 1;
                    tx_model.setText("1");
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

    @Override
    public void onCompletion(MediaPlayer mp) {
        switch (model) {
            case 1: {
                musicPlayer.play(currentItem);
                updateShow();
                break;
            }
            case 2: {
                if (++currentItem < arrayList.size()) {
                    // 播放下一首
                    musicPlayer.playNext();
                }
                updateShow();
                break;
            }
            case 3: {
                playNext();
                break;
            }
            case 4: {
                Random random = new Random();
                currentItem = random.nextInt(arrayList.size());
                musicPlayer.play(currentItem);
                updateShow();
                break;
            }
        }
    }
}

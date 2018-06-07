package com.example.administrator.sexrecroding;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.sexrecroding.Base.BaseBindingFragment;
import com.example.administrator.sexrecroding.Bean.Position;
import com.example.administrator.sexrecroding.Room.AbstractSexDatabase;
import com.example.administrator.sexrecroding.Room.SexDTO;
import com.example.administrator.sexrecroding.databinding.HomeFragmentBinding;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @author Administrator
 * @date 2018/5/23 0023
 */

public class HomeFragment extends BaseBindingFragment<HomeFragmentBinding> {

    private static HomeFragment homeFragment;
    private Fragment currentFragment = new Fragment();
    private long exitTime = 0;//点击两次回到主界面的计数器
    private PositionHandle positionHandle;
    private MediaPlayer mediaPlayer;
    private static final int POSITION_MOVE_TO_NEXT = 0;
    private static final int SEX_TIME_PART = 1;
    private static final int SEX_TIME_ALL = 2;
    private static final int SAVE = 3;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    private int tran = 0xFF000000;
    private int end = 0x00000000;

    public static Fragment getInstance() {
        if (homeFragment == null) {
            synchronized (HomeFragment.class) {
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                }
            }
        }
        return homeFragment;
    }

    @Override
    protected void bindData(HomeFragmentBinding dataBinding) {
        fab = getActivity().findViewById(R.id.fab);
        appBarLayout = getActivity().findViewById(R.id.app_bar_layout);
        //TODO:有一个停止播放音乐的按钮
        binding.imgReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMediaPlayer();
                initHandle();
                fab.setVisibility(View.GONE);
                ValueAnimator colorAnim = ObjectAnimator.ofInt(binding.imgReady, "backgroundColor", tran, end);
                colorAnim.setDuration(300);
                colorAnim.setEvaluator(new ArgbEvaluator());
                colorAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        binding.imgReady.setVisibility(View.GONE);
                        hideAppBarLayout(appBarLayout);
                        positionHandle.sendEmptyMessage(POSITION_MOVE_TO_NEXT);
                        //TODO:，播放背景音乐，场景变换，设置变量记录动作，开始计时

                    }
                });
                colorAnim.start();
            }

        });
    }

    private void initHandle() {
        if (positionHandle != null) {
            return;
        }
        positionHandle = new PositionHandle();
        positionHandle.setList(getValue());
        positionHandle.setDesc(new WeakReference<TextView>(binding.tvDesc));
        positionHandle.setTitle(new WeakReference<TextView>(binding.tvTitle));
        positionHandle.setImgPosition(new WeakReference<ImageView>(binding.imgSexPosition));

    }

    private void initMediaPlayer() {
        if (mediaPlayer != null) {
            return;
        }
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.naufragio);
        mediaPlayer.start();
    }

    private static int randomPosition(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    private void hideAppBarLayout(AppBarLayout appBarLayout) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(appBarLayout, "translationY", 0, -appBarLayout.getHeight());
        animator.setDuration(300);
        animator.start();
    }

    private void showAppBarLayout(AppBarLayout appBarLayout) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(appBarLayout, "translationY", -appBarLayout.getHeight(), 0);
        animator.setDuration(300);
        animator.start();
    }

    private List<Position> getValue() {
        List<Position> list = new ArrayList<>();
        Position position1 = new Position("test1", "desc1", R.drawable.sex1);
        Position position2 = new Position("test2", "desc2", R.drawable.sex2);
        Position position3 = new Position("test3", "desc3", R.drawable.sex3);
        Position position4 = new Position("test4", "desc4", R.drawable.sex4);
        list.add(position1);
        list.add(position2);
        list.add(position3);
        list.add(position4);
        return list;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (binding.imgReady.getVisibility() == View.VISIBLE) {
                getActivity().finish();
                return;
            }
            if ((System.currentTimeMillis() - exitTime) > 2000) //System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(getActivity(), "click once more to stop", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                backToStart();
            }
            return;
        }
    }

    private void backToStart() {
        positionHandle.sendEmptyMessage(SAVE);
        binding.imgReady.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        Glide.with(getActivity()).load(R.drawable.ready).into(binding.imgReady);
        showAppBarLayout(appBarLayout);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (positionHandle != null) {
            positionHandle = null;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment;
    }

    private static class PositionHandle extends Handler {

        private WeakReference<ImageView> imgPosition;
        private WeakReference<TextView> title;
        private WeakReference<TextView> desc;
        private ArrayList<Position> positionList = new ArrayList<>();
        private ArrayList<String> positionTimeList = new ArrayList<>();
        private Position position;
        private int sexTimePart = 0;
        private int sexTimeAll = 1;
        private List<Position> list;
        private int cip = 0;
        private String date;
        private SoundPool mSoundPool;
        private HashMap<Integer, Integer> soundPoolMap;

        public PositionHandle() {
            initSoundPool();
            sendEmptyMessageDelayed(SEX_TIME_ALL, 1000);
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh");
            date = sDateFormat.format(new java.util.Date());
            Log.e("william,time", date + "");
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case POSITION_MOVE_TO_NEXT:
                    positionMoveToNext();
                    break;
                case SEX_TIME_PART:
                    sexTimePart++;
                    sendEmptyMessageDelayed(SEX_TIME_PART, 1000);
                    break;
                case SEX_TIME_ALL:
                    sexTimeAll++;
                    sendEmptyMessageDelayed(SEX_TIME_ALL, 1000);
                    break;
                case SAVE:
//                    if (sexTimeAll < 60) {
//                        return;
//                    }
                    SexDTO sexDTO = new SexDTO();
                    sexDTO.setDate(date);
                    sexDTO.setPositionTime(positionTimeList);
                    sexDTO.setPosition(positionList);
                    sexDTO.setSexTime(sexTimeAll + "");
                    title = null;
                    desc = null;
                    imgPosition = null;
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            AbstractSexDatabase.getInstance(getInstance().getActivity()).sexDao().insertOne(sexDTO);
                            List<SexDTO> list = AbstractSexDatabase.getInstance(getInstance().getActivity()).sexDao().getAll();
                            Log.e("william0", list.toString());

                        }
                    };
                    new Thread(runnable).start();
                    Toast.makeText(getInstance().getContext(), "存储了一条数据", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

        private void positionMoveToNext() {
            if (title == null || desc == null || imgPosition == null) {
                return;
            }
            if (cip > list.size() - 1) {
                cip = 0;
            }
            saveDB();
            sendEmptyMessage(SEX_TIME_PART);
            //TODO:cip随机生成不重复的数，如果所有都用过再重置
            //TODO：记录动作，并记录这个动作的时间
            position = list.get(cip);
            Glide.with(HomeFragment.getInstance())
                    .load(position.getImg())
                    .crossFade(1500)
                    .into(imgPosition.get());
            title.get().setText(position.getTitle());
            desc.get().setText(position.getDesc());
            mSoundPool.play(soundPoolMap.get(randomPosition(0, 1)), 0.6f, 0.6f, 1, 0, 1);
            cip++;
            sendEmptyMessageDelayed(POSITION_MOVE_TO_NEXT, randomPosition(3000, 5000));
        }

        private void saveDB() {
            if (sexTimePart != 0) {
                positionTimeList.add(sexTimePart + "");
                positionList.add(position);
                sexTimePart = 0;
            }
        }

        private void initSoundPool() {
            mSoundPool = new SoundPool(5, AudioManager.STREAM_ALARM, 5);
            soundPoolMap = new HashMap<>();
            soundPoolMap.put(0, mSoundPool.load(HomeFragment.getInstance().getContext(), R.raw.come_on, 1));
            soundPoolMap.put(1, mSoundPool.load(HomeFragment.getInstance().getContext(), R.raw.come_on_baby, 1));
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    soundPool.play(soundPoolMap.get(randomPosition(0, 1)), 0.6f, 0.6f, 1, 0, 1);
                }
            });
        }

        public void setList(List<Position> list) {
            this.list = list;
        }

        public void setImgPosition(WeakReference<ImageView> imgPosition) {
            this.imgPosition = imgPosition;
        }

        public void setmSoundPool(SoundPool mSoundPool) {
            this.mSoundPool = mSoundPool;
        }

        public void setSoundPoolMap(HashMap<Integer, Integer> soundPoolMap) {
            this.soundPoolMap = soundPoolMap;
        }

        public void setTitle(WeakReference<TextView> title) {
            this.title = title;
        }

        public void setDesc(WeakReference<TextView> desc) {
            this.desc = desc;
        }
    }
}

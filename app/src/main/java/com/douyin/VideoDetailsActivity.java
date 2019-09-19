package com.douyin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.douyin.bean.MainVideoBean;
import com.douyin.fragment.VideoFragment;
import com.douyin.util.IntentUtil;
import com.douyin.util.StatusBarUtil;
import com.example.administrator.douyin.R;

import java.util.ArrayList;

public class VideoDetailsActivity extends FragmentActivity {

    private ArrayList<MainVideoBean> dataList;
    private ImageView iv_close;
    private int playPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);
        initIntent();
        initUI();
    }

    private void initIntent() {
        Intent intent = getIntent();
        playPosition = intent.getIntExtra(IntentUtil.INTENT_PLAY_POSITION, 0);
        dataList = (ArrayList<MainVideoBean>) intent.getSerializableExtra(IntentUtil.INTENT_DATA_LIST);
    }

    private void initUI() {
        iv_close = findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        VideoFragment videoFragment = new VideoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IntentUtil.INTENT_PLAY_POSITION, playPosition);
        bundle.putSerializable(IntentUtil.INTENT_DATA_LIST, dataList);
        videoFragment.setArguments(bundle);
        beginTransaction.replace(R.id.contentFL, videoFragment);
        beginTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorBlack), false);
    }
}

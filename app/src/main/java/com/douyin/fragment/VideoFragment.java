package com.douyin.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.douyin.adapter.VideoDetailsAdapter;
import com.douyin.bean.MainVideoBean;
import com.douyin.util.DataUtil;
import com.douyin.util.IntentUtil;
import com.douyin.util.MediaPlayerTool;
import com.example.administrator.douyin.R;

import java.util.ArrayList;

public class VideoFragment extends Fragment {

    private View mRootView;
    private RecyclerView rvVideo;
    private Activity mActivity;
    private LinearLayoutManager linearLayoutManager;
    private PagerSnapHelper pagerSnapHelper;
    private MediaPlayerTool mMediaPlayerTool;
    private ArrayList<MainVideoBean> dataList;
    private int playPosition = -1;
    private VideoDetailsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_video, container, false);
        mActivity = getActivity();
        initArguments();
        initUI();
        return mRootView;
    }

    private void initArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            playPosition = bundle.getInt(IntentUtil.INTENT_PLAY_POSITION, -1);
            dataList = (ArrayList<MainVideoBean>) bundle.getSerializable(IntentUtil.INTENT_DATA_LIST);
        }
    }

    private void initUI() {

        rvVideo = mRootView.findViewById(R.id.rvVideo);

        linearLayoutManager = new LinearLayoutManager(mActivity);
        rvVideo.setLayoutManager(linearLayoutManager);

        pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(rvVideo);
        mAdapter = new VideoDetailsAdapter(mActivity, dataList);
        rvVideo.setAdapter(mAdapter);

        rvVideo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (pagerSnapHelper.findSnapView(linearLayoutManager) != playView) {
                        playVisibleVideo(false);
                    }
                }
            }
        });
        mMediaPlayerTool = MediaPlayerTool.getInstance();
        if (dataList == null || dataList.isEmpty()) {
            dataList = DataUtil.createData();
            mAdapter.setData(dataList);
        }
    }

    View playView;

    /**
     * @param isResumePlay 是否继续上个界面播放
     */
    public void playVisibleVideo(boolean isResumePlay) {

        View snapView = pagerSnapHelper.findSnapView(linearLayoutManager);
        if (snapView == null) {
            return;
        }
        final int position = linearLayoutManager.getPosition(snapView);
        if (position < 0) {
            return;
        }

        if (!isResumePlay) {
            //重置播放器要在前面
            mMediaPlayerTool.reset();
        }

        playView = snapView;
        final VideoDetailsAdapter.MyViewHolder vh = (VideoDetailsAdapter.MyViewHolder) rvVideo.getChildViewHolder(playView);

        if (isResumePlay) {
            vh.pb_video.setVisibility(View.GONE);
            vh.iv_cover.setVisibility(View.GONE);
            vh.playTextureView.setRotation(mMediaPlayerTool.getRotation());
            vh.playTextureView.setVideoSize(mMediaPlayerTool.getVideoWidth(), mMediaPlayerTool.getVideoHeight());
        } else {
            //显示正在加载的界面
            vh.pb_video.setVisibility(View.VISIBLE);
            vh.iv_cover.setVisibility(View.VISIBLE);

            mMediaPlayerTool.initMediaPLayer();
            mMediaPlayerTool.setDataSource(dataList.get(position).getVideoUrl());
        }

        mMediaPlayerTool.setLooping(true);
        mMediaPlayerTool.setVolume(1);
        mMediaPlayerTool.setVideoListener(new MediaPlayerTool.VideoListener() {
            @Override
            public void onStart() {
                vh.pb_video.setVisibility(View.GONE);
                vh.iv_cover.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        vh.iv_cover.setVisibility(View.GONE);
                    }
                }, 200);
                vh.img_play.animate().alpha(0f).start();
                vh.playTextureView.setVideoSize(mMediaPlayerTool.getVideoWidth(), mMediaPlayerTool.getVideoHeight());
            }

            @Override
            public void onRotationInfo(int rotation) {
                vh.playTextureView.setRotation(rotation);
            }

            @Override
            public void onStop() {
                vh.pb_video.setVisibility(View.GONE);
                vh.img_play.animate().alpha(0.7f).start();
                vh.iv_cover.setVisibility(View.VISIBLE);
                vh.pb_play_progress.setSecondaryProgress(0);
                vh.pb_play_progress.setProgress(0);
                playView = null;
            }

            @Override
            public void onCompletion() {
                onStop();
                if (position + 1 >= dataList.size()) {
                    rvVideo.smoothScrollToPosition(0);
                } else {
                    rvVideo.smoothScrollToPosition(position + 1);
                }
            }

            @Override
            public void onPlayProgress(long currentPosition) {
                int pro = (int) (currentPosition * 1f / mMediaPlayerTool.getDuration() * 100);
                vh.pb_play_progress.setProgress(pro);
            }

            @Override
            public void onBufferProgress(int progress) {
                vh.pb_play_progress.setSecondaryProgress(progress);
            }
        });

        if (isResumePlay) {
            vh.playTextureView.resetTextureView(mMediaPlayerTool.getAvailableSurfaceTexture());
            mMediaPlayerTool.setPlayTextureView(vh.playTextureView);
            vh.playTextureView.postInvalidate();
        } else {
            vh.playTextureView.resetTextureView();
            mMediaPlayerTool.setPlayTextureView(vh.playTextureView);
            mMediaPlayerTool.setSurfaceTexture(vh.playTextureView.getSurfaceTexture());
            mMediaPlayerTool.prepare();
        }
        vh.playTextureView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mMediaPlayerTool.isPlaying()) {
                    vh.img_play.animate().alpha(0.7f).start();
                    mMediaPlayerTool.pause();
                } else {
                    vh.img_play.animate().alpha(0f).start();
                    mMediaPlayerTool.start();
                }
            }
        });
    }

    private boolean isVisible = false;
    private boolean isAutoPause = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (rvVideo != null) {
            if (isVisibleToUser) {
                if (mMediaPlayerTool != null) {
                    playVisibleVideo(false);
                }
            } else {
                if (mMediaPlayerTool != null && mMediaPlayerTool.isPlaying()) {
                    mMediaPlayerTool.reset();
                }
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible || playPosition != -1) {
            if (isFirst) {
                isFirst = false;
                if (rvVideo != null && mMediaPlayerTool != null) {
                    rvVideo.post(new Runnable() {
                        @Override
                        public void run() {
                            if (playPosition != -1) {
                                rvVideo.scrollToPosition(playPosition);
                            }
                            rvVideo.post(new Runnable() {
                                @Override
                                public void run() {
                                    playVisibleVideo(mMediaPlayerTool.isPlaying());
                                }
                            });
                        }
                    });
                }
            } else {
                if (isAutoPause && mMediaPlayerTool != null && !mMediaPlayerTool.isPlaying()) {
                    isAutoPause = false;
                    mMediaPlayerTool.start();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isVisible && mMediaPlayerTool != null && mMediaPlayerTool.isPlaying()) {
            isAutoPause = true;
            mMediaPlayerTool.pause();
        }
    }

    boolean isFirst = true;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (rvVideo != null) {
            if (isVisible && mMediaPlayerTool != null && mMediaPlayerTool.isPlaying()) {
                isAutoPause = true;
                mMediaPlayerTool.pause();
            }
        }
    }
}

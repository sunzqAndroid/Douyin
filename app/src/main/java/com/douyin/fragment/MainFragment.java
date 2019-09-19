package com.douyin.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.SmartSwipeWrapper;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.DrawerConsumer;
import com.billy.android.swipe.consumer.SlidingConsumer;
import com.billy.android.swipe.consumer.StretchConsumer;
import com.billy.android.swipe.listener.SimpleSwipeListener;
import com.douyin.MainActivity;
import com.douyin.util.MediaPlayerTool;
import com.example.administrator.douyin.R;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationItem;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationView;
import com.luseen.luseenbottomnavigation.BottomNavigation.OnBottomNavigationItemClickListener;

public class MainFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "douyin";
    private View mRootView;
    private View mContentView;
    private BottomNavigationView bottomNavigationView;
    private Activity mActivity;
    private SlidingConsumer mRightConsumer;
    private SlidingConsumer mLeftConsumer;
    private DrawerConsumer mCurrentConsumer;

    private int prePos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);
        mActivity = getActivity();
        initView();
        return mRootView;
    }

    private void initView() {
        bottomNavigationView = mRootView.findViewById(R.id.bottomNavigation);
        bottomNavigationView.selectTab(0);
        mContentView = mRootView.findViewById(R.id.contentView);
        initFragments();
        initSlideMenu();
        initBottom();
    }

    private void initFragments() {
        if (mFragments == null) {
            //mFragments被回收导致崩溃
            mFragments = new Fragment[4];
            mFragments[0] = new HomeFragment();
            mFragments[1] = new FollowFragment();
            mFragments[2] = new MsgFragment();
            mFragments[3] = new MineFragment();
        }
        switchTab(0);
    }

    private Fragment[] mFragments;

    public void switchTab(int pos) {
        prePos = pos;
        FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.length; i++) {
            beginTransaction.hide(mFragments[i]);
        }

        if (!mFragments[pos].isAdded()) {
            beginTransaction.add(R.id.contentFL, mFragments[pos]);
        }
        beginTransaction.show(mFragments[pos]);
        beginTransaction.commitAllowingStateLoss();
    }

    private void initBottom() {
        bottomNavigationView.isWithText(true);
        bottomNavigationView.isWithIcon(false);
        bottomNavigationView.setTextActiveSize(getResources().getDimension(R.dimen.text_active));
        bottomNavigationView.setTextInactiveSize(getResources().getDimension(R.dimen.text_inactive));
        bottomNavigationView.setItemActiveColorWithoutColoredBackground(ContextCompat.getColor(mActivity, R.color.colorWhite));

        BottomNavigationItem bottomNavigationItem = new BottomNavigationItem
                ("首页");
        BottomNavigationItem bottomNavigationItem1 = new BottomNavigationItem
                ("同城");
        BottomNavigationItem bottomNavigationItem2 = new BottomNavigationItem
                ("");
        BottomNavigationItem bottomNavigationItem3 = new BottomNavigationItem
                ("消息");
        BottomNavigationItem bottomNavigationItem4 = new BottomNavigationItem
                ("我");


        bottomNavigationView.addTab(bottomNavigationItem);
        bottomNavigationView.addTab(bottomNavigationItem1);
        bottomNavigationView.addTab(bottomNavigationItem2);
        bottomNavigationView.addTab(bottomNavigationItem3);
        bottomNavigationView.addTab(bottomNavigationItem4);

        bottomNavigationView.setOnBottomNavigationItemClickListener(new OnBottomNavigationItemClickListener() {
            @Override
            public void onNavigationItemClick(int index) {
                DrawerConsumer consumer = null;
                int pos = 0;
                switch (index) {
                    case 0:
                        pos = 0;
                        consumer = mLeftConsumer;
                        break;
                    case 1:
                        pos = 1;
                        break;
                    case 2:
                        return;
                    case 3:
                        pos = 2;
                        break;
                    case 4:
                        pos = 3;
                        consumer = mRightConsumer;
                        break;
                }
                if (prePos != pos) {
                    changeMenu(consumer);
                    if (index == 0) {
                        ((MainActivity) mActivity).setAllowSroll(true);
                        play();
                    } else {
                        ((MainActivity) mActivity).setAllowSroll(false);
                        MediaPlayerTool mMediaPlayerTool = MediaPlayerTool.getInstance();
                        mMediaPlayerTool.reset();
                    }
                    switchTab(pos);
                }
            }
        });
    }

    private void initSlideMenu() {

        View rightMenu = LayoutInflater.from(mActivity).inflate(R.layout.right_menu, null);
        rightMenu.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        SmartSwipeWrapper rightMenuWrapper = SmartSwipe.wrap(rightMenu).addConsumer(new StretchConsumer()).enableVertical().getWrapper();

        View leftMenu = LayoutInflater.from(mActivity).inflate(R.layout.fragment_first, null);
        leftMenu.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        SmartSwipeWrapper leftMenuWrapper = SmartSwipe.wrap(leftMenu).addConsumer(new StretchConsumer()).enableVertical().getWrapper();
        mRightConsumer = new SlidingConsumer()
                .setRightDrawerView(rightMenuWrapper)
                .setEdgeSize(0)
                .as(SlidingConsumer.class);
        mRightConsumer.setRelativeMoveFactor(1.0f);

        mLeftConsumer = new SlidingConsumer()
                .setLeftDrawerView(leftMenuWrapper)
                .setEdgeSize(0)
                .addListener(listener)
                .as(SlidingConsumer.class);
        mLeftConsumer.setRelativeMoveFactor(0);
        changeMenu(mLeftConsumer);
    }

    private void changeMenu(DrawerConsumer drawerConsumer) {
        if (drawerConsumer == null) {
            SmartSwipe.wrap(mContentView).removeAllConsumers();
        } else {
            if (mCurrentConsumer != null) {
                SmartSwipe.wrap(mContentView).removeConsumer(mCurrentConsumer);
            }
            SmartSwipe.wrap(mContentView).addConsumer(drawerConsumer);
        }
        mCurrentConsumer = drawerConsumer;
    }

    SimpleSwipeListener listener = new SimpleSwipeListener() {
        @Override
        public void onSwipeOpened(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction) {
            super.onSwipeOpened(wrapper, consumer, direction);
            ((MainActivity) mActivity).setAllowSroll(false);
            MediaPlayerTool mMediaPlayerTool = MediaPlayerTool.getInstance();
            mMediaPlayerTool.reset();
        }

        @Override
        public void onSwipeClosed(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction) {
            super.onSwipeClosed(wrapper, consumer, direction);
            ((MainActivity) mActivity).setAllowSroll(true);
            play();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photoIv:
                break;
        }
    }

    public boolean onBackPressed() {
        if (mCurrentConsumer != null && !mCurrentConsumer.isClosed()) {
            mCurrentConsumer.close(true);
            return true;
        }
        return false;
    }

    public void play() {
        ((HomeFragment) mFragments[0]).play();
    }
}

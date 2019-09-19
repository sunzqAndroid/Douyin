package com.douyin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.douyin.fragment.MainFragment;
import com.douyin.fragment.SecondFragment;
import com.douyin.util.MediaPlayerTool;
import com.douyin.util.StatusBarUtil;
import com.douyin.view.CustomViewPager;
import com.example.administrator.douyin.R;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    CustomViewPager mViewPager;
    MyPagerAdapter mPagerAdapter;
    MainFragment mMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = findViewById(R.id.viewPager);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        addingFragmentsTOpagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(true, new SimpleTransformation());
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0) {
                    MediaPlayerTool mMediaPlayerTool = MediaPlayerTool.getInstance();
                    mMediaPlayerTool.reset();
                } else {
                    mMainFragment.play();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorBlack), false);
    }

    private void addingFragmentsTOpagerAdapter() {
        mMainFragment = new MainFragment();
        mPagerAdapter.addFragments(mMainFragment);
        mPagerAdapter.addFragments(new SecondFragment());
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> fragmentList = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragments(Fragment fragment) {
            fragmentList.add(fragment);
        }
    }

    @Override
    public void onBackPressed() {
        if (mMainFragment.onBackPressed()) {
            return;
        } else if (mViewPager.getCurrentItem() != 0) {
            mViewPager.setCurrentItem(0);
        } else {
            finish();
        }
    }

    public void setAllowSroll(boolean enable) {
        if (enable) {
            mViewPager.setAllowedSwipeDirection(CustomViewPager.SwipeDirection.all);
        } else {
            mViewPager.setAllowedSwipeDirection(CustomViewPager.SwipeDirection.none);
        }
    }
}
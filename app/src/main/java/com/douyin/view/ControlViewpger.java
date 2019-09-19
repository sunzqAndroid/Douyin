package com.douyin.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ControlViewpger extends ViewPager {
    //是否可以滑出左侧页面
    private boolean isCanGoLeft = true;
    //是否可以画出右侧页面
    private boolean isCanGoRight = true;

    public ControlViewpger(Context context) {
        super(context);
    }

    public ControlViewpger(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCanGoLeft(boolean canGoLeft) {
        isCanGoLeft = canGoLeft;
    }

    public void setCanGoRight(boolean canGoRight) {
        isCanGoRight = canGoRight;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            Log.d("kid", "IllegalArgumentException发生了");
        }
        return false;
    }

    private float lastX;
    private static final int LEFT_TO_RIGHT = 1;//方向=从左到右
    private static final int RIGHT_TO_LEFT = 2;//方向=从右到左
    //手指是否已经抬起 （如果不加入这个参数判断，在手指抬起的那一刻touch事件return了true，页面会卡在手指离开那时候的偏移位置）
    private boolean isUp = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int direction = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isUp = false;
                //获取起始坐标值
                lastX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getX() - lastX < 0) {//从右到左滑（左滑）
                    direction = RIGHT_TO_LEFT;
                } else {//从左到右滑（右滑）
                    direction = LEFT_TO_RIGHT;
                }
                break;
            case MotionEvent.ACTION_UP:
                isUp = true;
                break;
        }
        if (isUp) {
            //手指抬起后,不再接管viewpager的事件，让它自由地滑动
            return tryCatchError(event);
        } else if (direction == RIGHT_TO_LEFT && isCanGoRight) {
            //允许滑动则应该调用父类的方法
            return tryCatchError(event);
        } else if (direction == LEFT_TO_RIGHT && isCanGoLeft) {
            //允许滑动则应该调用父类的方法
            return tryCatchError(event);
        } else {
            //禁止滑动则不做任何操作，直接返回true即可
            return true;
        }
    }

    /**
     * viewpager滑动偶尔莫名其妙崩溃报native,捕获异常以后，出现这种情况的时候App不会崩溃，只是卡顿那一短暂时间后继续正常操作
     *
     * @param event
     * @return
     */
    private boolean tryCatchError(MotionEvent event) {
        try {
            return super.onTouchEvent(event);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            Log.d("kid", "捕获浏览报错4");
        }
        return false;
    }
}
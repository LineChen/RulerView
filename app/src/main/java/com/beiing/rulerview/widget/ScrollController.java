package com.beiing.rulerview.widget;

import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

/**
 * Created by chenliu on 2016/8/26.<br/>
 * 描述：
 * </br>
 */
public class ScrollController {

    View view;
    private Scroller scroller;
    private VelocityTracker velocityTracker;//速度跟踪器

    public ScrollController(View view) {
        this.view = view;
        scroller = new Scroller(view.getContext());
        velocityTracker = VelocityTracker.obtain();
    }

    //调用此方法滚动到目标位置
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - scroller.getFinalX();
        int dy = fy - scroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {
        //设置mScroller的滚动偏移量
        scroller.startScroll(scroller.getFinalX(), scroller.getFinalY(), dx, dy, 500);
        view.invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    //调用此方法滚动到目标位置
    public void scrollTo(int fx, int fy) {
        int dx = fx - scroller.getFinalX();
        int dy = fy - scroller.getFinalY();
        scrollBy(dx, dy);
    }

    public void scrollBy(int dx, int dy){
        scroller.startScroll(scroller.getFinalX(), scroller.getFinalY(), dx, dy);
        view.invalidate();
    }


    private void countVelocityTracker() {
        velocityTracker.computeCurrentVelocity(1000);
        float xVelocity = -velocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > ViewConfiguration.get(view.getContext()).getScaledMinimumFlingVelocity()) {
            smoothScrollBy(200, 0);
        }
    }
}

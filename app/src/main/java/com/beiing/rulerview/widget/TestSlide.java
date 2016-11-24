package com.beiing.rulerview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

/**
 * Created by chenliu on 2016/10/20.<br/>
 * 描述：
 * </br>
 */
public class TestSlide extends View {

    private Paint paint;

    private GestureDetectorCompat gestureDetector;

    public TestSlide(Context context) {
        super(context);
    }

    public TestSlide(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScroller = new Scroller(getContext());
        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(60);

        gestureDetector = new GestureDetectorCompat(getContext(), new SimpleGestureListener());

    }

    public TestSlide(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setAlpha(100);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);

        paint.setAlpha(255);
        paint.setColor(Color.BLACK);

        mMove = Math.max(0, mMove);
        mMove = Math.min(getMeasuredWidth() - 200, mMove);
//        Log.e("====", "mMove=" + mMove);
        canvas.drawText("...TestSlide...", mMove, 100, paint);
    }

    int mLastX, mMove;

    private int mMinVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;//速度跟踪器

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int xPosition = (int) event.getX();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScroller.abortAnimation();
                mLastX = xPosition;
                break;
            case MotionEvent.ACTION_MOVE:
                mMove += (xPosition - mLastX);
                mLastX = xPosition;
                changeMoveAndValue();
                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                countVelocityTracker();
//                break;
        }
        mLastX = xPosition;
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private void countVelocityTracker() {
        mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = mVelocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > mMinVelocity) {
            mScroller.fling(mMove, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }
    }

    /**
     * 手指滑动
     */
    private void changeMoveAndValue() {
        postInvalidate();
    }


    //调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {

        //设置mScroller的滚动偏移量
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScroller.getCurrX() == mScroller.getFinalX()) { // over
            } else {
                int xPosition = mScroller.getCurrX();
                mMove += (xPosition - mLastX);
                changeMoveAndValue();
                mLastX = xPosition;
            }
        }
    }

    private class SimpleGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScroller.fling(mMove, 0, (int)velocityX, (int)velocityY, 0, getMeasuredWidth(), 0, 0);
            return true;
        }
    }

}

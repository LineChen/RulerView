package com.beiing.rulerview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

/**
 * Created by chenliu on 2016/8/25.<br/>
 * 描述：
 * </br>
 */
public class NewRulerView extends View {
    /**
     * wrap_content的宽高
     */
    public static final int DEFAULT_WIDTH_DP = 200;
    public static final int DEFAULT_HEIGHT_DP = 50;

    public static final int DEFAULT_DIALS_WIDTH_DP = 2;

    /**
     * 刻度画笔
     */
    protected Paint dialsPaint;

    protected int dialsColor = Color.parseColor("#689f38");

    protected float dialsWidth = dp2px(DEFAULT_DIALS_WIDTH_DP);

    /**
     * 滑动相关
     */
    protected Scroller scroller;

    protected VelocityTracker velocityTracker;


    public NewRulerView(Context context) {
        this(context, null, 0);
    }

    public NewRulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewRulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        dialsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dialsPaint.setStyle(Paint.Style.STROKE);
        dialsPaint.setColor(dialsColor);
        dialsPaint.setStrokeWidth(dialsWidth);

        scroller = new Scroller(getContext());
        velocityTracker = VelocityTracker.obtain();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;
        switch (widthSpecMode){
            case MeasureSpec.AT_MOST:
                width = (int) dp2px(DEFAULT_WIDTH_DP);
                break;

            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                width = widthSpecSize;
                break;
        }

        switch (heightSpecMode){
            case MeasureSpec.AT_MOST:
                height = (int) dp2px(DEFAULT_HEIGHT_DP);
                break;

            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                height = heightSpecSize;
                break;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        drawDials(canvas);

        drawScaleLine(canvas);
    }

    private void drawDials(Canvas canvas) {
        for (int i = 1; i <= 100; i++) {
            canvas.drawLine(i * dp2px(10) + moveX, 0, i * dp2px(10) + moveX, i % 5 == 0 ? dp2px(30) : dp2px(20), dialsPaint);
        }
    }


    private int mValue = 100;

    private int mMaxValue = 200;

    private int mLineDivider = 10;
    /**
     * 从中间往两边开始画刻度线
     *
     * @param canvas
     */
    private void drawScaleLine(Canvas canvas) {
        canvas.save();

        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setColor(Color.BLACK);

        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(dp2px(16));

        int width = getMeasuredWidth(), drawCount = 0;
        float xPosition = 0, textWidth = Layout.getDesiredWidth("0", textPaint);

        for (int i = 0; drawCount <= 6 * width; i++) {
            int numSize = String.valueOf(mValue + i).length();
            xPosition = (width / 2 - moveX) + i * dp2px(mLineDivider);
            if (xPosition + getPaddingRight() < width) {
                if ((mValue + i) % 10 == 0) {
                    canvas.drawLine(xPosition, getPaddingTop(), xPosition, dp2px(20), linePaint);

                    if (mValue + i <= mMaxValue && mValue + i >= 10) {
                        String text = String.valueOf((mValue + i) / 10) + ".0";
                        canvas.drawText(text, xPosition - (textWidth * numSize / 2), getHeight() - textWidth, textPaint);
                        }
                    }
                } else {
                    canvas.drawLine(xPosition, getPaddingTop(), xPosition, dp2px(10), linePaint);
                }
            xPosition = (width / 2 - moveX) - dp2px(mLineDivider);
            if (xPosition > getPaddingLeft()) {
                if ((mValue - i) % 10 == 0) {
                    canvas.drawLine(xPosition, getPaddingTop(), xPosition, dp2px(20), linePaint);
                    if (mValue - i >= 10) {
                        canvas.drawText(String.valueOf((mValue - i)/10) + ".0", xPosition - (textWidth * numSize / 2), getHeight() - textWidth, textPaint);
                    }
                } else {
                    canvas.drawLine(xPosition, getPaddingTop(), xPosition, dp2px(10), linePaint);
                }
            }

            drawCount += 2 * dp2px(mLineDivider);
        }

        canvas.restore();
    }

    int downX = 0;
    int moveX = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        velocityTracker.addMovement(event);

        int x = (int) event.getX();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!scroller.isFinished()){
                    scroller.abortAnimation();
                }
                downX = (int) event.getX();
                moveX = 0;
                break;

            case MotionEvent.ACTION_MOVE:
                int dx = x - downX;
                moveX += dx;
                invalidate();
//                smoothScrollBy(-dx, 0);
                break;

            case MotionEvent.ACTION_UP:
                countVelocityTracker();
                break;
        }
        return true;
    }

    private void countVelocityTracker() {
        velocityTracker.computeCurrentVelocity(1000);
        float xVelocity = velocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity()) {
            scroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }
    }

    @Override
    public void computeScroll() {
        //先判断mScroller滚动是否完成
        if (scroller.computeScrollOffset()) {
            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * 调用此方法滚动到目标位置
     * @param fx
     * @param fy
     */
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - scroller.getFinalX();
        int dy = fy - scroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    /**
     * 调用此方法设置滚动的相对偏移
     * @param dx
     * @param dy
     */
    public void smoothScrollBy(int dx, int dy) {
        //设置mScroller的滚动偏移量
        scroller.startScroll(scroller.getFinalX(), scroller.getFinalY(), dx, dy, 300);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    public float dp2px(float dpValue){
        float density =  getContext().getResources().getDisplayMetrics().density;
        return dpValue * density;
    }

}
























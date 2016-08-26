package com.beiing.rulerview.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;
@SuppressLint("ClickableViewAccessibility")

public class NewRulerView extends View {

    /**
     * wrap_content的宽高
     */
    public static final int DEFAULT_WIDTH_DP = 200;
    public static final int DEFAULT_HEIGHT_DP = 50;

    public static final int DEFAULT_DIALS_WIDTH_DP = 2;

    public interface OnValueChangeListener {
        void onValueChange(float value);
    }

    public static final int MOD_TYPE_HALF = 2;
    public static final int MOD_TYPE_ONE = 10;

    private static final int ITEM_HALF_DIVIDER = 40;
    private static final int ITEM_ONE_DIVIDER = 10;

    private static final int ITEM_MAX_HEIGHT = 30;
    private static final int ITEM_MIN_HEIGHT = 15;

    private static final int TEXT_SIZE = 18;

    private float mDensity;
    private int mValue = 61, mMaxValue = 333, mModType = MOD_TYPE_ONE,
            mLineDivider = ITEM_ONE_DIVIDER;

    private int mLastX, mMove;
    private int mWidth, mHeight;

    private int mMinVelocity; //最小速度
    private Scroller mScroller; //滑动控制器
    private VelocityTracker mVelocityTracker;//速度跟踪器

    private OnValueChangeListener mListener;

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
        mScroller = new Scroller(getContext());
        mDensity = getContext().getResources().getDisplayMetrics().density;
        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
    }

    /**
     *
     * @param defaultValue
     *            初始值
     * @param maxValue
     *            最大值
     * @param model
     *            刻度盘精度：<br>
     */
    public void initViewParam(int defaultValue, int maxValue, int model) {
        switch (model) {
            case MOD_TYPE_HALF:
                mModType = MOD_TYPE_HALF;
                mLineDivider = ITEM_HALF_DIVIDER;
                mValue = defaultValue * 2;
                mMaxValue = maxValue * 2;
                break;
            case MOD_TYPE_ONE:
                mModType = MOD_TYPE_ONE;
                mLineDivider = ITEM_ONE_DIVIDER;
                mValue = defaultValue;
                mMaxValue = maxValue;
                break;

            default:
                break;
        }
        invalidate();

        mLastX = 0;
        mMove = 0;
        notifyValueChange();
    }

    public void initDefaultViewParam(int defaultValue) {
        mValue = defaultValue;
        invalidate();
        mLastX = 0;
        mMove = 0;
        notifyValueChange();
    }


    /**
     * 设置用于接收结果的监听器
     *
     * @param listener
     */
    public void setOnValueChangeListener(OnValueChangeListener listener) {
        mListener = listener;
    }

    /**
     * 获取当前刻度值
     *
     * @return
     */
    public float getValue() {
        return mValue;
    }

    public void setValue(float value) {
        mValue = (int)(value * 10);
        initDefaultViewParam(mValue);
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
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth(); 
        mHeight = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawDials(canvas);


        /**
         * 绘制中间线
         */
        drawMiddleLine(canvas);
    }

    private int minValue = 0;
    private int maxValue = 200;
    private int oneGapValue = 1;//一刻度代表的值
    private int oneGapWidth = 60;//刻度间的宽度
    private int dialsHeightMin = 20;//短刻度高度
    private int dialsHeightMax = 35;//长刻度高度
    private int startLeft = 20;//开始位置
    private int dialsWidth = 5;//刻度宽度
    private int dialsColor = Color.MAGENTA;

    private int moveRecode = 0;

    private void drawDials(Canvas canvas) {
        int dialsCount = (maxValue - minValue) / oneGapValue;

        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(dialsWidth);
        linePaint.setColor(dialsColor);

        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(36);

        for (int i = 0; i < dialsCount; i++) {
            float startX = i * oneGapWidth + startLeft;
            if(i % 5 == 0){
                canvas.drawLine(startX, 0, startX, dialsHeightMax, linePaint);

                String text = String.valueOf(i * oneGapValue);
                Rect bounds = new Rect();
                textPaint.getTextBounds(text, 0, text.length(), bounds);
                int textW = bounds.width();
                int textH = bounds.height();

                canvas.drawText(text, startX - textW / 2, dialsHeightMax + textH * 1.5f, textPaint);
            } else {
                canvas.drawLine(startX, 0, startX, dialsHeightMin, linePaint);
            }
        }

    }

    private int distance = 0;

    /**
     * 画中间的红色指示线、阴影等。指示线两端简单的用了两个矩形代替
     *
     * @param canvas
     */
    private void drawMiddleLine(Canvas canvas) {
        // TOOD 常量太多，暂时放这，最终会放在类的开始，放远了怕很快忘记
        int gap = 12, indexWidth = 5, indexTitleWidth = 24, indexTitleHight = 10, shadow = 6;
        String color = "#66999999";

        canvas.save();

        Paint redPaint = new Paint();
        redPaint.setStrokeWidth(indexWidth);
        redPaint.setColor(Color.RED);
        canvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight/2, redPaint);

        Paint ovalPaint = new Paint();
        ovalPaint.setColor(Color.RED);
        ovalPaint.setStrokeWidth(indexTitleWidth);
//        canvas.drawLine(mWidth / 2, 0, mWidth / 2, indexTitleHight, ovalPaint);
//        canvas.drawLine(mWidth / 2, mHeight - indexTitleHight, mWidth / 2, mHeight, ovalPaint);

        // RectF ovalRectF = new RectF(mWidth / 2 - 10, 0, mWidth / 2 + 10, 4 *
        // mDensity); //TODO 椭圆
        // canvas.drawOval(ovalRectF, ovalPaint);
        // ovalRectF.set(mWidth / 2 - 10, mHeight - 8 * mDensity, mWidth / 2 +
        // 10, mHeight); //TODO

        Paint shadowPaint = new Paint();
        shadowPaint.setStrokeWidth(shadow);
        shadowPaint.setColor(Color.parseColor(color));
//        canvas.drawLine(mWidth / 2 + gap, 0, mWidth / 2 + gap, mHeight, shadowPaint);

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int xPosition = (int) event.getX();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScroller.abortAnimation();
                mLastX = xPosition;
                mMove = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                mMove = (mLastX - xPosition);
                smoothScrollBy(mMove, 0);
                changeMoveAndValue();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                countMoveEnd();
                countVelocityTracker();
                return true;
            default:
                break;
        }

        mLastX = xPosition;
        return true;
    }

    private void countVelocityTracker() {
        mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = -mVelocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > mMinVelocity) {
            mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }
    }

    private void changeMoveAndValue() {
        int tValue = mMove / oneGapWidth;
        if (Math.abs(tValue) > 0) {
            mValue += tValue;
            mMove -= tValue * oneGapWidth;
            if (mValue <= 10 || mValue > mMaxValue) {
                mValue = mValue <= 10 ? 10 : mMaxValue;
                mMove = 0;
                mScroller.forceFinished(true);
            }
            notifyValueChange();
        }
        postInvalidate();
    }

    private void countMoveEnd() {
        int roundMove = Math.round(mMove / oneGapWidth);
        mValue = mValue + roundMove;
        mValue = mValue <= 10 ? 10 : mValue;
        mValue = mValue > mMaxValue ? mMaxValue : mValue;

        mLastX = 0;
        mMove = 0;

        notifyValueChange();
        postInvalidate();
    }

    private void notifyValueChange() {
        if (null != mListener) {
            if (mModType == MOD_TYPE_ONE) {
                mListener.onValueChange(mValue);
            }
            if (mModType == MOD_TYPE_HALF) {
                mListener.onValueChange(mValue / 2f);
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
    }


    //调用此方法滚动到目标位置
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {
        //设置mScroller的滚动偏移量
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }



    private float dp2px(int dp){
        float density = getContext().getResources().getDisplayMetrics().density;
        return dp * density;
    }

}
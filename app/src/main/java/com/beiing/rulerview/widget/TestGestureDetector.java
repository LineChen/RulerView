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
import android.view.View;
import android.widget.Toast;

/**
 * Created by chenliu on 2016/11/24.<br/>
 * 描述：
 * </br>
 */
public class TestGestureDetector extends View {
    private Paint paint;

    private int mMove = 100;

    private GestureDetectorCompat gestureDetector;

    public TestGestureDetector(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(60);

        gestureDetector = new GestureDetectorCompat(getContext(), new SimpleGestureListener());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText("...TestSlide...", mMove, 100, paint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return true;
    }

    private class SimpleGestureListener extends
            GestureDetector.SimpleOnGestureListener {

        /*****OnGestureListener的函数*****/

        final int FLING_MIN_DISTANCE = 100, FLING_MIN_VELOCITY = 200;

        // 触发条件 ：
        // X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒

        // 参数解释：
        // e1：第1个ACTION_DOWN MotionEvent
        // e2：最后一个ACTION_MOVE MotionEvent
        // velocityX：X轴上的移动速度，像素/秒
        // velocityY：Y轴上的移动速度，像素/秒
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            Log.e("====", "onFling");

            if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                // Fling left
                Log.e("MyGesture", "Fling left");
            } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                // Fling right
                Log.e("MyGesture", "Fling right");
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            Log.e("====","onDown: " + event.toString());
            return true;
        }

    }

}











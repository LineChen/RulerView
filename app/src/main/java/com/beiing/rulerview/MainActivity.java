package com.beiing.rulerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.beiing.rulerview.widget.NewRulerView;
import com.beiing.rulerview.widget.TestScroller;

public class MainActivity extends AppCompatActivity {

    TestScroller scroller;

    NewRulerView newRulerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scroller = (TestScroller) findViewById(R.id.scroller);


        newRulerView = (NewRulerView) findViewById(R.id.new_ruler);

        newRulerView.setOnValueChangeListener(new NewRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                Log.e("====", "value=" + value);
            }
        });
    }

    public void left(View view) {
        scroller.smoothScrollBy(10, 0);
    }

    public void right(View view) {
        scroller.smoothScrollBy(-10, 0);
    }
}

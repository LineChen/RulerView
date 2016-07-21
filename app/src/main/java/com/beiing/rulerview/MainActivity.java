package com.beiing.rulerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.beiing.rulerview.widget.TestScroller;

public class MainActivity extends AppCompatActivity {

    TestScroller scroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scroller = (TestScroller) findViewById(R.id.scroller);
    }

    public void left(View view) {
        scroller.smoothScrollBy(10, 0);
    }

    public void right(View view) {
        scroller.smoothScrollBy(-10, 0);
    }
}

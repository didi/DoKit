package com.didichuxing.doraemondemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * 模拟内存泄漏的activity
 */
public class LeakActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak);
        App.leakActivity = this;
    }

}

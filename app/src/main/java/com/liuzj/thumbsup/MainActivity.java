package com.liuzj.thumbsup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private ThumbsUpLayout mThumbsUpLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mThumbsUpLayout = findViewById(R.id.lay_thumbsup);
    }

    public void thumbsUp(View view) {
        mThumbsUpLayout.addThumbs();

    }
}

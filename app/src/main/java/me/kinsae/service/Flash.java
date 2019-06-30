package me.kinsae.service;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Flash extends AppCompatActivity {
    private static int time_out = 800;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        logo = findViewById(R.id.logo);
        Animation fade_in = AnimationUtils.loadAnimation(this,R.anim.fadein);
        logo.startAnimation(fade_in);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){

                    Intent loginIntent = new Intent(Flash.this, MainActivity.class);
                    startActivity(loginIntent);
                    finish();
            }
        },time_out);
    }
}

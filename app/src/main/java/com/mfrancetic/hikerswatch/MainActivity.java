package com.mfrancetic.hikerswatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private Context context;

    private ImageView mainBackgroundImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainBackgroundImageView = findViewById(R.id.main_background_image_view);
        context = mainBackgroundImageView.getContext();


        Glide.with(context).load(R.drawable.forest).centerCrop().into(mainBackgroundImageView);
    }
}

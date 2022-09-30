package com.example.myapplication.balloons;

import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.skydoves.balloon.Balloon;


/**
 * Class containing balloons for MenuActivity
 */
public class MenuBalloons {

    private Balloon takePictureBtnBalloon;

    public MenuBalloons(AppCompatActivity context)
    {
        Resources res = context.getResources();

        Balloon.Builder builder = new BalloonBuilderFactory(context).createDefaultBuilder();

        takePictureBtnBalloon = builder.setText(res.getString(R.string.takePictureBtnBalloon)).build();
    }

    public Balloon getTakePictureBtnBalloon() {
        return takePictureBtnBalloon;
    }
}

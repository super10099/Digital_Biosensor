package com.example.myapplication.util;

import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

public class DataAnalysisBalloons {

    // balloons for tooltips
    private Balloon avgRGBBalloon;
    private Balloon avgRValBalloon;
    private Balloon avgRValSTDBalloon;
    private Balloon ratioBalloon;

    public DataAnalysisBalloons(AppCompatActivity context)
    {
        Resources res = context.getResources();

        // instantiate builder with preferred settings
        Balloon.Builder balloonBuilder = new Balloon.Builder(context)
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setWidth(BalloonSizeSpec.WRAP)
                .setPadding(4)
                .setTextSize(15f)
                .setCornerRadius(4f)
                .setAlpha(0.9f)
                .setText(res.getString(R.string.avg_rgb))
                .setTextColor(ContextCompat.getColor(context, R.color.msu_white))
                .setTextIsHtml(true)
                .setBackgroundColor(ContextCompat.getColor(context, R.color.msu_green))
                .setBalloonAnimation(BalloonAnimation.FADE);

        avgRGBBalloon = balloonBuilder
                .setText(res.getString(R.string.avg_rgb))
                .build();

        avgRValBalloon = balloonBuilder
                .setText(res.getString(R.string.avg_rval))
                .build();

        avgRValSTDBalloon = balloonBuilder
                .setText(res.getString(R.string.avg_rvalstd))
                .build();

        ratioBalloon = balloonBuilder
                .setText(res.getString(R.string.ratio))
                .build();
    }

    public Balloon getAvgRGBBalloon() {
        return avgRGBBalloon;
    }

    public Balloon getAvgRValBalloon() {
        return avgRValBalloon;
    }

    public Balloon getAvgRValSTDBalloon() {
        return avgRValSTDBalloon;
    }

    public Balloon getRatioBalloon() {
        return ratioBalloon;
    }
}

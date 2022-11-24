package com.bae.myapplication.balloons;

import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bae.myapplication.R;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;


/**
 * A factory class for creating Balloon Builders
 */
public class BalloonBuilderFactory {

    private Resources res;
    private AppCompatActivity context;

    public BalloonBuilderFactory(AppCompatActivity context)
    {
        res = context.getResources();
        this.context = context;
    }

    public Balloon.Builder createDefaultBuilder()
    {
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

        return balloonBuilder;
    }
}

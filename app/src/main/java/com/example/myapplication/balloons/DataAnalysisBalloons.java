package com.example.myapplication.balloons;

import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.skydoves.balloon.Balloon;

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
        Balloon.Builder balloonBuilder = new BalloonBuilderFactory(context).createDefaultBuilder();

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

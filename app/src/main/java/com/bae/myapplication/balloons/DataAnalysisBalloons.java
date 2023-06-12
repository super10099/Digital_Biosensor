package com.bae.myapplication.balloons;

import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import com.bae.myapplication.R;
import com.skydoves.balloon.Balloon;

public class DataAnalysisBalloons {

    // balloons for tooltips
    private Balloon avgRGBBalloon;
    private Balloon avgRValBalloon;
    //private Balloon avgRValSTDBalloon;
    private Balloon ratioBalloon;
    private Balloon snrBalloon;
    private Balloon alphaBalloon;

    public DataAnalysisBalloons(AppCompatActivity context)
    {
        Resources res = context.getResources();

        // instantiate builder with preferred settings
        Balloon.Builder balloonBuilder = new BalloonBuilderFactory(context).createDefaultBuilder();

        avgRGBBalloon = balloonBuilder
                .setText(res.getString(R.string.avg_rgb))
                .build();

        avgRValBalloon = balloonBuilder
                .setText(res.getString(R.string.alpha))
                .build();

        //avgRValSTDBalloon = balloonBuilder
        //        .setText(res.getString(R.string.avg_rvalstd))
        //        .build();

        ratioBalloon = balloonBuilder
                .setText(res.getString(R.string.ratio))
                .build();

        // Additions For Alpha and SNR tooltips
        alphaBalloon = balloonBuilder
                .setText(res.getString(R.string.alpha))
                .build();

        snrBalloon = balloonBuilder
                .setText(res.getString(R.string.snr))
                .build();

    }

    public Balloon getAvgRGBBalloon() {
        return avgRGBBalloon;
    }

    public Balloon getAvgRValBalloon() {
        return avgRValBalloon;
    }

//    public Balloon getAvgRValSTDBalloon() {
//        return avgRValSTDBalloon;
//    }

    public Balloon getRatioBalloon() {
        return ratioBalloon;
    }

    public Balloon getSnrBalloon() {return snrBalloon;}
    public Balloon getAlphaBalloon() {return  alphaBalloon;}
}

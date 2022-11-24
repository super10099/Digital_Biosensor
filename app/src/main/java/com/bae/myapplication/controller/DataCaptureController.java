package com.bae.myapplication.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bae.myapplication.activities.DataCaptureActivity;
import com.bae.myapplication.model.DataCaptureModel;

/**
 * Controller between DataCaptureModel and DataCaptureActivity(View)
 */
public class DataCaptureController {


    private DataCaptureActivity dataCaptureActivity;
    private DataCaptureModel dataCaptureModel;

    /**
     * Start of DataCapture phase.
     */
    public void startActivity(Context context, Bundle extras) {
        Intent act = new Intent(context, DataCaptureActivity.class);
        act.putExtras(extras);
        context.startActivity(act);
    }

    /**
     * After DataCaptureActivity is done sampling the points
     * this method switches to next activity.
     */
    public void startDataAnalysisActivity() {

    }


    /**
     * Let Model know to start the calculation.
     */
    public void startSampleCalculations() {

    }


}

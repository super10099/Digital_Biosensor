package com.example.myapplication.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.util.SelectDataSetContract;


//import com.jjoe64.graphview.*;

/**
 * Activity for displaying trends from multiple data captures.
 */
public class DataGraphViewActivity extends AppCompatActivity
{

    private ActivityResultLauncher<DataGraphViewActivity> launcher = registerForActivityResult(new SelectDataSetContract(),
            result ->
            {
                Log.d("DEBUG", result.toString());
            });


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // disable title bar
        getSupportActionBar().hide();

        // set the main layout
        setContentView(R.layout.activity_datagraphview);

        // set orientation of the device
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ImageButton addDataSetsBtn = findViewById(R.id.GraphViewAddDataSetsBtn);
        addDataSetsBtn.setOnTouchListener(new OnAddDataSetsBtn());
    }

    private void switchToBrowseSavedDataActivity()
    {
        launcher.launch(this);
    }

    private class OnAddDataSetsBtn implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                switchToBrowseSavedDataActivity();
                return true;
            }
            return false;
        }
    }
}

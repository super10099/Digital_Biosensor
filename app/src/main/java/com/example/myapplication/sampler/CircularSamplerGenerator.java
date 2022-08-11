

package com.example.myapplication.sampler;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.myapplication.activities.DataCaptureActivity;
import com.example.myapplication.datasystem.DataCaptureSettings;
import com.example.myapplication.util.Visitor;

import java.util.ArrayList;

/**
 * This class generates CircularSamplers. The CircularSampler(s) will sample an area given the number
 */
public class CircularSamplerGenerator
{
    private final DataCaptureSettings settings;

    // Activity to get context and such.
    private final DataCaptureActivity tookAct;

    // a list of all the samplers that was generated
    private final ArrayList<CircularSampler> samplers = new ArrayList<>();

    /**
     * Iterate through its samplers and make them compute their values
     */
    public void doTrials()
    {
        for (CircularSampler s : samplers) {
            s.doTrials();
        }
    }

    /**
     * Needs the view to attach an event listener to it.
     *
     * @param tookAct The Activity of TookPicture.
     * @param view    The view representing the circle that responds to user input.
     */
    public CircularSamplerGenerator(DataCaptureActivity tookAct, View view, DataCaptureSettings settings)
    {
        Log.d("DEBUG", "Creating a CircularSamplerGenerator");

        // need the Activity environment for inflater and finding views
        this.tookAct = tookAct;
        this.settings = settings;

        // attach listener that will generate a new sampler on touch
        view.setOnTouchListener(new onTouchCreateSampler());
    }

    /**
     * Accept visitor
     */
    public void acceptVisitor(Visitor visitor)
    {
        for (CircularSampler s : samplers) { s.acceptVisitor(visitor); }
    }


    /**
     * Create a new sampler everytime the view has been touched
     */
    private class onTouchCreateSampler implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                // create new sampler at specified location (relative to bitmap)
                int[] baseCoords = new int[2];
                tookAct.getPictureView().getLocationOnScreen(baseCoords);

                int xPos = (int) (event.getRawX() - baseCoords[0]);
                int yPos = (int) (event.getRawY() - baseCoords[1]);

//                int xPos = (int) (v.getX() + event.getX());
//                int yPos = (int) (v.getY() + event.getY());

                CircularSampler newSampler = new CircularSampler(
                        tookAct,
                        settings,
                        xPos, yPos);

                samplers.add(newSampler);
            }

            return true;
        }
    }

}



package com.example.myapplication.sampler;

import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

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
        for (CircularSampler s : samplers)
        {
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
        view.setOnTouchListener(new CreateSamplerOnTouchListener());
    }

    /**
     *
     */
    public void redraw()
    {
        for (CircularSampler sampler : samplers)
        {
            sampler.redraw();
        }
    }

    /**
     * Accept visitor
     */
    public void acceptVisitor(Visitor visitor)
    {
        for (CircularSampler s : samplers) {s.acceptVisitor(visitor);}
    }


    /**
     * Create a new sampler everytime the view has been touched
     */
    private class CreateSamplerOnTouchListener implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                v.playSoundEffect(SoundEffectConstants.CLICK);
                CircularSampler newSampler = new CircularSampler(tookAct, settings);
                samplers.add(newSampler);

                // set name of sampler
                String name;
                int ith = samplers.size();
                if (ith == 1)
                {
                    name = "control";
                }
                else
                {
                    name = String.format("x%d", ith - 1);
                }
                newSampler.setName(name);
            }

            return true;
        }
    }

}

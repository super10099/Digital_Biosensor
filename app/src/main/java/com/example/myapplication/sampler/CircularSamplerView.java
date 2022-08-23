package com.example.myapplication.sampler;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.myapplication.R;
import com.example.myapplication.activities.DataCaptureActivity;


/**
 * Class that represents the View of CircularSampler.
 */
public class CircularSamplerView
{
    private final ImageView circleView;
    private final ImageView stickView;
    private final TextView labelView;

    private final float[] circleViewOffset = new float[2];
    private final float[] labelViewOffset = new float[2];

    private CircularSampler sampler;

    // for translating the view
    private float dX, dY;

    // only true after circleView is posted (ready).
    boolean viewPosted = false;

    private final DataCaptureActivity dcAct;

    /**
     * Instantiate the views as well as attach listeners to them.
     */
    @SuppressLint("ClickableViewAccessibility")
    public CircularSamplerView(DataCaptureActivity dcAct)
    {
        this.dcAct = dcAct;
        ConstraintLayout cL = dcAct.findViewById(R.id.cLCircularSamplers);

        // Inflate sampler (target, stick, label)
        ConstraintLayout samplerCL = (ConstraintLayout) dcAct.getLayoutInflater()
                .inflate(R.layout.circularsampler_circle_test, cL, false);
        cL.addView(samplerCL);

        circleView = samplerCL.findViewById(R.id.samplerTarget);
        stickView = samplerCL.findViewById(R.id.samplerStick);
        labelView = samplerCL.findViewById(R.id.samplerLabel);

        stickView.setOnTouchListener(new onDragListener());
    }

    /**
     * Set the CircularSampler that the view represents.
     *
     * @param sampler CircularSampler of the view.
     */
    public void setCircularSampler(CircularSampler sampler)
    {
        this.sampler = sampler;
    }

    /**
     * Only call after setting the sampler.
     * Set up views after they are finished laying out.
     */
    public void show()
    {
        stickView.post(() ->
        {
            viewPosted = true;
            update(stickView.getX(), stickView.getY());  // set x and y based on xml starting layout
        });

        labelView.post(() ->
            {
                labelView.setText(sampler.getName());
                // get the initial starting offset relative to circleView
                circleViewOffset[0] = circleView.getX() - stickView.getX();
                circleViewOffset[1] = circleView.getY() - stickView.getY();
                labelViewOffset[0] = labelView.getX() - stickView.getX();
                labelViewOffset[1] = labelView.getY() - stickView.getY();
            });
    }


    /**
     * Handles on the event that the view is being dragged.
     */
    private class onDragListener implements View.OnTouchListener
    {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event)
        {
            if (viewPosted)
            {
                // disable scrollview from intercepting drag
                stickView.getParent().requestDisallowInterceptTouchEvent(true);

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        dX = -event.getX();
                        dY = -event.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        int[] pos = new int[2];
                        dcAct.getPictureView().getLocationOnScreen(pos);

                        // get positions relative to pictureView
                        float x = event.getRawX() - pos[0];
                        float y = event.getRawY() - pos[1];

                        // update the model
                        update(x + dX, y + dY);
                        draw();

                        break;

                    default:
                        return false;
                }
            }

            return true;
        }
    }

    /**
     *
     */
    private void update(float x, float y)
    {
        sampler.setX(x);
        sampler.setY(y);
    }

    /**
     *
     */
    private void draw()
    {
        stickView.setX(sampler.getX());
        stickView.setY(sampler.getY());

        // draw in the peripherals
        circleView.setX(stickView.getX() + circleViewOffset[0]);
        circleView.setY(stickView.getY() + circleViewOffset[1]);
        labelView.setX(stickView.getX() + labelViewOffset[0]);
        labelView.setY(stickView.getY() + labelViewOffset[1]);
    }
}
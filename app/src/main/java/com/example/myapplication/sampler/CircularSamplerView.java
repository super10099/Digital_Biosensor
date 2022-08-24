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

    private final float crosshair_buffer_zone = 1.5f;

    private final CircularSampler sampler;

    // for translating the view
    private float dX, dY;

    // only true after circleView is posted (ready).
    boolean viewPosted = false;

    private final DataCaptureActivity dcAct;

    /**
     * Instantiate the views as well as attach listeners to them.
     */
    @SuppressLint("ClickableViewAccessibility")
    public CircularSamplerView(DataCaptureActivity dcAct, CircularSampler sampler)
    {
        this.dcAct = dcAct;
        this.sampler = sampler;

        ConstraintLayout cL = dcAct.findViewById(R.id.cLCircularSamplers);

        // Inflate sampler (target, stick, label)
        ConstraintLayout samplerCL = (ConstraintLayout) dcAct.getLayoutInflater()
                .inflate(R.layout.circularsampler_sampler, cL, false);
        cL.addView(samplerCL, 0);

        circleView = samplerCL.findViewById(R.id.samplerTarget);
        stickView = samplerCL.findViewById(R.id.samplerStick);
        labelView = samplerCL.findViewById(R.id.samplerLabel);

        stickView.setOnTouchListener(new onDragListener());

        samplerCL.post(() -> {
            // set text label
            labelView.setText(sampler.getName());

            // adjust size of circle based on settings
            adjustCircle();

            // get the initial starting offset relative to stick
            labelViewOffset[0] = labelView.getX() - stickView.getX();
            labelViewOffset[1] = labelView.getY() - stickView.getY();

            // update y-pos based on vertical scroll
            update(stickView.getX(), stickView.getY() + dcAct.getScrollView().getScrollY());
            draw();
            viewPosted = true;
        });
    }

    /**
     *
     */
    private void adjustCircle()
    {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) circleView.getLayoutParams();
        float factor = dcAct.getApplicationContext().getResources().getDisplayMetrics().density;
        int radius = sampler.getSettings().getSamplerRadius();  // radius is in density independent pixels
        radius *= crosshair_buffer_zone;    // buffer zone, since the crosshair is a bit smaller than the actual radius
        params.width = (int)(2 * radius * factor);
        params.height = (int)(2 * radius * factor);
        circleView.setX(stickView.getX() - (float)params.width / 2);
        circleView.setY(stickView.getY() - (float)params.height / 2);

        // update offset
        circleViewOffset[0] = circleView.getX() - stickView.getX();
        circleViewOffset[1] = circleView.getY() - stickView.getY();
    }


    /**
     * Only called when needing to adjust circle
     */
    public void redraw()
    {
        adjustCircle();
        circleView.requestLayout();
        draw();
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

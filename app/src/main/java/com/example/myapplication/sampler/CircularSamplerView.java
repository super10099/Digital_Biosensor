package com.example.myapplication.sampler;

import android.annotation.SuppressLint;
import android.util.Log;
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
    private static final int WEIRD_OFFSET = 5; // Needed for sampler to be in center of view

    private final ImageView circleView;
    private final TextView labelView;

    private CircularSampler sampler;

    // for translating the view
    private float dX, dY;

    // only true after circleView is posted (ready).
    boolean viewPosted = false;

    /**
     * Instantiate the views as well as attach listeners to them.
     */
    @SuppressLint("ClickableViewAccessibility")
    public CircularSamplerView(DataCaptureActivity dcAct)
    {
        ConstraintLayout cL = dcAct.findViewById(R.id.cLCircularSamplers);

        // Inflate circle
        circleView = (ImageView) dcAct.getLayoutInflater()
                .inflate(R.layout.circularsampler_circle, cL, false);
        cL.addView(circleView);
        circleView.setOnTouchListener(new onDragListener());

        // inflate label
        labelView = (TextView) dcAct.getLayoutInflater()
                .inflate(R.layout.circularsampler_label, cL, false);
        cL.addView(labelView);
    }

    /**
     * Get the view's x position
     */
    private float getX()
    {
        return (sampler.getX()) - (float) (circleView.getWidth() / 2);
    }

    /**
     * Get the view's y position
     */
    private float getY()
    {
        return (sampler.getY()) - (float) (circleView.getHeight() / 2);
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
     * Animate views to their positions.
     */
    public void show()
    {
        circleView.post(() ->
        {
            viewPosted = true;
            circleView.animate().x(getX());
            circleView.animate().y(getY());
            circleView.animate().withEndAction(() ->
                    Log.d("DEBUG", String.format("Animating to %f,%f",
                            circleView.getTranslationX(), circleView.getTranslationY())
                    )
            );
        });

        labelView.post(() ->
        {
            labelView.setText(sampler.getName());
            labelView.animate().x(getX());
            labelView.animate().y(getY());
        });
    }


    /**
     * Handles on the event that the view is being dragged.
     * Attributed drag and move from https://stackoverflow.com/questions/9398057/android-move-a-view-on-touch-move-action-move
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
                circleView.getParent().requestDisallowInterceptTouchEvent(true);

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        dX = circleView.getX() - event.getRawX();
                        dY = circleView.getY() - event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        // update the logical position of sampler
                        sampler.setX(event.getRawX() + dX + (float) circleView.getWidth() / 2 - WEIRD_OFFSET);
                        sampler.setY(event.getRawY() + dY + (float) circleView.getWidth() / 2 - WEIRD_OFFSET);

                        // do the animation
                        view.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();

                        break;

                    default:
                        return false;
                }
            }

            return true;
        }
    }
}
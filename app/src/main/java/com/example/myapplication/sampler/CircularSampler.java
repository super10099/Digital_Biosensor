package com.example.myapplication.sampler;

import android.graphics.Point;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.myapplication.R;
import com.example.myapplication.activities.DataCaptureActivity;
import com.example.myapplication.datasystem.DataCaptureSettings;
import com.example.myapplication.datasystem.DataProcessor;
import com.example.myapplication.util.Visitor;

import java.util.ArrayList;
import java.util.Random;


/**
 * A sampler that takes sampling points within a circular area.
 */
public class CircularSampler
{
    private static final int ROOT_LAYOUT_ID = R.id.cLCircularSamplers; /// Layout to store the views
    private static final int VIEW_VISUALDOT_LAYOUT = R.layout.circularsampler_visualdot;

    private final DataProcessor dProcessor = new DataProcessor();   /// brain of this sampler

    private final DataCaptureActivity dcAct;

    private final DataCaptureSettings settings;
    private float x;   /// x position of circle
    private float y;   /// y position of circle

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    /**
     * Instantiate its member variables.
     * Instantiate a view for itself.
     */
    public CircularSampler(DataCaptureActivity dcAct, DataCaptureSettings settings,
                           float x, float y)
    {
        this.settings = settings;
        this.x = x;
        this.y = y;
        this.dcAct = dcAct;

        CircularSamplerView samplerView = new CircularSamplerView(dcAct);
        samplerView.setCircularSampler(this);
        samplerView.show();
    }

    /**
     * Given the numTrials, perform the trials
     * As in, calculate the rPoints of the position of each trial.
     */
    public void doTrials()
    {
        // need to turn the list of positions to a list of colors.
        // add each color to the data processor and let it handle the calculations.
        ArrayList<Point> positions = generatePositions();
        for (Point p : positions)
        {
            // The points fetched from touch position is based on a scaled image
            // Need to transform the position from scaled to unscaled by dividing
            // by the scale factor.
            int color = dcAct.getPixelFromPosition(p);
            dProcessor.addSampleColor(color);
        }

        dProcessor.start(); // start computing the sampling points
    }

    /**
     * Generate the sampling positions. These positions are based on the scaled image.
     * Uses numTrials and radius, and position of the circle.
     * // NOTE: the positions generated are absolute
     * // when taking pixels from Bitmap, need to make the absolute positions
     * // relative to pictureView.
     */
    public ArrayList<Point> generatePositions()
    {
        // Uses polar coordinates to calculate
        // Need to randomize the angle(radians) and the magnitude
        Random randomGenerator = new Random();
        ArrayList<Point> positions = new ArrayList<>();
        ConstraintLayout cL = dcAct.findViewById(ROOT_LAYOUT_ID);
        int radius = settings.getSamplerRadius();

        for (int i = 0; i < settings.getNumSamplingPoints(); i++)
        {
            // randomly generate magnitude and radians
            double magnitude = randomGenerator.nextDouble() * radius;
            double rads = randomGenerator.nextDouble() * 2 * Math.PI;

            // make a new position and make the position relative to the sampler's center
            // floating point for greater precision, but convert to integer point at the end
            float xLength = (float) (Math.cos(rads) * magnitude);
            float yLength = (float) (Math.sin(rads) * magnitude);
            float xPos = x + xLength;
            float yPos = y + yLength;
            Point position = new Point(Math.round(xPos), Math.round(yPos));
            positions.add(position);
            generateVisualDot(cL, position);
        }

        return positions;
    }


    /**
     * Generate dots to show to the user where each point was generated.
     *
     * @param cL Parent layout for visualdot view.
     */
    private void generateVisualDot(ConstraintLayout cL, Point position)
    {
        // Commented out due to memory bursts. Race condition, possibly.
//        ImageView visualDotView = (ImageView) dcAct.getLayoutInflater()
//                .inflate(VIEW_VISUALDOT_LAYOUT, cL, false);
//
//        Point targetPoint = new Point(
//                Math.round(position.x - visualDotView.getWidth() / 2f),
//                Math.round(position.y - visualDotView.getHeight() / 2f)
//        );
//
//        cL.addView(visualDotView);
//        dcAct.animateVisualDot(visualDotView, targetPoint);
    }

    /**
     * Accept only DataProcessorCollectionVisitor
     */
    public void acceptVisitor(Visitor visitor)
    {
        visitor.visitSampler(dProcessor);
    }

}
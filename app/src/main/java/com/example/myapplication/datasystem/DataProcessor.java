package com.example.myapplication.datasystem;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

/**
 * Responsible for handling data processing
 */
public class DataProcessor
{

    /**
     * Color of each sampled point.
     */
    private final List<Integer> sampleColors;

    /**
     * rPoints of each sampled point.
     */
    private List<Double> rPoints;

    /**
     * ratio of the rPoint compared to the control sample.
     */
    private double comparativeValue;

    public DataProcessor()
    {
        sampleColors = new ArrayList<>();
    }

    /**
     * Add a sample color to the list.
     *
     * @param sampleColor color (int) to add.
     */
    public void addSampleColor(int sampleColor)
    {
        sampleColors.add(sampleColor);
    }

    /**
     * Calculates the values after all the data points are installed.
     */
    public void start()
    {
        rPoints = calculateRPoints();
    }

    /**
     * Do (r + g)/ b algorithm, this calculates the R point.
     *
     * @return a list of rPoints in order.
     */
    public List<Double> calculateRPoints()
    {
        List<Double> rPoints = new ArrayList<>();
        for (int i = 0; i < sampleColors.size(); i++)
        {
            int r = Color.red(sampleColors.get(i));
            int g = Color.green(sampleColors.get(i));
            int b = Color.blue(sampleColors.get(i));
            double rPoint = (double) (r + g) / b;
            rPoints.add(rPoint);
        }
        return rPoints;
    }

    /**
     * Calculate the average red values of each sampling point.
     *
     * @return Average red value
     */
    public double getAvgRValue()
    {
        double val = 0;
        for (int i = 0; i < sampleColors.size(); i++)
        {
            val += Color.red(sampleColors.get(i));
        }
        return val / sampleColors.size();
    }

    /**
     * Calculate the average green values of each sampling point.
     *
     * @return Average green value
     */
    public double getAvgGValue()
    {
        double val = 0;
        for (int i = 0; i < sampleColors.size(); i++)
        {
            val += Color.green(sampleColors.get(i));
        }
        return val / sampleColors.size();
    }

    /**
     * Calculate the average blue values of each sampling point.
     *
     * @return Average blue value
     */
    public double getAvgBValue()
    {
        double val = 0;
        for (int i = 0; i < sampleColors.size(); i++)
        {
            val += Color.blue(sampleColors.get(i));
        }
        return val / sampleColors.size();
    }

    /**
     * Calculate the average rPoint value among the sampled rPoints
     *
     * @return the average rPoint value.
     */
    public double getAvgRPoint()
    {
        double val = 0;
        for (Double d : rPoints)
        {
            val += d;
        }
        return val / rPoints.size();

        //return getAvgRValue() / (getAvgGValue() + getAvgBValue());
    }

    /**
     * Get the standard devation of the RPoints
     *
     * @return
     */
    public double getRPointSTD()
    {
        double mean = getAvgRPoint();
        double squaredDiffs = 0;

        for (Double d : rPoints)
        {
            squaredDiffs = Math.pow(d - mean, 2);
        }
        return squaredDiffs / rPoints.size();
    }

    /**
     * Setter for comparativeValue
     *
     * @param val
     */
    public void setComparativeValue(double val)
    {
        comparativeValue = val;
    }

    /**
     * Getter for comparativeValue
     *
     * @return
     */
    public double getComparativeValue()
    {
        return comparativeValue;
    }

    /** Get the tranformation from linear space to non-linear space
     *
     * @return
     */
    public double getTransformedValue()
    {
        return comparativeValue * Math.exp(comparativeValue);
    }
}

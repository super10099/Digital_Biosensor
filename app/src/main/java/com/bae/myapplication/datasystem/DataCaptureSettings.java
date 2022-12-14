package com.bae.myapplication.datasystem;

public class DataCaptureSettings
{
    private double imageScale = 5;
    private int samplerRadius = 5;
    private int numSamplingPoints = 5;

    public double getImageScale()
    {
        return imageScale;
    }

    public void setImageScale(double imageScale)
    {
        this.imageScale = imageScale;
    }

    public int getSamplerRadius()
    {
        return samplerRadius;
    }

    public void setSamplerRadius(int samplerRadius)
    {
        this.samplerRadius = samplerRadius;
    }

    public int getNumSamplingPoints()
    {
        return numSamplingPoints;
    }

    public void setNumSamplingPoints(int numSamplingPoints)
    {
        this.numSamplingPoints = numSamplingPoints;
    }
}

package com.bae.myapplication.sampler;

import com.bae.myapplication.datasystem.DataProcessor;
import com.bae.myapplication.util.Visitor;

import java.util.ArrayList;

/**
 * Visitor class that gathers all samplers from CircularSamplerGenerator to calculate the
 * comparative values of each sampler.
 */
public class ComparativeValueVisitor implements Visitor
{

    /// a list of visited DataProcessor(s)
    private final ArrayList<DataProcessor> dps = new ArrayList<>();

    @Override
    public void visitSampler(DataProcessor dp)
    {
        dps.add(dp);
    }

    /**
     * After visitor has a list of all the data from each visited sampler.
     * Then it can use those values to calculate the comparative values
     */
    public void calculateComparativeValues()
    {
        // The denominator of each calculation
        double controlRPoint = dps.get(0).getAvgRPoint();
        double transformedRPoint = nonlinearTransform(controlRPoint);

        // Iterate through the list of rPoints and calculate the comparative values
        // The comparative values are added to the list.
        for (DataProcessor dp : dps)
        {
            // double nComparativeVal = dp.getAvgRPoint() / controlRPoint;
            double nComparativeVal = nonlinearTransform(dp.getAvgRPoint()) / transformedRPoint;
            dp.setTransformedValue(nonlinearTransform(dp.getAvgRPoint()));
            dp.setComparativeValue(nComparativeVal);
        }
    }

    private double nonlinearTransform(double r)
    {
        return 1 / Math.exp(r);
    }
}

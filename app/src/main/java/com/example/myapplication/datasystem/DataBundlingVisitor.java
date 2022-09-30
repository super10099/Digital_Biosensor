package com.example.myapplication.datasystem;


import com.example.myapplication.util.ActivityTransitions;
import com.example.myapplication.util.Visitor;

import java.util.ArrayList;

/**
 * Storing the data into DataStore
 * First, it puts the encapsulate each sampler's data into a DataSet object
 * Second, it puts the DataSet object by calling a function of DataStore
 */
public class DataBundlingVisitor implements Visitor
{

    /**
     * String to pass and access extra in bundle passed between activities
     */
    public static String KEY_EXTRA_STRING = "uKey";

    /**
     * List of data processors
     */
    private ArrayList<DataProcessor> dps = new ArrayList<>();

    /**
     * Visit sampler to gather all data from each sampler and bundle for data storage
     *
     * @param dp DataProcessor of the visited sampler.
     */
    @Override
    public void visitSampler(DataProcessor dp)
    {
        dps.add(dp);
    }


    /**
     * Pack the gathered data into a DataSet object
     * Put the DataSet object into DataStore
     *
     * @return unique key corresponding to the DataSet
     */
    public String PackDataSet()
    {
        DataStore.DataSet newDS = new DataStore.DataSet();
        for (DataProcessor dp : dps)
        {
            newDS.newElement(dp.getAvgRValue(), dp.getAvgGValue(), dp.getAvgBValue(),
                    dp.getAvgRPoint(), dp.getRPointSTD(), dp.getComparativeValue(), dp.getTransformedValue());
        }

        // Store the new DataSet and retrieve a new unique key for future references
        return DataStore.primaryDataStore.putNewDataSet(newDS, ActivityTransitions.FROM_DATA_CAPTURE_ACTIVITY);
    }
}

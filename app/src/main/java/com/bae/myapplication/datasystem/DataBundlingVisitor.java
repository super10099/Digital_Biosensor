package com.bae.myapplication.datasystem;


import com.bae.myapplication.util.ActivityTransitions;
import com.bae.myapplication.util.DataCaptureModule;
import com.bae.myapplication.util.Visitor;

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
     * Save the data collected from the csGen
     * It first packs it into a DataCaptureModule and then send that module into DS.
     * @return
     */
    public String saveData()
    {
        DataCaptureModule module = new DataCaptureModule();
        for (DataProcessor dp : dps)
        {
            DataCaptureModule.Element elem = new DataCaptureModule.Element(
                    dp.getAvgRValue(), dp.getAvgGValue(), dp.getAvgBValue(),
                    dp.getAvgRPoint(), dp.getRPointSTD(), dp.getComparativeValue(), dp.getTransformedValue());
            module.putElement(elem);
        }
        return DataStore.primaryDataStore.putModule(module, ActivityTransitions.FROM_DATA_CAPTURE_ACTIVITY);
    }
}

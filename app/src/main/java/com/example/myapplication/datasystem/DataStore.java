/*
This class is responsible for handling the storing and retrieving of saved data.
-Thinh Nguyen
-6/21/22
 */

package com.example.myapplication.datasystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.example.myapplication.util.ActivityTransitions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;


/**
 * Responsible for dealing with databases for this application.
 * By such, I mean the management of the data to be stored and retrieved.
 */
public class DataStore
{
    private static final String SERIALIZE_FILE_EXT = ".ser";
    private static final String TEMP_DIRECTORY_NAME = "temp";
    private static final String SAVE_DIRECTORY_NAME = "datasets";
    private static final String UKEY_SEPARATOR = "__";

    /**
     * There will only be one data store (handles the app's database)
     * Initiated during MainActivity
     */
    public static DataStore PrimaryDataStore;

    /**
     * reference allows access to Internal Storage
     */
    private Context appContext;

    /**
     * Directory to temporary data folder
     * The directory is cleared each time the application starts.
     */
    private final File tempDir;

    /**
     * Directory to saved data
     */
    private final File saveDir;

    public File getTempDir()
    {
        return tempDir;
    }

    public File getSaveDir()
    {
        return saveDir;
    }


    /**
     * Declare application's Context object.
     */
    public DataStore(Context context)
    {
        appContext = context;
        saveDir = new File(appContext.getFilesDir(), SAVE_DIRECTORY_NAME);
        tempDir = new File(appContext.getFilesDir(), TEMP_DIRECTORY_NAME);
        if (!saveDir.exists()) {saveDir.mkdir();}
        if (!tempDir.exists()) {tempDir.mkdir();}

        // Clear temporary directory
        for (File f : tempDir.listFiles())
        {
            if (!f.delete())
            {
                Log.d("DEBUG", "Failed to delete temporary file.");
            }
        }
    }

    /**
     * Conform the filename to the standards.
     *
     * @return a filename corresponding to the naming conventions.
     */
    private String filenameConform(String filename)
    {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        return timeStamp + UKEY_SEPARATOR + filename;
    }

    /**
     * Store new data and return a unique key for the data
     *
     * @param dSet Collection of data processors that contain the data to store
     * @return A key as a reference to the stored data
     */
    public String putNewDataSet(DataSet dSet, ActivityTransitions from)
    {
        return putNewDataSet(dSet, from, null);
    }

    /**
     * Store new data and return a unique key for the data
     *
     * @param dSet Collection of data processors that contain the data to store
     * @return A key as a reference to the stored data
     */
    public String putNewDataSet(DataSet dSet, ActivityTransitions from, String filename)
    {
        String uKey;
        Date dateCreated = Calendar.getInstance().getTime();
        File dir;

        if (from == ActivityTransitions.FROM_DATA_CAPTURE_ACTIVITY)
        {
            uKey = filenameConform("NA");
            dSet.setMetaData(uKey, dateCreated);
            dir = getTempDir();
        } else
        {
            uKey = filenameConform(filename);
            dSet.setMetaData(uKey, dateCreated);
            dir = getSaveDir();
        }
        saveDSet(dSet, uKey, dir);

        return uKey;
    }

    /**
     * Save DataSet into local storage
     * Need ApplicationContext to access Internal Storage.
     *
     * @param dSet DataSet to save to local storage
     */
    private void saveDSet(DataSet dSet, String uKey, File dir)
    {
        File file = new File(dir, uKey + SERIALIZE_FILE_EXT);

        try
        {
            FileOutputStream fileOS = new FileOutputStream(file);
            ObjectOutputStream fileOOS = new ObjectOutputStream(fileOS);
            fileOOS.writeObject(dSet);
        } catch (IOException e)
        {
            Log.d("DEBUG", e.toString());
        }
    }

    /**
     * Load a DataSet from Internal Storage
     *
     * @param uKey Unique key corresponding to a DataSet in Internal Storage.
     * @param from Which activity requested the dataset to be viewed.
     * @return The loaded DataSet that matched the unique key.
     */
    public DataSet loadDSet(String uKey, ActivityTransitions from)
    {
        File dirTarget;
        switch (from)
        {
            case FROM_DATA_CAPTURE_ACTIVITY:
                dirTarget = getTempDir();
                break;
            default:
                dirTarget = getSaveDir();
                break;
        }

        File file = new File(dirTarget, uKey + SERIALIZE_FILE_EXT);
        DataSet dSet = null;

        try
        {
            FileInputStream fileIS = new FileInputStream(file);
            ObjectInputStream fileOIS = new ObjectInputStream(fileIS);
            dSet = (DataSet) fileOIS.readObject();
        } catch (IOException | ClassNotFoundException e)
        {
            Log.d("DEBUG", e.toString());
        }

        return dSet;
    }


    /**
     * Provides meta data for datasets
     * Useful for displaying datasets in graph (X-axis as date).
     */
    private static class DataSetMetaData implements Serializable
    {
        private String name;
        private Date date;

        public DataSetMetaData(String name, Date date)
        {
            this.name = name;
            this.date = date;
        }

        public String getName()
        {
            return name;
        }

        public Date getDate()
        {
            return date;
        }
    }

    /**
     * An abstract data model to store data from each data capture activity.
     */
    public static class DataSet implements Serializable
    {

        private DataSetMetaData metaData;

        /**
         * Element of the data set.
         * Each element represents data from a tube.
         */
        public class DataSetElement implements Serializable
        {
            private double avgR;
            private double avgG;
            private double avgB;
            private double rPoint;
            private double rPointSTD;
            private double comparativeValue;

            public DataSetElement(double avgR, double avgG, double avgB, double rPoint, double rPointSTD, double comparativeValue)
            {
                this.avgR = avgR;
                this.avgG = avgG;
                this.avgB = avgB;
                this.rPoint = rPoint;
                this.rPointSTD = rPointSTD;
                this.comparativeValue = comparativeValue;
            }

            public double getAvgR()
            {
                return avgR;
            }

            public double getAvgG()
            {
                return avgG;
            }

            public double getAvgB()
            {
                return avgB;
            }

            public double getRPoint()
            {
                return rPoint;
            }

            public double getRPointSTD()
            {
                return rPointSTD;
            }

            public double getComparativeValue()
            {
                return comparativeValue;
            }
        }

        private ArrayList<DataSetElement> elements = new ArrayList<>();

        /**
         * Constructor
         */
        public DataSet()
        {
        }


        /**
         * Set the meta data of this DataSet
         * @param name Name of DataSet
         * @param dateCreated Date created
         */
        public void setMetaData(String name, Date dateCreated)
        {
            metaData = new DataSetMetaData(name, dateCreated);
        }


        /**
         * get MetaData
         * @return
         */
        public DataSetMetaData getMetaData() { return metaData; }


        /**
         * Add a new element to the dataset with the given values
         *
         * @param avgR
         * @param avgG
         * @param avgB
         * @param rPoint
         * @param rPointSTD
         * @param comparativeValue
         */
        public void newElement(double avgR, double avgG, double avgB, double rPoint, double rPointSTD, double comparativeValue)
        {
            DataSetElement newElem = new DataSetElement(avgR, avgG, avgB, rPoint, rPointSTD, comparativeValue);
            elements.add(newElem);
        }

        /**
         * @return A raw string that represents the DataSet as a CSV file extension.
         */
        public String getCSV()
        {
            StringBuilder rawCSV = new StringBuilder(new String());

            // append column titles
            rawCSV.append("avgR,avgG,avgB,rPoint,rPointSTD,comparativeValue\n");

            // for each element, append a row
            for (DataSetElement elem : elements)
            {
                @SuppressLint("DefaultLocale") String newRow = String.format("%f,%f,%f,%f,%f,%f\n",
                        elem.getAvgR(), elem.getAvgG(), elem.getAvgB(),
                        elem.getRPoint(), elem.getRPointSTD(), elem.getComparativeValue());
                rawCSV.append(newRow);
            }

            return rawCSV.toString();
        }

        /**
         * @return An iterator for the collection of DataSetElements this DataSet has.
         */
        public Iterator<DataSetElement> getIterator()
        {
            return elements.iterator();
        }

    }

    /**
     * @return A list of saved DataSet(s) saved in the internal storage.
     */
    public ArrayList<DataSetListViewModel> getDataSetListViewModels()
    {
        ArrayList<DataSetListViewModel> models = new ArrayList<>();
        File[] dsFiles = saveDir.listFiles();

        for (File f : dsFiles)
        {
            String fName = f.getName(); // name of file
            String[] tokens = fName.split(UKEY_SEPARATOR);
            String name = tokens[1];
            String date = tokens[0];
            String uKey = fName.split("\\.")[0];  // exclude file extension
            DataSetListViewModel model = new DataSetListViewModel(name, date, uKey);
            models.add(model);
        }

        return models;
    }

    /**
     * A data model for ListView (What to display in a ListView paradigm)
     */
    public static class DataSetListViewModel
    {
        private final String name;
        private final String date;
        private final String uKey;

        public DataSetListViewModel(String name, String date, String uKey)
        {
            this.name = name;
            this.date = date;
            this.uKey = uKey;
        }

        public String getName()
        {
            return name;
        }

        public String getDate()
        {
            return date;
        }

        public String getUKey()
        {
            return uKey;
        }
    }
}

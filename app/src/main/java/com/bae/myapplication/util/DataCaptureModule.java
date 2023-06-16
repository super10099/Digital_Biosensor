package com.bae.myapplication.util;

import android.annotation.SuppressLint;

import com.bae.myapplication.datasystem.DataModule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class contains all the fields that are captured during DataCapture process
 */
public class DataCaptureModule extends DataModule implements Serializable
{
    /**
     * Element of the data set.
     * Each element represents data from a tube.
     */
    public static class Element implements Serializable
    {
        private DataCaptureModule parent;
        private double avgR;
        private double avgG;
        private double avgB;
        private double rPoint;
        private double rPointSTD;
        private double comparativeValue;
        private double transformedValue;

        private double alpha;
        private double beta;
        private double snr;

        public Element(double avgR, double avgG, double avgB, double rPoint, double rPointSTD,
                       double comparativeValue, double transformedValue, double alpha, double beta,
                       double snr)
        {
            this.avgR = avgR;
            this.avgG = avgG;
            this.avgB = avgB;
            this.rPoint = rPoint;
            this.rPointSTD = rPointSTD;
            this.comparativeValue = comparativeValue;
            this.transformedValue = transformedValue;
            this.alpha = alpha;
            this.beta = beta;
            this.snr = snr;
        }

        /** Getters */
        public double getAvgR() { return avgR; }
        public double getAvgG() { return avgG; }
        public double getAvgB() {return avgB; }
        public double getRPoint() { return rPoint; }
        public double getRPointSTD() { return rPointSTD; }
        public double getComparativeValue() { return comparativeValue; }
        public double getTransformedValue() { return transformedValue; }

        public double getAlpha() {return alpha;}
        public double getBeta() {return beta;}
        public double getSnr() {return snr;}


        public DataCaptureModule getParent() { return parent; }

        public void setParent(DataCaptureModule parent) { this.parent = parent; }

    }

    // Contains the samples. e.g. control, x1, x2, x3, x4
    private ArrayList<Element> elements = new ArrayList<>();


    /**
     * Add a new element to the module
     */
    public void putElement(Element elem)
    {
        elem.setParent(this);
        elements.add(elem);
    }


    // Add beta, alpha, snr to csv file
    /**
     * @return A raw string that represents the DataSet as a CSV file extension.
     */
    public String getCSV()
    {
        StringBuilder rawCSV = new StringBuilder(new String());

        // append column titles
        rawCSV.append("\"avg(R,G,B)\",rPoint,rPointSTD,TransformedValue,Alpha, Beta,SNR\n");

        // for each element, append a row
        for (Element elem : elements)
        {
            @SuppressLint("DefaultLocale") String newRow = String.format("\"(%.0f, %.0f, %.0f)\",%f,%f,%f,%f,%f,%f\n",
                    elem.getAvgR(), elem.getAvgG(), elem.getAvgB(),
                    elem.getRPoint(), elem.getRPointSTD(), elem.getTransformedValue(),
                    elem.getAlpha(), elem.getBeta(), elem.getSnr());
            rawCSV.append(newRow);
        }

        return rawCSV.toString();
    }

    /**
     * @return An iterator for the collection of DataSetElements this DataSet has.
     */
    public Iterator<Element> getIterator()
    {
        return elements.iterator();
    }
}
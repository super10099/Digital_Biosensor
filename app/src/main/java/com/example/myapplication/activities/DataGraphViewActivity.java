package com.example.myapplication.activities;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.myapplication.R;
import com.example.myapplication.datasystem.DataModule;
import com.example.myapplication.datasystem.DataStore;
import com.example.myapplication.util.ActivityTransitions;
import com.example.myapplication.util.DataCaptureModule;
import com.example.myapplication.util.SelectDataSetContract;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;


/**
 * Activity for displaying trends from multiple data captures.
 */
public class DataGraphViewActivity extends AppCompatActivity
{
    private final static double UNIQUE_SATURATION = 0.50;
    private final static double UNIQUE_VALUE = 0.70;

    /**
     * DataSets to display is loaded here
     */
    private final ArrayList<DataCaptureModule> loadedDataModules = new ArrayList<>();

    /**
     * Activity Launcher for receiving selected datasets
     */
    private final ActivityResultLauncher<DataGraphViewActivity> selectDataLauncher =
            registerForActivityResult(new SelectDataSetContract(), new selectDataCallBack());


    private ImageButton addDataSetBtn;
    private Button exitBtn;
    private XYPlot plot;


    /**
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // disable title bar
        getSupportActionBar().hide();

        // set the main layout
        setContentView(R.layout.activity_datagraphview);

        // set orientation of the device
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        addDataSetBtn = findViewById(R.id.GraphViewAddDataSetsBtn);
        exitBtn = findViewById(R.id.GraphView_ExitBtn);
        plot = findViewById(R.id.GraphView_Plot);

        addDataSetBtn.setOnClickListener(new AddDataSetsOnClickListener());
        exitBtn.setOnClickListener(new ExitOnClickListener());
    }


    /**
     * Launch the ActivityForResult for user to select a list of DataSets.
     */
    private void switchToBrowseSavedDataActivity()
    {
        selectDataLauncher.launch(this);
    }

    /**
     * https://martin.ankerl.com/2009/12/09/how-to-create-random-colors-programmatically/
     *
     * @return
     */
    private int unique_color()
    {
        final double golden_ratio_conjugate = 0.618033988749895;
        double h = Math.random();
        h += golden_ratio_conjugate;
        h %= 1;
        return hsvToRGB(h, UNIQUE_SATURATION, UNIQUE_VALUE);
    }

    /**
     * https://martin.ankerl.com/2009/12/09/how-to-create-random-colors-programmatically/
     *
     * @param h
     * @param s
     * @param v
     * @return
     */
    private int hsvToRGB(double h, double s, double v)
    {
        int h_i = (int) (h * 6);
        double f = h * 6 - h_i;
        double p = v * (1 - s);
        double q = v * (1 - f * s);
        double t = v * (1 - (1 - f) * s);

        double r = 0, g = 0, b = 0;
        if (h_i == 0)
        {
            r = v;
            g = t;
            b = p;
        }
        if (h_i == 1)
        {
            r = q;
            g = v;
            b = p;
        }
        if (h_i == 2)
        {
            r = p;
            g = v;
            b = t;
        }
        if (h_i == 3)
        {
            r = p;
            g = q;
            b = v;
        }
        if (h_i == 4)
        {
            r = t;
            g = p;
            b = v;
        }
        if (h_i == 5)
        {
            r = v;
            g = p;
            b = q;
        }

        return Color.rgb((int) (r * 256), (int) (g * 256), (int) (b * 256));
    }

    /**
     * Called after datasets are loaded.
     * Display the loaded datasets.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayDataModules()
    {
        plot.clear();

        loadedDataModules.sort(Comparator.comparing(DataModule::getDate));

        // get datasets into a 2D array to iterator by column major
        // make the array is normal (all rows have same sizes)
        ArrayList<ArrayList<DataCaptureModule.Element>> arr2D = new ArrayList<>();
        for (DataCaptureModule m : loadedDataModules)
        {
            Iterator<DataCaptureModule.Element> it = m.getIterator();
            ArrayList<DataCaptureModule.Element> row = new ArrayList<>();
            while (it.hasNext())
            {
                row.add(it.next());
            }
            arr2D.add(row);
        }

        // skip first column (skip control series)
        for (int col = 1; col < arr2D.get(0).size(); col++)
        {
            ArrayList<Long> seriesNumbersX = new ArrayList<>();
            ArrayList<Double> seriesNumbersY = new ArrayList<>();
            for (int row = 0; row < arr2D.size(); row++)
            {
                DataCaptureModule.Element elem = arr2D.get(row).get(col);
                seriesNumbersX.add(elem.getParent().getDate().getTime());
                seriesNumbersY.add(elem.getComparativeValue());
                Log.d("DEBUG", String.format("%d,%f\t", elem.getParent().getDate().getTime(), elem.getComparativeValue()));
            }
            Log.d("DEBUG", "\n");

            // create series
            String seriesTitle = String.format("x%d", col);
            XYSeries series = new SimpleXYSeries(seriesNumbersX, seriesNumbersY, seriesTitle);

            // series formatting
            LineAndPointFormatter seriesFormat = new LineAndPointFormatter(unique_color(), Color.GREEN, null, null);
            seriesFormat.getLinePaint().setPathEffect(new DashPathEffect(new float[]{
                    PixelUtils.dpToPix(20),
                    PixelUtils.dpToPix(15)}, 0));

            // display dates in x-axis
            plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format()
            {
                private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");

                @Override
                public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos)
                {
                    long millis = ((Number) obj).longValue();
                    Date date = new Date(millis);
                    return dateFormat.format(date, toAppendTo, pos);
                }

                @Override
                public Object parseObject(String source, ParsePosition pos)
                {
                    return null;
                }
            });

            plot.setDomainStep(StepMode.SUBDIVIDE, arr2D.size());
            plot.setRangeStep(StepMode.SUBDIVIDE, arr2D.get(0).size());
            plot.setRangeBoundaries(0, 1.5, BoundaryMode.FIXED);
            plot.addSeries(series, seriesFormat);
        }

        addDataSetBtn.setVisibility(View.GONE);
        exitBtn.setVisibility(View.VISIBLE);
        plot.setVisibility(View.VISIBLE);
    }


    /**
     * Process the data from selectDataLauncher
     * Empty and fill loadedDset with new datasets
     */
    private class selectDataCallBack implements ActivityResultCallback<ArrayList<String>>
    {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onActivityResult(ArrayList<String> result)
        {
            loadedDataModules.clear();

            for (String uKey : result)
            {
                DataModule module = DataStore.primaryDataStore
                        .loadModule(uKey, ActivityTransitions.FROM_DATA_GRAPH_VIEW_ACTIVITY);
                loadedDataModules.add((DataCaptureModule) module);
            }

            displayDataModules();
        }
    }


    /**
     * The user clicked on button to add datasets to display.
     */
    private class AddDataSetsOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            switchToBrowseSavedDataActivity();
        }
    }

    /**
     * The user clicked on button exit activity
     */
    private class ExitOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            finish();
        }
    }
}

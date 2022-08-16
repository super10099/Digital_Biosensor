package com.example.myapplication.activities;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.datasystem.DataStore;
import com.example.myapplication.util.ActivityTransitions;
import com.example.myapplication.util.SelectDataSetContract;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;


/**
 * Activity for displaying trends from multiple data captures.
 */
public class DataGraphViewActivity extends AppCompatActivity
{

    /**
     * DataSets to display is loaded here
     */
    private ArrayList<DataStore.DataSet> loadedDSets = new ArrayList<>();

    /**
     *
     */
    private ActivityResultLauncher<DataGraphViewActivity> selectDataLauncher =
            registerForActivityResult(new SelectDataSetContract(), new selectDataCallBack());


    /**
     *
     * @param savedInstanceState
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

        ImageButton addDataSetsBtn = findViewById(R.id.GraphViewAddDataSetsBtn);
        addDataSetsBtn.setOnTouchListener(new OnAddDataSetsBtn());
    }


    /**
     * Launch the ActivityForResult for user to select a list of DataSets.
     */
    private void switchToBrowseSavedDataActivity()
    {
        selectDataLauncher.launch(this);
    }


    /**
     * Called after datasets are loaded.
     * Display the loaded datasets.
     */
    private void displayDataSets()
    {
        GraphView graph = findViewById(R.id.GraphView_Graph);
        Calendar calendar = Calendar.getInstance();
        ArrayList<Date> dates = new ArrayList<>();

        int count = 0;
        for (DataStore.DataSet ds : loadedDSets)
        {
            Iterator<DataStore.DataSet.DataSetElement> it = ds.getIterator();
            ArrayList<DataPoint> points = new ArrayList<>();
            while (it.hasNext())
            {
                DataStore.DataSet.DataSetElement elem = it.next();
                Date date = calendar.getTime();
                DataPoint p = new DataPoint(date, elem.getComparativeValue());
                points.add(p);

                dates.add(date);
                calendar.add(Calendar.DATE, 1);
            }
            DataPoint[] arr = new DataPoint[points.size()];
            points.toArray(arr);
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(arr);

            // styling
            series.setTitle(String.valueOf(count));
            series.setColor(Color.GREEN);
            series.setDrawDataPoints(true);
            series.setDataPointsRadius(20);
            series.setThickness(8);

            // custom paint to make a dotted line
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            paint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
            series.setCustomPaint(paint);

            graph.addSeries(series);

            count++;
        }

        // legend
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
        graph.getViewport().setMinX(dates.get(0).getTime());
        graph.getViewport().setMaxX(dates.get(2).getTime());
        graph.getViewport().setXAxisBoundsManual(true);
    }


    /**
     * Process the data from selectDataLauncher
     * Empty and fill loadedDset with new datasets
     */
    private class selectDataCallBack implements ActivityResultCallback<ArrayList<String>>
    {
        @Override
        public void onActivityResult(ArrayList<String> result)
        {
            loadedDSets.clear();

            for (String uKey : result)
            {
                DataStore.DataSet dSet = DataStore.PrimaryDataStore
                        .loadDSet(uKey, ActivityTransitions.FROM_DATA_GRAPH_VIEW_ACTIVITY);
                loadedDSets.add(dSet);
            }

            displayDataSets();
        }
    }


    /**
     * The user clicked on button to add datasets to display.
     */
    private class OnAddDataSetsBtn implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                switchToBrowseSavedDataActivity();
                return true;
            }
            return false;
        }
    }
}

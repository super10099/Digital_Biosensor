

package com.example.myapplication.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import com.example.myapplication.R;
import com.example.myapplication.datasystem.DataBundlingVisitor;
import com.example.myapplication.datasystem.DataStore;
import com.example.myapplication.util.ActivityTransitions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Thinh Nguyen
 * Displays the sample results.
 * Attributed from https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
 */
public class DataAnalysisActivity extends AppCompatActivity
{
    /**
     * Title for export menu
     */
    private static final String EXPORT_DATA_CHOOSER_TITLE = "Export Results";

    // TextLabels for each sample
    private static final int LISTVIEW_ACTUAL = R.id.resultsListView;
    private static final int LISTVIEW_LAYOUT_ID = R.layout.listview_results_item;
    private static final int LISTVIEW_TEXTVIEW = R.id.sampleLabel;
    private static final int LISTVIEW_SAMPLE_LABEL = R.id.sampleLabel;
    private static final int LISTVIEW_RGB_LABEL = R.id.RGBLabel;
    private static final int LISTVIEW_RPOINT_LABEL = R.id.RPointLabelTitle;
    private static final int LISTVIEW_RPOINTSTD_LABEL = R.id.RPointSTDLabel;
    private static final int LISTVIEW_COMPARISONVALUE_LABEL = R.id.comparisonValueLabel;

    private static final int EXIT_BTN = R.id.discardBtn;
    private static final int EXPORT_DATA_BTN = R.id.exportDataBtn;

    // references to save confirmation panel
    private static final int SAVE_RESULTS_LAYOUT = R.id.SaveResultsFrameConstraintLayout;
    private static final int SAVE_RESULTS_BTN = R.id.saveResultsBtn;
    private static final int SAVE_RESULTS_CANCEL_BTN = R.id.saveResultsCancelBtn;
    private static final int SAVE_RESULTS_CONFIRM_BTN = R.id.saveResultsConfirmBtn;
    private static final int SAVE_RESULTS_FILENANME_EDITTEXT = R.id.editTextFilename;

    private ConstraintLayout saveResultsCL;
    private EditText saveResultsFilenameEditText;

    // Information passed from DataCaptureActivity
    private SampleDataModelAdapter sampleDataModels;

    private DataStore.DataSet loadedDataSet;

    /**
     * Init of the results activity.
     *
     * @param savedInstanceState Previously saved state.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_results);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // variable inits
        // Views for actions
        Button newImageBtn = findViewById(EXIT_BTN);
        Button dataExportBtn = findViewById(EXPORT_DATA_BTN);

        saveResultsCL = findViewById(SAVE_RESULTS_LAYOUT);
        Button saveResultsBtn = findViewById(SAVE_RESULTS_BTN);
        Button saveResultsCancelBtn = findViewById(SAVE_RESULTS_CANCEL_BTN);
        Button saveResultsConfirmBtn = findViewById(SAVE_RESULTS_CONFIRM_BTN);
        saveResultsFilenameEditText = findViewById(SAVE_RESULTS_FILENANME_EDITTEXT);

        // attach event listeners
        newImageBtn.setOnTouchListener(new newImageOnTouch());
        dataExportBtn.setOnTouchListener(new onExportBtn());
        saveResultsBtn.setOnTouchListener(new onSaveResultsBtn());
        saveResultsCancelBtn.setOnTouchListener(new onSaveResultsCancelBtn());
        saveResultsConfirmBtn.setOnTouchListener(new onSaveResultsConfirmSaveBtn());

        // load the dataset
        Bundle extras = getIntent().getExtras();
        String uKey = (String) extras.get(DataBundlingVisitor.KEY_EXTRA_STRING);
        ActivityTransitions from = (ActivityTransitions) extras.getSerializable(ActivityTransitions.extraKey);
        loadedDataSet = DataStore.PrimaryDataStore.loadDSet(uKey, from);

        // only show save button if from data capture activity
        if (from == ActivityTransitions.FROM_DATA_CAPTURE_ACTIVITY)
        {
            saveResultsBtn.setVisibility(View.VISIBLE);
        } else
        {
            saveResultsBtn.setVisibility(View.GONE);
        }

        Log.d("DEBUG", "ResultsActivity onCreate is running.");

        sampleDataModels = new SampleDataModelAdapter(this);

        // create the ListView and ArrayAdapter
        convertData();
        displayData();
    }

    /**
     * Transition to MainActivity to restart the process.
     */
    private class newImageOnTouch implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                finish();
                return true;
            }
            return false;
        }
    }

    /**
     * Convert data from DataSet into the array data model for ListView
     */
    private void convertData()
    {
        Iterator<DataStore.DataSet.DataSetElement> dsIterator = loadedDataSet.getIterator();
        while (dsIterator.hasNext())
        {
            DataStore.DataSet.DataSetElement elem = dsIterator.next();
            Map<String, Double> sampleColor = new HashMap<>();
            sampleColor.put("avgR", elem.getAvgR());
            sampleColor.put("avgG", elem.getAvgG());
            sampleColor.put("avgB", elem.getAvgB());
            SampleDataModel newData =
                    new SampleDataModel(
                            sampleColor,
                            elem.getRPoint(),
                            elem.getRPointSTD(),
                            elem.getComparativeValue());
            sampleDataModels.add(newData);
        }
    }

    /**
     * Display data into listview.
     */
    private void displayData()
    {
        ListView resultsListView = findViewById(LISTVIEW_ACTUAL);
        resultsListView.setAdapter(sampleDataModels);
    }


    /**
     * Data model used in adapter for ListView.
     */
    private class SampleDataModel
    {
        private final Map<String, Double> sampleColor;
        private final double RPoint;
        private final double RPointSTD;
        private final double comparisonValue;

        public SampleDataModel(Map<String, Double> sampleColor, double RPoint, double RPointSTD, double comparisonValue)
        {
            this.sampleColor = sampleColor;
            this.RPoint = RPoint;
            this.RPointSTD = RPointSTD;
            this.comparisonValue = comparisonValue;
        }

        /**
         * @return average R value.
         */
        public Double getR()
        {
            return sampleColor.get("avgR");
        }

        /**
         * @return average G value
         */
        public Double getG()
        {
            return sampleColor.get("avgG");
        }

        /**
         * @return Average B value
         */
        public Double getB()
        {
            return sampleColor.get("avgB");
        }

        /**
         * @return Ratio divided by ratio of control.
         */
        public double getRPoint()
        {
            return RPoint;
        }

        /**
         * @return Standard deviation of R point values.
         */
        public double getRPointSTD()
        {
            return RPointSTD;
        }

        /**
         * @return Ratio of current sample
         */
        public double getComparisonValue()
        {
            return comparisonValue;
        }

    }

    private class SampleDataModelAdapter extends ArrayAdapter<SampleDataModel>
    {
        /**
         * Constructor
         *
         * @param context Activity context
         */
        public SampleDataModelAdapter(DataAnalysisActivity context)
        {
            super(context, LISTVIEW_LAYOUT_ID, LISTVIEW_TEXTVIEW);
        }

        /**
         * This method converts the SampleDataModel into a View that can be displayed.
         *
         * @param position    Position of data element (which sample?)
         * @param convertView The view to display
         * @param parent      Parent of the View (ViewGroup)
         * @return View that has been inflated with model information
         */
        @SuppressLint("ResourceType")
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            super.getView(position, convertView, parent);

            // inflate the view with the xml template
            if (convertView == null)
            {
                convertView = LayoutInflater.from(getContext())
                        .inflate(LISTVIEW_LAYOUT_ID, parent, false);
            }

            // this is to set the attributes of the convertView
            SampleDataModel sampleData = getItem(position);
            TextView sampleLabel = convertView.findViewById(LISTVIEW_SAMPLE_LABEL);
            TextView RGBLabel = convertView.findViewById(LISTVIEW_RGB_LABEL);
            TextView RPointLabel = convertView.findViewById(LISTVIEW_RPOINT_LABEL);
            TextView RPointSTDLabel = convertView.findViewById(LISTVIEW_RPOINTSTD_LABEL);
            TextView comparisonValueLabel = convertView.findViewById(LISTVIEW_COMPARISONVALUE_LABEL);

            // Setting the labels
            if (position == 0)
            {
                sampleLabel.setText("Control");
            } else
            {
                sampleLabel.setText("x" + position);
            }
            RGBLabel.setText(String.format("(%.0f, %.0f, %.0f)", sampleData.getR(), sampleData.getG(), sampleData.getB()));
            RPointLabel.setText(String.format("%.2f", sampleData.getRPoint()));
            RPointSTDLabel.setText(String.format("%.2E", sampleData.getRPointSTD()));
            comparisonValueLabel.setText(String.format("%.2f", sampleData.getComparisonValue()));

            return convertView;
        }
    }

    private void saveResultsReset()
    {
        saveResultsFilenameEditText.setText("");
        saveResultsCL.setVisibility(View.GONE);
    }

    /**
     * Open save results menu
     */
    private class onSaveResultsBtn implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                saveResultsCL.setVisibility(View.VISIBLE);
            }
            return true;
        }
    }

    /**
     * Cancel the act of saving results.
     */
    private class onSaveResultsCancelBtn implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                saveResultsReset();
            }
            return true;
        }
    }

    /**
     * Confirm save results
     */
    private class onSaveResultsConfirmSaveBtn implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                String inputFileName = saveResultsFilenameEditText.getText().toString();
                DataStore.PrimaryDataStore.putNewDataSet(loadedDataSet, ActivityTransitions.FROM_DATA_ANALYSIS_ACTIVITY, inputFileName);
                saveResultsReset();
            }
            return true;
        }
    }

    /**
     * Export data
     */
    private class onExportBtn implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                String rawCSV = loadedDataSet.getCSV();

                try
                {
                    // create a temporary CSV file
                    File dir = DataStore.PrimaryDataStore.getTempDir();
                    File csvFile = File.createTempFile("resultsExport", ".csv", dir);
                    FileOutputStream OStream = new FileOutputStream(csvFile);
                    OStream.write(rawCSV.getBytes());
                    Uri csvFileUri = FileProvider.getUriForFile(getApplicationContext(),
                            "com.example.myapplication.fileprovider",
                            csvFile);

                    // send as an intent
                    Intent exportDataIntent = new Intent(Intent.ACTION_SEND);
                    exportDataIntent.setType("text/csv");
                    exportDataIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    exportDataIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    exportDataIntent.putExtra(Intent.EXTRA_STREAM, csvFileUri);
                    startActivity(Intent.createChooser(exportDataIntent, EXPORT_DATA_CHOOSER_TITLE));
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            return true;
        }
    }
}

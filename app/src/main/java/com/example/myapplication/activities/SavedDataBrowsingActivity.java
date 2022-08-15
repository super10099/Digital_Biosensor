package com.example.myapplication.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.datasystem.DataBundlingVisitor;
import com.example.myapplication.datasystem.DataStore;
import com.example.myapplication.util.ActivityTransitions;
import com.example.myapplication.util.SelectDataSetContract;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Activity responsible for displaying the saved data in internal storage of application.
 * Upon browsing, the user can pick which saved DataSets to display the results of.
 * The activity will simply launch an ResultsActivity with the corresponding unique key and
 * parameters.
 */
public class SavedDataBrowsingActivity extends AppCompatActivity
{
    private static final int LISTVIEW_LAYOUT_ID = R.id.savedDataListView;
    private static final int LISTVIEW_ITEM_NAME_ID = R.id.savedDataNameBtn;
    private static final int LISTVIEW_ITEM_DATE_ID = R.id.savedDataDateBtn;
    private static final int LISTVIEW_ITEM_ID = R.layout.listview_saveddatabrowsing_item; // from graph viewing activity
    private static final int LISTVIEW_ITEM2_CHECKBOX_ID = R.id.savedDataCheckBox;
    private static final int EXIT_BTN_ID = R.id.savedDataExitBtn;

    private ActivityTransitions from;

    private SavedDataBrowsingGraphViewBranch branch;

    /**
     * Initialize activity. Set the device settings (title bar, orientation)
     *
     * @param savedInstanceState saved instances for this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        from = (ActivityTransitions) getIntent().getExtras().getSerializable(ActivityTransitions.extraKey);

        // set the main layout
        if (from == ActivityTransitions.FROM_DATA_GRAPH_VIEW_ACTIVITY)
        {
            setContentView(R.layout.activity_saveddatabrowsing_fromgraphview);
            branch = new SavedDataBrowsingGraphViewBranch(this);
            displaySavedData();
            branch.takeOver();
        } else
        {
            setContentView(R.layout.activity_saveddatabrowsing);
            findViewById(EXIT_BTN_ID).setOnTouchListener(new exitBtnListener());
            displaySavedData();
        }

    }

    /**
     * Show the entries representing saved data. User can click on an entry to display more details.
     */
    private void displaySavedData()
    {
        ArrayList<DataStore.DataSetListViewModel> dataSetModels = DataStore.PrimaryDataStore.getDataSetListViewModels();
        DataSetModelArrayAdapter adapter = new DataSetModelArrayAdapter(this);
        for (DataStore.DataSetListViewModel m : dataSetModels)
        {
            adapter.add(m);
        }
        ((ListView) findViewById(LISTVIEW_LAYOUT_ID)).setAdapter(adapter);
    }

    /**
     * Adapter for DataSets to be viewed in list.
     */
    private class DataSetModelArrayAdapter extends ArrayAdapter<DataStore.DataSetListViewModel>
    {
        /**
         * Constructor
         *
         * @param context Activity context
         */
        public DataSetModelArrayAdapter(SavedDataBrowsingActivity context)
        {
            super(context, LISTVIEW_ITEM_ID, LISTVIEW_ITEM_NAME_ID);
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
            DataStore.DataSetListViewModel model = getItem(position);

            // inflate the view with the xml template
            if (convertView == null)
            {

                convertView = LayoutInflater.from(getContext())
                        .inflate(LISTVIEW_ITEM_ID, parent, false);

                if (from == ActivityTransitions.FROM_DATA_GRAPH_VIEW_ACTIVITY)
                {
                    CheckBox cb = convertView.findViewById(LISTVIEW_ITEM2_CHECKBOX_ID);
                    branch.SetOnCheckedChangeListener(cb, model.getUKey());
                    cb.setVisibility(View.VISIBLE);
                }
            }

            // set listener
            OnDataView itemListener = new OnDataView();
            itemListener.setuKey(model.getUKey());
            convertView.setOnTouchListener(itemListener);

            // this is to set the attributes of the convertView
            TextView itemName = convertView.findViewById(LISTVIEW_ITEM_NAME_ID);
            TextView itemDate = convertView.findViewById(LISTVIEW_ITEM_DATE_ID);

            // Setting the labels
            itemName.setText(model.getName());
            itemDate.setText(model.getDate());

            return convertView;
        }
    }

    /**
     * Exit activity
     */
    private class exitBtnListener implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                finish();
            }
            return true;
        }
    }

    /**
     * View a data selection.
     */
    private class OnDataView implements View.OnTouchListener
    {
        private String uKey;

        public void setuKey(String uKey)
        {
            this.uKey = uKey;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                Intent resultsActivity = new Intent(SavedDataBrowsingActivity.this, DataAnalysisActivity.class);
                resultsActivity.putExtra(DataBundlingVisitor.KEY_EXTRA_STRING, uKey);
                resultsActivity.putExtra(ActivityTransitions.extraKey, ActivityTransitions.FROM_SAVED_DATA_BROWSING);
                startActivity(resultsActivity);
            }
            return true;
        }
    }


    /**
     * Branch here to handle ActivityTransitions from DataGraphView.
     */
    private class SavedDataBrowsingGraphViewBranch
    {
        private final SavedDataBrowsingActivity compat;

        private HashMap<String, Boolean> selectedDataSetUKeys = new HashMap<>();

        public SavedDataBrowsingGraphViewBranch(SavedDataBrowsingActivity compat)
        {
            this.compat = compat;
        }

        /**
         * Branching function
         */
        public void takeOver()
        {
            compat.findViewById(R.id.savedDataGraphViewBranchDoneBtn).setOnTouchListener(new OnDoneDataSelection());
        }

        /**
         * Set the listener for when user changes the checkbox.
         * @param cb
         * @param uKey
         */
        public void SetOnCheckedChangeListener(CheckBox cb, String uKey)
        {
            OnCheckedChangeListener l = new OnCheckedChangeListener();
            l.setUKey(uKey);
            cb.setOnCheckedChangeListener(l);
        }


        /**
         * Organizes the data and passes it as a result for the ActivityForResultContract
         */
        private class OnDoneDataSelection implements View.OnTouchListener
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    Intent intentData = new Intent();
                    ArrayList<String> uKeysToView = new ArrayList<>();

                    // loops through hashmap to check which uKeys were set to true
                    // the ones set to true were the ones selected by user
                    // so append it to an arraylist and set it as the result
                    for (String s : selectedDataSetUKeys.keySet())
                    {
                        if (Boolean.TRUE.equals(selectedDataSetUKeys.get(s)))
                        {
                            uKeysToView.add(s);
                        }
                    }
                    intentData.putExtra(SelectDataSetContract.EXTRA_KEY, uKeysToView);
                    setResult(Activity.RESULT_OK, intentData);
                    finish();
                    return true;
                }
                return false;
            }
        }


        /**
         * When a DataSet is selected, add it to the dictionary
         */
        private class OnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener
        {
            private String modelUKey;

            public void setUKey(String uKey)
            {
                this.modelUKey = uKey;
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                selectedDataSetUKeys.put(modelUKey, isChecked);
            }
        }
    }
}

package com.example.myapplication.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.datasystem.DataBundlingVisitor;
import com.example.myapplication.datasystem.DataStore;
import com.example.myapplication.util.ActivityTransitions;

import java.util.ArrayList;

/**
 * Activity responsible for displaying the saved data in internal storage of application.
 * Upon browsing, the user can pick which saved DataSets to display the results of.
 * The activity will simply launch an ResultsActivity with the corresponding unique key and
 * parameters.
 */
public class SavedDataBrowsingActivity extends AppCompatActivity
{

    private static final int LISTVIEW_LAYOUT_ID = R.id.savedDataListView;
    private static final int LISTVIEW_ITEM_ID = R.layout.listview_saveddatabrowsing_item;
    private static final int LISTVIEW_ITEM_NAME_ID = R.id.savedDataNameBtn;
    private static final int LISTVIEW_ITEM_DATE_ID = R.id.savedDataDateBtn;

    private static final int EXIT_BTN_ID = R.id.savedDataExitBtn;

    /**
     * Initialize activity. Set the device settings (title bar, orientation)
     * @param savedInstanceState saved instances for this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // disable title bar
        getSupportActionBar().hide();

        // set the main layout
        setContentView(R.layout.activity_saveddatabrowsing);

        // set orientation of the device
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // attach event listeners
        findViewById(EXIT_BTN_ID).setOnTouchListener(new exitBtnListener());

        displaySavedData();
    }

    private void displaySavedData()
    {
        ArrayList<DataStore.DataSetListViewModel> dataSetModels = DataStore.PrimaryDataStore.getDataSetListViewModels();
        DataSetModelArrayAdapter adapter = new DataSetModelArrayAdapter(this);
        for (DataStore.DataSetListViewModel m : dataSetModels)
        {
            adapter.add(m);
        }
        ((ListView)findViewById(LISTVIEW_LAYOUT_ID)).setAdapter(adapter);
    }

    private class DataSetModelArrayAdapter extends ArrayAdapter<DataStore.DataSetListViewModel>
    {
        /**
         * Constructor
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

            // inflate the view with the xml template
            if (convertView == null)
            {
                convertView = LayoutInflater.from(getContext())
                        .inflate(LISTVIEW_ITEM_ID, parent, false);
            }
            DataStore.DataSetListViewModel model = getItem(position);

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

}

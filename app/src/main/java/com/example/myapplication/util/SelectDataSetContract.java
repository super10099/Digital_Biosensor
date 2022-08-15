package com.example.myapplication.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.activities.DataGraphViewActivity;
import com.example.myapplication.activities.SavedDataBrowsingActivity;

import java.util.ArrayList;


/**
 * Contract for selecting data sets to display in graph view.
 */
public class SelectDataSetContract extends ActivityResultContract<DataGraphViewActivity, ArrayList<String>>
{
    public static final String EXTRA_KEY = "uKeysToView";

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, DataGraphViewActivity input)
    {
        Intent intent = new Intent(input, SavedDataBrowsingActivity.class);
        intent.putExtra(ActivityTransitions.extraKey, ActivityTransitions.FROM_DATA_GRAPH_VIEW_ACTIVITY);
        return intent;
    }

    @Override
    public ArrayList<String> parseResult(int resultCode, @Nullable Intent intent)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            ArrayList<String> results = (ArrayList<String>) intent.getExtras().getSerializable(EXTRA_KEY);
            return results;
        }
        return null;
    }
}

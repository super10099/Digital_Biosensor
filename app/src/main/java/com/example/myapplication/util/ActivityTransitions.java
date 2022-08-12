package com.example.myapplication.util;


/** Used for giving activites information on the previous activity
 * Primarily, which activity was prior to the current activity. */
public enum ActivityTransitions
{
    FROM_DATA_CAPTURE_ACTIVITY, FROM_DATA_ANALYSIS_ACTIVITY,
    FROM_SAVED_DATA_BROWSING, FROM_DATA_GRAPH_VIEW_ACTIVITY;

    public static final String extraKey = "FROM";
}

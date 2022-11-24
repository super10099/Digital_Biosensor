package com.bae.myapplication.util;


/**
 * Used for giving activites information on the previous activity
 * Primarily, which activity was prior to the current activity.
 */
public enum ActivityTransitions
{
    FROM_DATA_CAPTURE_ACTIVITY, FROM_DATA_ANALYSIS_ACTIVITY,
    FROM_SAVED_DATA_BROWSING, FROM_DATA_GRAPH_VIEW_ACTIVITY, FROM_MENU;

    public static final String extraKey = "FROM";
}

package com.example.myapplication.balloons;


/**
 * Manages balloons throughout the app when tutorial mode is on
 */
public class BalloonsManager {

    private static boolean tutorial_on;

    public static void set_tutorial_bool(boolean b)
    {
        tutorial_on = b;
    }

    public static boolean get_tutorial_bool() { return tutorial_on; }
}

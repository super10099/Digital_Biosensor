package com.bae.myapplication.controller;


/**
 * Manages existing controllers
 */
public class ControllerManager {

//    public static MenuController getMenuController() {
//        if (menuController == null)
//        {
//            menuController = new MenuController();
//        }
//        return menuController;
//    }

    public static DataCaptureController getDataCaptureController() {
        if (dataCaptureController == null)
        {
            dataCaptureController = new DataCaptureController();
        }
        return dataCaptureController;
    }

    private static MenuController menuController;

    private static DataCaptureController dataCaptureController;

}

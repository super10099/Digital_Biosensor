package com.bae.myapplication.model;

import android.net.Uri;

public class MenuModel {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_LOAD_IMAGE = 2;

    public void setImageBitmapUri(Uri imageBitmapUri) {
        this.imageBitmapUri = imageBitmapUri;
    }

    /**
     * Uri for the image file from loading or camera capture.
     * Required for next activity.
     */
    private Uri imageBitmapUri;



    public Uri getImageBitmapUri() {
        return imageBitmapUri;
    }
}

package com.example.myapplication.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.FileProvider;

import com.example.myapplication.activities.MenuActivity;
import com.example.myapplication.model.MenuModel;
import com.example.myapplication.util.PickPictureContract;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controller for MenuActivity
 */
public class MenuController {

    private MenuActivity applicationContext;
    private MenuModel model = new MenuModel();

    private ActivityResultLauncher<Void> loadPictureContractLauncher;


    public MenuController(MenuActivity context)
    {
        applicationContext = context;
        loadPictureContractLauncher = applicationContext.registerForActivityResult(
                new PickPictureContract(), new loadPictureCallback());
    }


    public void startDataCaptureActivity() {
        Bundle extras = new Bundle();
        extras.putParcelable("bitmapUri", model.getImageBitmapUri());
        ControllerManager.getDataCaptureController().startActivity(applicationContext, extras);
    }

    /**
     * Creates an image file with unique names each time.
     * The file is stored in external directory.
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Save a file: path for use with ACTION_VIEW intents
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",    /* suffix */
                storageDir      /* directory */
        );
    }

    public void takePicture() {
        // Create an image file to store the capture
        // Create a Uri to the image file
        // Use the Uri as an extra in ACTION_IMAGE_CAPTURE
        File pictureFile = null;
        try {
            pictureFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Ensure that the image file was successfully created
        // If successful, create the Intent and attach file Uri
        // Image capture result will be stored at the Uri destination
        if (pictureFile != null) {
            model.setImageBitmapUri(FileProvider.getUriForFile(applicationContext,
                    "com.example.myapplication.fileprovider",
                    pictureFile));
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, model.getImageBitmapUri());
            applicationContext.startActivityForResult(takePictureIntent, model.REQUEST_IMAGE_CAPTURE);

        }
    }

    public void loadPicture() {
        Intent pickPictureIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPictureIntent, model.REQUEST_LOAD_IMAGE);
    }

    private class loadPictureCallback implements ActivityResultCallback<Uri>
    {
        @Override
        public void onActivityResult(Uri result) {
            model.setImageBitmapUri(result);
            startDataCaptureActivity();
        }
    }

}

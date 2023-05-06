package com.bae.myapplication.controller;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.FileProvider;

import com.bae.myapplication.activities.MenuActivity;
import com.bae.myapplication.model.MenuModel;
import com.bae.myapplication.util.PickPictureContract;
import com.bae.myapplication.util.TakePictureContract;

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

    private ActivityResultLauncher<Uri> takePictureContractLauncher;
    private ActivityResultLauncher<Void> pickPictureContractLauncher;


    /**
     * Constructor for initializing the contract launchers for each activity.
     * @param context
     */
    public MenuController(MenuActivity context) {
        applicationContext = context;
        takePictureContractLauncher = applicationContext.registerForActivityResult(
                new TakePictureContract(), new takePictureCallback());
        pickPictureContractLauncher = applicationContext.registerForActivityResult(
                new PickPictureContract(), new loadPictureCallback());
    }


    /*
     * ********************* Helper functions ************************************************
     */


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
                    "com.bae.myapplication.fileprovider",
                    pictureFile));
            takePictureContractLauncher.launch(model.getImageBitmapUri());
        }
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

    /**
     * Start the next screen activity after the user took a picture.
     */
    public void startDataCaptureActivity() {
        Bundle extras = new Bundle();
        extras.putParcelable("bitmapUri", model.getImageBitmapUri());
        ControllerManager.getDataCaptureController().startActivity(applicationContext, extras);
    }

    /**
     * Load picture by launching the phone photo library app.
     */
    public void loadPicture() {
        pickPictureContractLauncher.launch(null);
    }


    /*
     * ********************* Callbacks ************************************************
     */

    /**
     * This function is called when the user clicks on the 'take picture' button
     */
    private class takePictureCallback implements ActivityResultCallback<Void> {
        @Override
        public void onActivityResult(Void result) {
            // saving it to storage
            Bitmap bitmap = null;
            Uri imageBitmapUri = model.getImageBitmapUri();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(),
                        imageBitmapUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            MediaStore.Images.Media.insertImage(applicationContext.getContentResolver(), bitmap,
                    imageBitmapUri.getLastPathSegment(), // last path is the name of the actual file
                    "Picture taken by user");
            startDataCaptureActivity();
        }
    }

    /**
     * This function is called when the user clicks on the 'load picture' button
     */
    private class loadPictureCallback implements ActivityResultCallback<Uri> {
        @Override
        public void onActivityResult(Uri result) {
            model.setImageBitmapUri(result);
            startDataCaptureActivity();
        }
    }
}

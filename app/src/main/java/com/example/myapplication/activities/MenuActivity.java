package com.example.myapplication.activities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.myapplication.datasystem.DataStore;
import com.example.myapplication.R;

/**
 * @author Thinh Nguyen
 * 5/31/2022
 * <p>
 * Android SDK documentation references:
 * https://developer.android.com/training/camera/photobasics
 * https://developer.android.com/reference/androidx/core/content/FileProvider#getUriForFile(android.content.Context,%20java.lang.String,%20java.io.File)
 */
public class MenuActivity extends AppCompatActivity
{
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;

    /**
     * Uri for the image file from loading or camera capture.
     * this is sent to TookPictureActivity
     */
    private Uri pictureFileUri;

    /**
     * Initialize Activity by defining variables and attaching UI listeners.
     * Also initializes DataStore if PrimaryDataStore does not exist.
     *
     * @param savedInstanceState saved instances for this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // init xml items and add listeners
        Button takePictureBtn = findViewById(R.id.menuTakePictureBtn);
        Button loadPictureBtn = findViewById(R.id.menuLoadPictureBtn);
        Button savedDataBtn = findViewById(R.id.menuSavedDataBtn);
        Button graphDataBtn = findViewById(R.id.menuDataGraphViewBtn);
        takePictureBtn.setOnClickListener(new TakePictureOnClickListener());
        loadPictureBtn.setOnClickListener(new LoadPictureOnClickListener());
        savedDataBtn.setOnClickListener(new SavedDataOnClickListener());
        graphDataBtn.setOnClickListener(new GraphDataOnClickListener());

        // initiate DataStore if does not exist
        if (DataStore.primaryDataStore == null)
        {
            DataStore.primaryDataStore = new DataStore(getApplicationContext());
        }

        // initialize tooltipmemory
        if (DataStore.primaryToolTipMemory == null)
        {
            DataStore.primaryDataStore.loadToolTipMemory();
        }
    }

    /**
     * Creates an image file with unique names each time.
     * The file is stored in external directory.
     */
    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Save a file: path for use with ACTION_VIEW intents
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",    /* suffix */
                storageDir      /* directory */
        );
    }

    /**
     * Goes to the next activity
     * Passes bitmap Uri to the activity data
     */
    private void setPictureView()
    {
        // Use bitmap to display in new activity UI
        Intent changeActivity = new Intent(MenuActivity.this, DataCaptureActivity.class);
        changeActivity.putExtra("bitmapUri", pictureFileUri);
        startActivity(changeActivity);
    }

    /**
     * When picture is taken, save the picture as a bitmap and update the pictureView with that bitmap.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Check to make sure it is the result that needs to be handled
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            setPictureView();
            Log.d("DEBUG", "Picture Took");
        } else if (requestCode == REQUEST_LOAD_IMAGE && resultCode == RESULT_OK && data != null)
        {
            pictureFileUri = data.getData();
            setPictureView();

            Log.d("DEBUG", "from Main, pictureFileUri=" + pictureFileUri);
            Log.d("DEBUG", "Selected an image to load.");
        }
    }

    /**
     * Listener for a request to capture picture
     */
    private class TakePictureOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            // Create an image file to store the capture
            // Create a Uri to the image file
            // Use the Uri as an extra in ACTION_IMAGE_CAPTURE
            File pictureFile = null;
            try {pictureFile = createImageFile();} catch (IOException e) {e.printStackTrace();}

            // Ensure that the image file was successfully created
            // If successful, create the Intent and attach file Uri
            // Image capture result will be stored at the Uri destination
            if (pictureFile != null)
            {
                pictureFileUri = FileProvider.getUriForFile(getApplicationContext(),
                        "com.example.myapplication.fileprovider",
                        pictureFile);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureFileUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                Log.d("DEBUG", "Taking Picture");
            }
        }
    }

    /**
     * Listener for request to load a picture from gallery
     */
    private class LoadPictureOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            Intent pickPictureIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPictureIntent, REQUEST_LOAD_IMAGE);
        }
    }


    /**
     * Listener for activity switch to SavedDataBrowsing
     */
    private class SavedDataOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            Intent changeActivity = new Intent(MenuActivity.this, SavedDataBrowsingActivity.class);
            changeActivity.putExtra("bitmapUri", pictureFileUri);
            startActivity(changeActivity);
        }
    }

    /**
     * Listener for activity switch to DataGraphViewActivity
     */
    private class GraphDataOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            Intent changeActivity = new Intent(MenuActivity.this, DataGraphViewActivity.class);
            startActivity(changeActivity);
        }
    }
}

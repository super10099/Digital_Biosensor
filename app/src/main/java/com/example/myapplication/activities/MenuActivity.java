package com.example.myapplication.activities;

import java.io.IOException;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.controller.MenuController;
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
public class MenuActivity extends AppCompatActivity {

    private static boolean tutorial_mode = false;

    private boolean isTutorialModeOn() {
        return tutorial_mode;
    }

    private void setTutorialMode(boolean b) {
        tutorial_mode = b;
    }

    // since the thread starts in MenuActivity, the view actually creates the controller.
    private MenuController controller;

    /**
     * Initialize Activity by defining variables and attaching UI listeners.
     * Also initializes DataStore if PrimaryDataStore does not exist.
     *
     * @param savedInstanceState saved instances for this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // init xml items and add listeners
        Button takePictureBtn = findViewById(R.id.menuTakePictureBtn);
        Button loadPictureBtn = findViewById(R.id.menuLoadPictureBtn);
        Button savedDataBtn = findViewById(R.id.menuSavedDataBtn);
        Button graphDataBtn = findViewById(R.id.menuDataGraphViewBtn);
        Switch tutorialSwitch = findViewById(R.id.TutorialSwitch);
        takePictureBtn.setOnClickListener(new TakePictureOnClickListener());
        loadPictureBtn.setOnClickListener(new LoadPictureOnClickListener());
        savedDataBtn.setOnClickListener(new SavedDataOnClickListener());
        graphDataBtn.setOnClickListener(new GraphDataOnClickListener());
        tutorialSwitch.setOnCheckedChangeListener(new TutorialOnCheckListener());

        // initiate DataStore if does not exist
        if (DataStore.primaryDataStore == null) {
            DataStore.primaryDataStore = new DataStore(getApplicationContext());
        }

        // initialize tooltipmemory
        if (DataStore.primaryToolTipMemory == null) {
            DataStore.primaryDataStore.loadToolTipMemory();
        }

        controller = new MenuController(this);
    }

    private class TakePictureOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            controller.takePicture();
        }
    }

    /**
     * Listener for request to load a picture from gallery
     */
    private class LoadPictureOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            controller.loadPicture();
        }
    }


    /**
     * Listener for activity switch to SavedDataBrowsing
     */
    private class SavedDataOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent changeActivity = new Intent(MenuActivity.this, SavedDataBrowsingActivity.class);
            startActivity(changeActivity);
        }
    }

    /**
     * Listener for activity switch to DataGraphViewActivity
     */
    private class GraphDataOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent changeActivity = new Intent(MenuActivity.this, DataGraphViewActivity.class);
            startActivity(changeActivity);
        }
    }

    /**
     * Listener for tutorial mode switch
     */
    private class TutorialOnCheckListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            setTutorialMode(b);
        }
    }
}

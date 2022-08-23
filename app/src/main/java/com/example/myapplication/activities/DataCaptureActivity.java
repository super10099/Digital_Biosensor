package com.example.myapplication.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.myapplication.datasystem.DataCaptureSettings;
import com.example.myapplication.sampler.CircularSamplerGenerator;
import com.example.myapplication.sampler.ComparativeValueVisitor;
import com.example.myapplication.datasystem.DataBundlingVisitor;
import com.example.myapplication.R;
import com.example.myapplication.util.ActivityTransitions;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;


/**
 * Activity responsible for gathering user input after a picture was taken.
 */
public class DataCaptureActivity extends AppCompatActivity
{
    /**
     * Settings of data capture, has options for user to adjust
     */
    private static DataCaptureSettings settings;

    private ImageView pictureView;
    private Bitmap pictureBitmap;

    private CircularSamplerGenerator csGen;
    private ImageButton csGenView;

    private ConstraintLayout dataCaptureOptionsCL;
    private TextInputEditText scaleInputField;
    private TextInputEditText samplerRadiusInputField;
    private TextInputEditText samplerNumPointsInputField;

    private ScrollView scrollView;
    private HorizontalScrollView horizontalScrollView;


    public ImageView getPictureView()
    {
        return pictureView;
    }

    public ScrollView getScrollView()
    {
        return scrollView;
    }

    public HorizontalScrollView getHorizontalScrollViewScrollView()
    {
        return horizontalScrollView;
    }


    /**
     * Function that runs when this activity is started
     *
     * @param savedInstanceState Saved instances
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datacapture);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // initializations
        if (settings == null)
        {
            settings = new DataCaptureSettings();
        }

        scrollView = findViewById(R.id.DataCaptureImageScrollView);
        horizontalScrollView = findViewById(R.id.DataCaptureHorizontalScroll);
        pictureView = findViewById(R.id.DataCapturePictureView);
        ImageButton doneBtnView = findViewById(R.id.DataCaptureDoneBtn);
        csGenView = findViewById(R.id.DataCaptureAddSamplerBtn);
        dataCaptureOptionsCL = findViewById(R.id.DataCaptureOptionsConstraintLayout);
        ImageButton dataCaptureOptionsBtn = findViewById(R.id.DataCaptureOptionsBtn);
        Button dataCaptureCancelBtn = findViewById(R.id.DataCaptureOptionsCancelBtn);
        Button dataCaptureConfirmBtn = findViewById(R.id.DataCaptureOptionsConfirmBtn);
        scaleInputField = findViewById(R.id.DataCaptureEditScale);
        samplerRadiusInputField = findViewById(R.id.DataCaptureEditSamplerRadius);
        samplerNumPointsInputField = findViewById(R.id.DataCaptureEditNumTrials);

        // add listeners for doneBtn
        doneBtnView.setOnClickListener(new DeployedSamplersOnClickListener());
        pictureView.setOnTouchListener(new ScreenDebuggingOnTouchListener());
        dataCaptureOptionsBtn.setOnClickListener(new OptionsOnClickListener());
        dataCaptureCancelBtn.setOnClickListener(new OptionsCancelOnClickListener());
        dataCaptureConfirmBtn.setOnClickListener(new OptionsConfirmOnClickListener());

        // set the picture after the view has been initialized
        pictureView.post(() ->
        {
            Uri currentPhotoUri = (Uri) getIntent().getExtras().get("bitmapUri");
            try
            {
                // load original bitmap
                InputStream is = getContentResolver().openInputStream(currentPhotoUri);
                pictureBitmap = BitmapFactory.decodeStream(is);
                pictureView.setImageBitmap(pictureBitmap);

                // calculate scale to fit phone length
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                double scale = (double) displayMetrics.widthPixels / pictureBitmap.getWidth();
                settings.setImageScale(scale);  // update settings

                layoutImageView();

            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        });

        // create the generator after view is initialized.
        csGenView.post(() -> csGen = new CircularSamplerGenerator(this, csGenView, settings));
    }


    /**
     * Calculates argb value in bitmap given a global position.
     * Beforehand, the global position changed to a relative position to pictureView.
     *
     * @param p Global position
     * @return Integer representation of argb.
     */
    public int getPixelFromPosition(Point p)
    {
        // Convert absolute coordinates to relative coordinates of pictureView
        // Descale the position to the unscaled bitmap.
        double scale = settings.getImageScale();
        int unscaledX = (int) ((p.x - pictureView.getX()) / scale);
        int unscaledY = (int) ((p.y - pictureView.getY()) / scale);
        Log.d("DEBUG", String.format("scaled position = %d,%d", p.x, p.y));
        Log.d("DEBUG", String.format("descaled position = %d,%d", unscaledX, unscaledY));

        Bitmap pictureBitmap = ((BitmapDrawable) pictureView.getDrawable()).getBitmap();
        return pictureBitmap.getPixel(unscaledX, unscaledY);
    }


    /**
     * Animate a given visual dot to the target position.
     * NOTE: Target position is in global space.
     *
     * @param visualDotView  ImageView to animate
     * @param targetPosition Target position in global space.
     */
    public void animateVisualDot(ImageView visualDotView, Point targetPosition)
    {
        int relativeX = Math.round(targetPosition.x);
        int relativeY = Math.round(targetPosition.y);
        visualDotView.setX(relativeX - Math.round(visualDotView.getWidth() / 2f));
        visualDotView.setY(relativeY - Math.round(visualDotView.getHeight() / 2f));
    }

    /**
     * When settings has been updated
     */
    private void updatedSettings()
    {
        layoutImageView();
    }

    /**
     * Set the image view (as in layout).
     */
    private void layoutImageView()
    {
        // set the dimensions of view according to scale settings
        double scale = settings.getImageScale();
        ConstraintLayout samplersCL = findViewById(R.id.cLCircularSamplers);
        int scaledWit = (int) (pictureBitmap.getWidth() * scale);
        int scaledHit = (int) (pictureBitmap.getHeight() * scale);
        pictureView.getLayoutParams().width = scaledWit;
        pictureView.getLayoutParams().height = scaledHit;
        samplersCL.getLayoutParams().width = scaledWit;
        samplersCL.getLayoutParams().height = scaledHit;
        pictureView.requestLayout();
        samplersCL.requestLayout();
    }


    /**
     * Listener for when the user is done picking deploying the samplers.
     * Mostly, this function generates the data and passes it the results activity.
     */
    private class DeployedSamplersOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            v.playSoundEffect(SoundEffectConstants.CLICK);

            csGen.doTrials();

            // Create visitor to calculate comparative values of each sampler
            ComparativeValueVisitor calculateCVVistor = new ComparativeValueVisitor();
            csGen.acceptVisitor(calculateCVVistor);
            calculateCVVistor.calculateComparativeValues();

            // Create visitor to collect all the data and bundle it for data storage
            DataBundlingVisitor dbVisitor = new DataBundlingVisitor();
            csGen.acceptVisitor(dbVisitor);
            String uKey = dbVisitor.PackDataSet();
            goToResultsActivity(uKey);
        }
    }


    /**
     * Debugging mouse clicks
     */
    private class ScreenDebuggingOnTouchListener implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                double x = event.getX();
                double y = event.getY();
                double rx = event.getRawX();
                double ry = event.getRawY();
                Log.d("DEBUG", String.format("Clicked %f,%f,%f,%f", x, y, rx, ry));
            }
            return true;
        }
    }


    /**
     *
     */
    private class OptionsOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            v.playSoundEffect(SoundEffectConstants.CLICK);
            dataCaptureOptionsCL.setVisibility(View.VISIBLE);

            // update fields to current settings
            double scale = settings.getImageScale();
            int radius = settings.getSamplerRadius();
            int numPoints = settings.getNumSamplingPoints();
            scaleInputField.setText(Double.toString(scale));
            samplerRadiusInputField.setText(Integer.toString(radius));
            samplerNumPointsInputField.setText(Integer.toString(numPoints));
        }
    }

    /**
     *
     */
    private class OptionsCancelOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            v.playSoundEffect(SoundEffectConstants.CLICK);
            dataCaptureOptionsCL.setVisibility(View.GONE);
        }
    }


    /**
     *
     */
    private class OptionsConfirmOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            v.playSoundEffect(SoundEffectConstants.CLICK);
            dataCaptureOptionsCL.setVisibility(View.GONE);

            // update settings
            double scale = Double.parseDouble(scaleInputField.getText().toString());
            int radius = Integer.parseInt(samplerRadiusInputField.getText().toString());
            int numPoints = Integer.parseInt(samplerNumPointsInputField.getText().toString());
            settings.setImageScale(scale);
            settings.setSamplerRadius(radius);
            settings.setNumSamplingPoints(numPoints);
            updatedSettings();
        }
    }


    /**
     * Move data to new activity, that activity can display the data.
     *
     * @param uKey Key to access the DataSet from TookPictureActivity
     */
    private void goToResultsActivity(String uKey)
    {
        // pass the bundle to next activity
        Intent resultsActivity = new Intent(DataCaptureActivity.this, DataAnalysisActivity.class);
        resultsActivity.putExtra(DataBundlingVisitor.KEY_EXTRA_STRING, uKey);
        resultsActivity.putExtra(ActivityTransitions.extraKey, ActivityTransitions.FROM_DATA_CAPTURE_ACTIVITY);
        startActivity(resultsActivity);
        finish();
    }
}

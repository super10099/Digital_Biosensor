package com.example.myapplication.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Contract for selecting loading picture
 */
public class TakePictureContract extends ActivityResultContract<Uri, Void>
{
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Uri imageUri) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        return takePictureIntent;
    }

    @Override
    public Void parseResult(int resultCode, @Nullable Intent intent)
    {
        return null;
    }
}

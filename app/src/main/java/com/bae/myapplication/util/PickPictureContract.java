package com.bae.myapplication.util;

import android.app.Activity;
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
public class PickPictureContract extends ActivityResultContract<Void, Uri>
{
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Void input) {
        Intent pickPictureIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        return pickPictureIntent;
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            return (Uri) intent.getData();
        }
        return null;
    }
}

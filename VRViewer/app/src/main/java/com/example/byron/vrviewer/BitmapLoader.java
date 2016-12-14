package com.example.byron.vrviewer;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Created by Byron on 12/13/2016.
 */

public class BitmapLoader extends AsyncTaskLoader<Bitmap> {

    Context context;
    Uri fileUri;
    static final private String TAG = "BaseImageVRActivity";

    public BitmapLoader(Context context, Uri fileUri) {
        super(context);
        this.context = context;
        this.fileUri = fileUri;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Bitmap loadInBackground() {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), fileUri);
        } catch (Exception e) {
            Log.e(TAG, "Could not load file: " + e);
            return null;
        }

        return bitmap;
    }
}

package com.example.byron.vrviewer.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.byron.vrviewer.R;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.google.vr.sdk.widgets.pano.VrPanoramaView.Options;

public class BaseImageVRActivity extends AppCompatActivity {

    public boolean loadImageSuccessful;
    private VrPanoramaView panoWidgetView;
    private BaseImageVRActivity.ImageLoaderTask backgroundImageLoaderTask;
    static final private String TAG = "BaseImageVRActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_image_vr);

        getSupportActionBar().hide();


        panoWidgetView = (VrPanoramaView) findViewById(R.id.pano_view);
        panoWidgetView.setEventListener(new BaseImageVRActivity.ActivityEventListener());
    }

    protected void loadPanoramicImageFromDisk(Uri fileUri) {
        VrPanoramaView.Options panoOptions = new VrPanoramaView.Options();
        panoOptions.inputType = VrPanoramaView.Options.TYPE_MONO;

        backgroundImageLoaderTask = new BaseImageVRActivity.ImageLoaderTask();
        backgroundImageLoaderTask.execute((Pair.create(fileUri, panoOptions)));
    }

    protected void loadPanoramicImageFromUrl(String imageUrl) {
        final VrPanoramaView.Options panoOptions = new VrPanoramaView.Options();
        panoOptions.inputType = VrPanoramaView.Options.TYPE_MONO;

        Glide.with(getApplicationContext())
                .load(imageUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        panoWidgetView.loadImageFromBitmap(resource, panoOptions);
                    }
                });
    }

    @Override
    protected void onPause() {
        panoWidgetView.pauseRendering();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        panoWidgetView.resumeRendering();
    }

    @Override
    protected void onDestroy() {
        // Destroy the widget and free memory.
        panoWidgetView.shutdown();

        if (backgroundImageLoaderTask != null) {
            backgroundImageLoaderTask.cancel(true);
        }
        super.onDestroy();
    }

    //Read image from disk
    class ImageLoaderTask extends AsyncTask<Pair<Uri, Options>, Void, Boolean> {

        /**
         * Reads the bitmap from disk in the background and waits until it's loaded by pano widget.
         */
        @Override
        protected Boolean doInBackground(Pair<Uri, Options>... fileInformation) {
            Options panoOptions = null;  // It's safe to use null VrPanoramaView.Options.
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileInformation[0].first);
                panoOptions = fileInformation[0].second;

            } catch (Exception e) {
                Log.e(TAG, "Could not load file: " + e);
                return false;
            }

            panoWidgetView.loadImageFromBitmap(bitmap, panoOptions);
            return true;
        }
    }

    /**
     * Listen to the important events from widget.
     */
    private class ActivityEventListener extends VrPanoramaEventListener {

        @Override
        public void onLoadSuccess() {
            loadImageSuccessful = true;
        }

        @Override
        public void onLoadError(String errorMessage) {
            loadImageSuccessful = false;
            Toast.makeText(
                    BaseImageVRActivity.this, "Error loading pano: " + errorMessage, Toast.LENGTH_LONG)
                    .show();
            Log.e(TAG, "Error loading pano: " + errorMessage);
        }
    }

}

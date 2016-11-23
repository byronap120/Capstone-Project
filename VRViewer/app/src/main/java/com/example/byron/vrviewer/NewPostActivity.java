package com.example.byron.vrviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.google.vr.sdk.widgets.pano.VrPanoramaView.Options;

import java.io.IOException;
import java.io.InputStream;


public class NewPostActivity extends AppCompatActivity {

    static final int GALLERY = 1;
    private VrPanoramaView panoWidgetView;
    public boolean loadImageSuccessful;
    private String TAG = "NewPostActivity";
    private ImageLoaderTask backgroundImageLoaderTask;
    private String packagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        panoWidgetView = (VrPanoramaView) findViewById(R.id.pano_view);
        panoWidgetView.setEventListener(new ActivityEventListener());

        packagePath = Environment.getExternalStorageDirectory().getPath();

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), GALLERY);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            loadPanoramicImage(selectedImageUri);
        }
    }

    private void loadPanoramicImage(Uri fileUri) {
        Options panoOptions = new Options();
        panoOptions.inputType = VrPanoramaView.Options.TYPE_MONO;

        backgroundImageLoaderTask = new ImageLoaderTask();
        backgroundImageLoaderTask.execute(Pair.create(fileUri, panoOptions));
    }


    class ImageLoaderTask extends AsyncTask<Pair<Uri, Options>, Void, Boolean> {

        /**
         * Reads the bitmap from disk in the background and waits until it's loaded by pano widget.
         */
        @Override
        protected Boolean doInBackground(Pair<Uri, Options>... fileInformation) {
            Options panoOptions = null;  // It's safe to use null VrPanoramaView.Options.
            InputStream istr = null;
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileInformation[0].first);
                panoOptions = fileInformation[0].second;

            } catch (Exception e) {
                Log.e(TAG, "Could not load file: " + e);
                return false;
            }


            panoWidgetView.loadImageFromBitmap(bitmap, panoOptions);
            try {
                istr.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close input stream: " + e);
            }

            return true;
        }
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

        // The background task has a 5 second timeout so it can potentially stay alive for 5 seconds
        // after the activity is destroyed unless it is explicitly cancelled.
        if (backgroundImageLoaderTask != null) {
            backgroundImageLoaderTask.cancel(true);
        }
        super.onDestroy();
    }

    /**
     * Listen to the important events from widget.
     */
    private class ActivityEventListener extends VrPanoramaEventListener {
        /**
         * Called by pano widget on the UI thread when it's done loading the image.
         */
        @Override
        public void onLoadSuccess() {
            loadImageSuccessful = true;
        }

        /**
         * Called by pano widget on the UI thread on any asynchronous error.
         */
        @Override
        public void onLoadError(String errorMessage) {
            loadImageSuccessful = false;
            Toast.makeText(
                    NewPostActivity.this, "Error loading pano: " + errorMessage, Toast.LENGTH_LONG)
                    .show();
            Log.e(TAG, "Error loading pano: " + errorMessage);
        }
    }
}

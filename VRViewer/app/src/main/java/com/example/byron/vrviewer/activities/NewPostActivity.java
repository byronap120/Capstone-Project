package com.example.byron.vrviewer.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.byron.vrviewer.R;
import com.example.byron.vrviewer.models.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.google.vr.sdk.widgets.pano.VrPanoramaView.Options;


public class NewPostActivity extends AppCompatActivity {

    static final int GALLERY = 1;
    static final private String TAG = "NewPostActivity";

    public boolean loadImageSuccessful;

    private ImageLoaderTask backgroundImageLoaderTask;
    private Uri selectedImageUri;

    private VrPanoramaView panoWidgetView;
    private Button buttonPost;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private ProgressDialog progressDialog;

    private FirebaseApp app;
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    private StorageReference storageRef;
    private DatabaseReference databaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        buttonPost = (Button) findViewById(R.id.buttonPost);
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataIsCorrect()) {
                    newPost();
                }
            }
        });
        panoWidgetView = (VrPanoramaView) findViewById(R.id.pano_view);
        panoWidgetView.setEventListener(new ActivityEventListener());

        // Initialize Firebase
        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);
        storage = FirebaseStorage.getInstance(app);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), GALLERY);
    }

    public boolean dataIsCorrect() {
        boolean allDataIsCorrect = true;

        if (TextUtils.isEmpty(editTextTitle.getText())) {
            String errorMessage = getResources().getString(R.string.newPost_required_data);
            editTextTitle.setError(errorMessage);
            allDataIsCorrect = false;
        }

        if (!loadImageSuccessful) {
            String errorMessage = getResources().getString(R.string.newPost_image_error);
            Toast.makeText(NewPostActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            allDataIsCorrect = false;
        }

        return allDataIsCorrect;
    }

    private void newPost() {
        progressDialog = new ProgressDialog(NewPostActivity.this);
        String loadingMessage = getResources().getString(R.string.dialog_loading);
        progressDialog.setMessage(loadingMessage);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // Get a reference to the location where we'll store our photos
        storageRef = storage.getReference("chat_photos");
        // Get a reference to store file at chat_photos/<FILENAME>
        final StorageReference photoRef = storageRef.child(selectedImageUri.getLastPathSegment());

        // Upload Image to Firebase Storage
        photoRef.putFile(selectedImageUri)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // When the image has successfully uploaded, we get its download URL
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        writeNewPost(
                                editTextTitle.getText().toString(),
                                editTextDescription.getText().toString(),
                                downloadUrl.toString());
                    }
                });
    }

    private void writeNewPost(String title, String description, String imageLink) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.getDisplayName();

        Post post = new Post(title, description, user.getDisplayName(), imageLink);

        databaseRef = database.getReference("posts");
        databaseRef.push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                //startActivity(new Intent(NewPostActivity.this, ExplorePostsActivity.class));
                onBackPressed();
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
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

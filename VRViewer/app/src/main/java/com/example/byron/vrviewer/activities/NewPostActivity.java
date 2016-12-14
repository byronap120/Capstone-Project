package com.example.byron.vrviewer.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

public class NewPostActivity extends BaseImageVRActivity {

    static final int GALLERY = 1;

    private Uri selectedImageUri;

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

        //Loading NewPostActivity layout into the BaseImageVRActivity layout
        RelativeLayout baseRelativeLayout = (RelativeLayout) findViewById(R.id.base_relative_layout);
        View postActivityLayout = getLayoutInflater().inflate(R.layout.activity_new_post, null);
        baseRelativeLayout.addView(postActivityLayout);

        editTextTitle = (EditText) postActivityLayout.findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) postActivityLayout.findViewById(R.id.editTextDescription);
        buttonPost = (Button) postActivityLayout.findViewById(R.id.buttonPost);
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataIsCorrect()) {
                    newPost();
                }
            }
        });

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
        createAndShowDialog();
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

    private void createAndShowDialog() {
        progressDialog = new ProgressDialog(NewPostActivity.this);
        String loadingMessage = getResources().getString(R.string.dialog_Posting);
        progressDialog.setMessage(loadingMessage);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
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
                onBackPressed();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            loadPanoramicImageFromDisk(selectedImageUri);
        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }

}

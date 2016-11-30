package com.example.byron.vrviewer.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.byron.vrviewer.R;
import com.example.byron.vrviewer.models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

public class DetailActivity extends BaseImageVRActivity implements ValueEventListener {

    private DatabaseReference mPostReference;
    private String postRef;

    private TextView textViewUser;
    private TextView textViewTitle;
    private TextView textViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Loading DetailActivity layout into the BaseImageVRActivity layout
        RelativeLayout baseRelativeLayout = (RelativeLayout) findViewById(R.id.base_relative_layout);
        View detailActivityLayout = getLayoutInflater().inflate(R.layout.activity_detail, null);
        baseRelativeLayout.addView(detailActivityLayout);

        textViewUser = (TextView) baseRelativeLayout.findViewById(R.id.textViewUser);
        textViewTitle = (TextView) baseRelativeLayout.findViewById(R.id.textViewTitle);
        textViewDescription = (TextView) baseRelativeLayout.findViewById(R.id.textViewDescription);

        postRef = getIntent().getStringExtra("postRef");
        mPostReference = FirebaseDatabase.getInstance().getReference().child("posts").child(postRef);
        mPostReference.addValueEventListener(this);

        boolean fullView = getIntent().getBooleanExtra("fullView", false);
        if(fullView) {
            panoWidgetView.setDisplayMode(VrPanoramaView.DisplayMode.FULLSCREEN_MONO);
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Post post = dataSnapshot.getValue(Post.class);
        textViewUser.setText(post.getUsername());
        textViewTitle.setText(post.getTitle());
        textViewDescription.setText(post.getDescription());
        loadPanoramicImageFromUrl(post.getImageLink());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}

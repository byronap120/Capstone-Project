package com.example.byron.vrviewer.activities;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.byron.vrviewer.DatabaseContract;
import com.example.byron.vrviewer.R;
import com.example.byron.vrviewer.adapters.PostsAdapter;
import com.example.byron.vrviewer.models.Post;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ExplorePostsActivity extends AppCompatActivity implements ChildEventListener {

    private FirebaseApp app;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private FirebaseAnalytics mFirebaseAnalytics;


    private PostsAdapter postsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);

        databaseRef = database.getReference("posts");
        databaseRef.keepSynced(true);

        databaseRef.addChildEventListener(this);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);


        RecyclerView postsRecyclerView = (RecyclerView) findViewById(R.id.postsRecyclerView);
        postsAdapter = new PostsAdapter(getApplicationContext());
        postsRecyclerView.setAdapter(postsAdapter);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        postsAdapter.setOnItemClickListener(new PostsAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(View itemView, String postRef, boolean fullView) {
                Intent intent = new Intent(ExplorePostsActivity.this, DetailActivity.class);
                intent.putExtra("postRef", postRef);
                intent.putExtra("fullView", fullView);
                trackAnalyticsEvent(postRef, fullView);

                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(ExplorePostsActivity.this).toBundle();


                startActivity(intent, bundle);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExplorePostsActivity.this, NewPostActivity.class));
            }
        });
    }

    private void trackAnalyticsEvent(String postRef, boolean fullView) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, postRef);
        bundle.putBoolean(FirebaseAnalytics.Param.VALUE, fullView);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        dataSnapshot.getRef();
        Post post = dataSnapshot.getValue(Post.class);
        post.setPostRef(dataSnapshot.getRef().getKey());
        postsAdapter.addNewPost(post);
        addToDataBase(post);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private void addToDataBase(Post post) {

        ContentValues post_values = new ContentValues();
        post_values.put(DatabaseContract.posts_table.TITLE, post.getTitle());
        post_values.put(DatabaseContract.posts_table.USERNAME, post.getUsername());
        post_values.put(DatabaseContract.posts_table.IMAGE_LINK, post.getImageLink());
        post_values.put(DatabaseContract.posts_table.POST_REF, post.getPostRef());

        String[] postRef = {post.getPostRef()};

        getApplicationContext().getContentResolver().update(
                DatabaseContract.BASE_CONTENT_URI,
                post_values,
                DatabaseContract.posts_table.POST_REF + " = ?",
                postRef);
    }
}

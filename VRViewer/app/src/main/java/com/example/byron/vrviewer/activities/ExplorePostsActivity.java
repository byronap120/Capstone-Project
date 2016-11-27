package com.example.byron.vrviewer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.byron.vrviewer.R;
import com.example.byron.vrviewer.adapters.PostsAdapter;
import com.example.byron.vrviewer.models.Post;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ExplorePostsActivity extends AppCompatActivity implements ChildEventListener {

    private FirebaseApp app;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

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

        RecyclerView postsRecyclerView = (RecyclerView) findViewById(R.id.postsRecyclerView);
        postsAdapter = new PostsAdapter(this);
        postsRecyclerView.setAdapter(postsAdapter);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        postsAdapter.setOnItemClickListener(new PostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, String postRef) {
                Intent intent = new Intent(ExplorePostsActivity.this, DetailActivity.class);
                intent.putExtra("postRef", postRef);
                startActivity(intent);
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


    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        dataSnapshot.getRef();
        Post post = dataSnapshot.getValue(Post.class);
        post.setPostRef(dataSnapshot.getRef().getKey());
        postsAdapter.addNewPost(post);
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
}

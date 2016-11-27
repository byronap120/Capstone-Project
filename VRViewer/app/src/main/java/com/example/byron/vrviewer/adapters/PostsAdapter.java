package com.example.byron.vrviewer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.byron.vrviewer.R;
import com.example.byron.vrviewer.models.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Byron on 11/27/2016.
 */

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{

    private List<Post> postList;
    private Context context;

    public PostsAdapter(Context context) {
        this.postList = new ArrayList<>();
        this.context = context;
    }

    public void addNewPost(Post post) {
        postList.add(post);
        notifyItemInserted(postList.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_posts, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Post userPost = postList.get(position);

        // Set item views based on your views and data model
        TextView userName = viewHolder.userName;
        userName.setText(userPost.getUsername());

        ImageView postImage = viewHolder.postImage;
        String imageLink = userPost.getImageLink();
        //Picasso.with(context).load(imageLink).into(postImage);

        Glide.with(context)
                .load(imageLink)
                .into(postImage);

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView postImage;
        TextView userName;
        Button buttonVR;
        Button buttonDetails;

        public ViewHolder(View itemView) {
            super(itemView);
            postImage = (ImageView) itemView.findViewById(R.id.imageViewPost);
            userName = (TextView) itemView.findViewById(R.id.textViewUser);
            buttonVR = (Button) itemView.findViewById(R.id.buttonVR);
            buttonDetails = (Button) itemView.findViewById(R.id.buttonDetails);
        }
    }
}

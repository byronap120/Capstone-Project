package com.example.byron.vrviewer;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.example.byron.vrviewer.models.Post;

import java.util.List;

/**
 * Created by bajin on 12/14/16.
 */

/**
    This class move Data received from server and saved to local DB, this way I'm able to use a Content Provider
    and pass information to Widget
*/
public class SaveDataAsyncTask extends AsyncTask<List<Post>, Void, Boolean> {

    private Context context;

    public SaveDataAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(List<Post>... params) {

        List<Post> posts = params[0];

        for (Post postResource : posts) {
            ContentValues post_values = new ContentValues();
            post_values.put(DatabaseContract.posts_table.TITLE, postResource.getTitle());
            post_values.put(DatabaseContract.posts_table.USERNAME, postResource.getUsername());
            post_values.put(DatabaseContract.posts_table.IMAGE_LINK, postResource.getImageLink());
            post_values.put(DatabaseContract.posts_table.POST_REF, postResource.getPostRef());

            String[] postRef = { postResource.getPostRef() };

            context.getContentResolver().update(
                DatabaseContract.BASE_CONTENT_URI,
                post_values,
                DatabaseContract.posts_table.POST_REF + " = ?",
                postRef);
        }

        return true;
    }

}


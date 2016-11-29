package com.example.byron.vrviewer;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Byron on 11/28/2016.
 */

public class DatabaseContract {

    public static final String CONTENT_AUTHORITY = "com.example.byron.vrviewer";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_POST = "post";

    public static final String POSTS_TABLE = "posts_table";

    public static final class posts_table implements BaseColumns
    {
        //Table data
        public static final String TITLE = "title";
        public static final String USERNAME = "username";
        public static final String IMAGE_LINK = "imageLink";

        //Types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POST;

        public static Uri buildPosts()
        {
            return BASE_CONTENT_URI.buildUpon().appendPath("id").build();
        }

    }




}

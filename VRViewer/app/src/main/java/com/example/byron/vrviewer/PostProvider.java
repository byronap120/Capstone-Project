package com.example.byron.vrviewer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Byron on 11/28/2016.
 */

public class PostProvider extends ContentProvider {

    private static final int POSTS = 100;

    private static PostDBHelper postDBHelper;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        String content = DatabaseContract.CONTENT_AUTHORITY;

        // All paths to the UriMatcher have a corresponding code to return
        // when a match is found (the ints above).
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, "", POSTS);
        matcher.addURI(content, null, POSTS);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        postDBHelper = new PostDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case POSTS:
                return DatabaseContract.posts_table.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = postDBHelper.getWritableDatabase();
        long _id;
        Uri returnUri;

        int uriMatcher3 = uriMatcher.match(uri);

        switch (uriMatcher.match(uri)) {
            case POSTS:
                _id = db.insert(DatabaseContract.POSTS_TABLE, null, values);
                if (_id > 0) {
                    returnUri = DatabaseContract.BASE_CONTENT_URI;
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Use this on the URI passed into the function to notify any observers that the uri has
        // changed.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

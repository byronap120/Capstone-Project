package com.example.byron.vrviewer.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.byron.vrviewer.DatabaseContract;
import com.example.byron.vrviewer.R;

import java.util.ArrayList;

/**
 * Created by Byron on 11/28/2016.
 */

public class WidgetListProvider implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<Integer> listItemList = new ArrayList<Integer>();
    Context context;
    ContentResolver contentResolver;
    Cursor cursor;

    public WidgetListProvider(Context context, Intent intent) {
        this.context = context;

    }

    @Override
    public void onCreate() {
        contentResolver = context.getContentResolver();
        populateListItem();
    }


    private void populateListItem() {

        cursor = contentResolver.query (DatabaseContract.BASE_CONTENT_URI , null, null, null, null);
/*

        for (int i = 0; i < 10; i++) {
            listItemList.add(i);
        }


*/

    }

    @Override
    public void onDataSetChanged() {
        populateListItem();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        cursor.moveToPosition(position);

        int usernameIndex = cursor.getColumnIndex(DatabaseContract.posts_table.USERNAME);

        RemoteViews mView = new RemoteViews(context.getPackageName(), R.layout.widget_list_row);
        mView.setTextViewText(R.id.textViewUser, cursor.getString(usernameIndex) + "");
        return mView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

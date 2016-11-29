package com.example.byron.vrviewer.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.byron.vrviewer.R;

import java.util.ArrayList;

/**
 * Created by Byron on 11/28/2016.
 */

public class WidgetListProvider implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<Integer> listItemList = new ArrayList<Integer>();
    Context context;

    public WidgetListProvider(Context context, Intent intent) {
        this.context = context;
        populateListItem();
    }

    @Override
    public void onCreate() {
    }


    private void populateListItem() {
        for (int i = 0; i < 10; i++) {
            listItemList.add(i);
        }

    }

    @Override
    public void onDataSetChanged() {
        listItemList.clear();
        populateListItem();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        int num = listItemList.get(position);
        RemoteViews mView = new RemoteViews(context.getPackageName(), R.layout.widget_list_row);
        mView.setTextViewText(R.id.textViewUser, num + "");
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

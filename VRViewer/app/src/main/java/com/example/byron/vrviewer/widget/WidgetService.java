package com.example.byron.vrviewer.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Byron on 11/28/2016.
 */

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        WidgetListProvider dataProvider = new WidgetListProvider(
                getApplicationContext(), intent);
        return dataProvider;
    }

}
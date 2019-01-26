package com.hamada.android.talktome.Widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hamada.android.talktome.MainActivity;
import com.hamada.android.talktome.Model.Users;
import com.hamada.android.talktome.R;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class TalkWidgetProvider extends AppWidgetProvider {
    ArrayList<Users> mListUser = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private String userId;
    private FirebaseUser user;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        /*int[] appWidgetIds holds ids of multiple instance of your widget
         * meaning you are placing more than one widgets on your homescreen*/
        initializeData(N, appWidgetIds, appWidgetManager, context);

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId, ArrayList<Users> mListUser) {

        //which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.talk_widget_provider);

        // Create an Intent to launch MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//        remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

        //setting adapter to listview of the widget
//        remoteViews.setRemoteAdapter(appWidgetId, R.id.listViewWidget,
//                svcIntent);
        // Set up the collection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, remoteViews, mListUser);
        } else {
            setRemoteAdapterV11(context, remoteViews, mListUser);
        }
        //setting an empty view in case of no data
        remoteViews.setEmptyView(R.id.talk_widget_listview, R.id.empty_view);
        return remoteViews;
    }


    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
//        if (WIDGET_BUTTON.equals(intent.getAction())) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, getClass()));
//            Utils.showPercent = !Utils.showPercent;
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.talk_widget_listview);
//        }
    }
    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     * @param mListUser
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views, ArrayList<Users> mListUser) {
        Intent intent = new Intent(context, TalkServiceWidget.class);
        Bundle b = new Bundle();
        b.putParcelableArrayList("key", mListUser);
        intent.putExtra("bundle", b);
        views.setRemoteAdapter(R.id.talk_widget_listview,
                intent);
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     * @param mListUser
     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views, ArrayList<Users> mListUser) {
        Intent intent = new Intent(context, TalkServiceWidget.class);
        Bundle b = new Bundle();
        b.putParcelableArrayList("key", mListUser);
        intent.putExtra("bundle", b);

        views.setRemoteAdapter(0, R.id.talk_widget_listview,
                intent);
    }


    private void initializeData(final int widgetLength, final int[] appWidgetIds, final AppWidgetManager appWidgetManager, final Context context)throws NullPointerException{
        try {

            mListUser.clear();
            mAuth=FirebaseAuth.getInstance();
            user=mAuth.getCurrentUser();
            assert user !=null;
            userId=mAuth.getCurrentUser().getUid();
            mReference=FirebaseDatabase.getInstance().getReference();

            final DatabaseReference usersdRef = mReference.child("users");

            usersdRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dp:dataSnapshot.getChildren()){
                        Users u=new Users(dp.child("user_name").getValue().toString(), "fda");
//                        u.setUser_name(dp.child("user_name").getValue().toString());
                        mListUser.add(u);
                    }
                    for (int i = 0; i < widgetLength; ++i) {
                        RemoteViews remoteViews = updateWidgetListView(context,
                                appWidgetIds[i], mListUser);
                        appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }catch (NullPointerException e)
        {

            e.printStackTrace();
        }
    }
}


package com.hamada.android.talktome.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hamada.android.talktome.Model.Users;
import com.hamada.android.talktome.R;

import java.util.ArrayList;
import java.util.List;

public class WidgetRemoteView implements RemoteViewsService.RemoteViewsFactory {

    public static String TAG=WidgetRemoteView.class.getSimpleName();
    private Context context;
    private Intent intent;
    private List<Users> mListUser=new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private String userId;
    private FirebaseUser user;
    private int appWidgetId;

    public WidgetRemoteView(Context context, Intent intent, ArrayList<Users> mListUser) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        this.mListUser = mListUser;
    }
    private void intializeData()throws NullPointerException{
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
                        Log.d(TAG,u.getUser_name());
                        mListUser.add(u);
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

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        Log.d("UserCount", String.valueOf(mListUser.size()));
        return mListUser.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews=new
                RemoteViews(context.getPackageName(),R.layout.custom_widget_list);
        Log.d("USER_NAME", mListUser.get(position).getUser_name());
        remoteViews.setTextViewText(R.id.widget_user_name,mListUser.get(position).getUser_name());
        return remoteViews;
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

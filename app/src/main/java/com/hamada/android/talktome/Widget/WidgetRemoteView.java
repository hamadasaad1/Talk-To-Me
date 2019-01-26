package com.hamada.android.talktome.Widget;

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
//                        Users u=new Users();
//                        u.setUser_name(dp.child("user_name").getValue().toString());
//                        Log.d(TAG,u.getUser_name());
                        Users users=dp.getValue(Users.class);
                        mListUser.add(users);
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

//    private void displayData(){
//        mListUser.clear();
//        mAuth=FirebaseAuth.getInstance();
//        user=mAuth.getCurrentUser();
//        userId=mAuth.getCurrentUser().getUid();
//
//        if (user !=null){
//            mReference=FirebaseDatabase.getInstance().getReference();
//            final DatabaseReference usersdRef = mReference.child("users");
//            ValueEventListener eventListener=new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//
//                        String name = ds.child("user_name").getValue(String.class);
//                        String image = ds.child("user_image").getValue(String.class);
//                        Users user = ds.getValue(Users.class);
//                        Log.d("TAG", name);
//
//                        //mListUser.add(user);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            };
//            usersdRef.addValueEventListener(eventListener);
//        }
//    }


    public WidgetRemoteView(Context context, Intent intent) {

        this.context=context;
        this.intent=intent;
    }

    @Override
    public void onCreate() {

        intializeData();
        //displayData();
    }

    @Override
    public void onDataSetChanged() {

        intializeData();
        //displayData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {

        return mListUser.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews=new
                RemoteViews(context.getPackageName(),R.layout.custom_widget_list);

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

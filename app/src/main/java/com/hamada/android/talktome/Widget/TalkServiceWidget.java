package com.hamada.android.talktome.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hamada.android.talktome.Model.Users;

import java.util.ArrayList;
import java.util.List;

public class TalkServiceWidget extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        Bundle b = intent.getBundleExtra("bundle");
        ArrayList<Users> mListUser = b.getParcelableArrayList("key");
//        Log.d("USER_LIST case 2", mListUser.toString());

        return (new WidgetRemoteView(this.getApplicationContext(), intent, mListUser));
    }


}

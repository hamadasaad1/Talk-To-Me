package com.hamada.android.talktome.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class CheckConnection extends AsyncTask<Void,Void,Boolean> {

    private Context context;

    public CheckConnection(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        return checkConnection(this.context);
    }

    private Boolean checkConnection(Context context){
        ConnectivityManager connectivityMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connectivityMgr != null;
        NetworkInfo networkInfo = connectivityMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        return networkInfo != null && networkInfo.isConnected();
    }
}

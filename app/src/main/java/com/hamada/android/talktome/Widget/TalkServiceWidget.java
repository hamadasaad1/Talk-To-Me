package com.hamada.android.talktome.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class TalkServiceWidget extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteView(this,intent);
    }
}

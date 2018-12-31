package com.hamada.android.talktome.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hamada.android.talktome.ui.ChatFragment;
import com.hamada.android.talktome.ui.FrindsFragment;
import com.hamada.android.talktome.ui.RequestsFragment;

public class ViewsPagerAdapter extends FragmentPagerAdapter {

    public ViewsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
        switch (position){
            case 0:
                fragment=new RequestsFragment();
                break;
            case 1:
                fragment=new FrindsFragment();
                break;
            case 2:
                fragment=new ChatFragment();
                break;
                default:
                    return null;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title=null;
        switch (position){
            case 0:
                title="Requests";
                break;
            case 1:
                title="Friends";
                break;
            case 2:
                title="Chat";
                break;
          default:
              return null;
        }
        return title;
    }
}

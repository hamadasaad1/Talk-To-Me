package com.hamada.android.talktome;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.hamada.android.talktome.Adapter.ViewsPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuthFirebase;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    private ViewsPagerAdapter mPagerAdapter;

    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        mAuthFirebase=FirebaseAuth.getInstance();
        setSupportActionBar(mToolbar);
        // Show the Up button in the action bar and set recipes name as title.

        mPagerAdapter=new ViewsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mReference=FirebaseDatabase.getInstance().getReference()
                .child("users").child(mAuthFirebase.getCurrentUser().getUid());

    }

    @Override
    protected void onStart() {
        super.onStart();
        //to check the user is register or not
        checkLogin();
    }

    @Override
    protected void onStop() {
        super.onStop();

            mReference.child("online").setValue(ServerValue.TIMESTAMP);


    }
    //check for user is register or null
    private void checkLogin(){
        FirebaseUser user=mAuthFirebase.getCurrentUser();
        if (user==null){
            Intent intent=new Intent(this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else {

            mReference.child("online").setValue("true");
        }
    }

   //for menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    //for handle click for item menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.Action_Setting:
                 startActivity(new Intent(this,SettingActivity.class));
                return true;
            case R.id.Action_AllUser:
                startActivity(new Intent(this,UsersActivity.class));
                return true;
            case R.id.Action_Logout:
                mReference.child("online").setValue(ServerValue.TIMESTAMP);
                //this to logout for account
                mAuthFirebase.signOut();
                //call this method to check for user and go out
                checkLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

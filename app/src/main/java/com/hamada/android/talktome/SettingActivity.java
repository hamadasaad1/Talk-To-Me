package com.hamada.android.talktome;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar mToolbar;
    @BindView(R.id.tv_Change_Name)
    TextView mTvChangeName;
    @BindView(R.id.tv_Change_State)
    TextView mTvChangeState;

    private CircleImageView mImageView;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private final  int GALLERY_REQUEST=10;
    private StorageReference mStorageRef;
    private Bitmap themb=null;
    private StorageReference mThumRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_profile);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mImageView=findViewById(R.id.profile_image);
        mAuth=FirebaseAuth.getInstance();
        String online_user_Id=mAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Profile_images");
        mThumRef=FirebaseStorage.getInstance().getReference().child("thumb_images");
        mDatabaseReference=FirebaseDatabase.getInstance()
                .getReference().child("users").child(online_user_Id);
         mDatabaseReference.keepSynced(true);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String mNameUser=dataSnapshot.child("user_name").getValue().toString();
                String mStateUser=dataSnapshot.child("user_state").getValue().toString();
                String mImageUser=dataSnapshot.child("user_image").getValue().toString();
                final String mThumbUser=dataSnapshot.child("user_thumb_image").getValue().toString();
                mTvChangeName.setText(mNameUser);
                mTvChangeState.setText(mStateUser);
                if (!mImageUser.equals("profile")) {
//                    Picasso.get().load(mThumbUser).placeholder(R.drawable.profile)
//                            .into(mImageView);

                    Picasso.get().load(mThumbUser).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.profile)
                            .into(mImageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {

                                    Picasso.get().load(mThumbUser).placeholder(R.drawable.profile)
                            .into(mImageView);
                                }
                            });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void changeProfileImage(View view) {
//        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        photoPickerIntent.setType("image/*");
//
//        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
// start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);

    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (reqCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                final Uri resultUri = result.getUri();
                File file=new File(resultUri.getPath());

                String User_Id=mAuth.getCurrentUser().getUid();

                try {
                 themb=new Compressor(this)
                         .setMaxWidth(640)
                         .setMaxHeight(480)
                         .setQuality(75)
                         .compressToBitmap(file);

                }catch (IOException e){
                    e.printStackTrace();
                }
                ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
                themb.compress(Bitmap.CompressFormat.JPEG,50,outputStream);
                final byte[] thumb_byte=outputStream.toByteArray();
                final StorageReference thumb_StorageRer=mThumRef.child(User_Id+".jpg");


                final StorageReference riversRef = mStorageRef.child(User_Id+".jpg");
                riversRef.putFile(resultUri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String dwonloadURL=uri.toString();

                                    UploadTask uploadTask=thumb_StorageRer.putBytes(thumb_byte);
                                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            thumb_StorageRer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String thumb_uri=uri.toString();
                                                    Map update=new HashMap();
                                                    update.put("user_image",dwonloadURL);
                                                    update.put("user_thumb_image",thumb_uri);


                                                    mDatabaseReference.updateChildren(update)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()) {
                                                                        Toast.makeText(SettingActivity.
                                                                                        this, "The Profile picture Changed",
                                                                                Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            });
                                        }
                                    });



                                }
                            });

                        }else {
                            Toast.makeText(SettingActivity.this,
                                    "Something happen Try", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }



    }

    public void changeState(View view) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText edittext = new EditText(this);
        alert.setMessage("Enter Your State here");
        alert.setTitle("Change State");

        alert.setView(edittext);

        alert.setPositiveButton(R.string.dialog_action_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String YouEditTextValue = edittext.getText().toString();
                if (TextUtils.isEmpty(YouEditTextValue)) {
                    Toast.makeText(SettingActivity.
                            this, "Please Set State ", Toast.LENGTH_SHORT).show();
                } else {
                    mDatabaseReference.child("user_state").setValue(YouEditTextValue)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SettingActivity.this,
                                                "Your State Changed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SettingActivity.this,
                                                "Try Late ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        alert.setNegativeButton(R.string.dialog_action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
                    dialog.dismiss();

            }
        });

        alert.show();
    }
}

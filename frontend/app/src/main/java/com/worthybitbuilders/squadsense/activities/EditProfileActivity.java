package com.worthybitbuilders.squadsense.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityEditProfileBinding;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.ImageUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;

    private UserViewModel userViewModel;
    private UserModel currentUser;
    private Uri avatarUri;
    String bucketName = "squadsense";
    String region = "ap-southeast-1";
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getUserById(SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID), new UserViewModel.UserCallback() {
            @Override
            public void onSuccess(UserModel user) {
                currentUser = user;
                if(user.getName() != null && !user.getName().isEmpty())
                {
                    binding.name.setText(user.getName());
                    binding.defaultImageProfileText.setText(String.valueOf(binding.name.getText().charAt(0)));
                }
                else {
                    binding.name.setText("Anonymous");
                    binding.defaultImageProfileText.setText("A");
                }

                if(user.getProfileImagePath() != null && !user.getProfileImagePath().isEmpty())
                {
                    try{
                        String profileImagePath = user.getProfileImagePath();
                        String publicProfileImageURL = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, profileImagePath);

                        Glide.with(EditProfileActivity.this)
                                .load(publicProfileImageURL)
                                .into(binding.imageProfile);
                        loadAvatarView(true);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    loadAvatarView(false);

                if(user.getIntroduction() != null && !user.getIntroduction().isEmpty())
                {
                    binding.introduction.setText(user.getIntroduction());
                }

                if(user.getEmail() != null && !user.getEmail().isEmpty())
                {
                    binding.email.setText(user.getEmail());
                }

                if(user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty())
                {
                    binding.phonenumber.setText(user.getPhoneNumber());
                }

                if(user.getBirthday() != null && !user.getBirthday().isEmpty())
                {
                    binding.birthday.setText(user.getBirthday());
                }
            }

            @Override
            public void onFailure(String message) {
                Toast t = Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP, 0, 0);
                t.show();
            }
        });

        //set onclick buttons here
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileActivity.super.onBackPressed();
            }
        });

        binding.btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCamera_showPopup();
            }
        });

        binding.imageProfileBorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCamera_showPopup();
            }
        });

        binding.btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.name.getText().toString() != null && !binding.name.getText().toString().isEmpty())
                    currentUser.setName(binding.name.getText().toString());
                else
                    currentUser.setName("Anonymous");

                if(avatarUri != null)
                {
                    File avatarFile = UriToFile(avatarUri);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), avatarFile);
                    MultipartBody.Part avatarFilePart = MultipartBody.Part.createFormData("avatar-file", avatarFile.getName(), requestBody);
                    String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USERID);
                    userViewModel.uploadAvatar(userId, avatarFilePart, new UserViewModel.UploadAvatarCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(EditProfileActivity.this, "Save image successfully!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String message) {
                            binding.introduction.setText(message);
                        }
                    });
                }
                currentUser.setIntroduction(binding.introduction.getText().toString());
                currentUser.setPhoneNumber(binding.phonenumber.getText().toString());
                currentUser.setBirthday(binding.birthday.getText().toString());
                userViewModel.updateUser(currentUser, new UserViewModel.UserCallback() {
                    @Override
                    public void onSuccess(UserModel user) {
                        Toast t = Toast.makeText(EditProfileActivity.this, "user updated!", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP, 0, 0);
                        t.show();

                        EditProfileActivity.super.onBackPressed();
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast t = Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP, 0, 0);
                        t.show();
                    }
                });
            }
        });

        setContentView(binding.getRoot());
    }



    //define function here
    private void btnCamera_showPopup() {
        final Dialog dialog = new Dialog(getWindow().getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_of_camera);
        //Set activity of button in dialog here
        TextView tvOpenCamera = (TextView) dialog.findViewById(R.id.option_open_camera);
        TextView tvUploadPhoto = (TextView) dialog.findViewById(R.id.option_upload_photo);

        tvOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                avatarUri = createImage();
                myCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, avatarUri);
                startActivityIfNeeded(myCameraIntent, CAMERA_PERMISSION_REQUEST_CODE);
            }
        });

        tvUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                myFileIntent.setType("image/*");
                startActivityIfNeeded(myFileIntent, STORAGE_PERMISSION_REQUEST_CODE);
            }
        });
        //
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode, data);
        switch (requestCode)
        {
            case STORAGE_PERMISSION_REQUEST_CODE:
                if(resultCode == RESULT_OK)
                {
                    //display image
                    avatarUri = data.getData();
                    binding.imageProfile.setImageURI(avatarUri);
                    loadAvatarView(true);
                    return;
                }
            case CAMERA_PERMISSION_REQUEST_CODE:
                if(resultCode == RESULT_OK)
                {
                    Toast.makeText(this, "image captured", Toast.LENGTH_SHORT).show();
                    return;
                }
        }
    }

    private Uri createImage(){
        Uri uri = null;
        ContentResolver resolver = getContentResolver();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        else
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String imageName = String.valueOf(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageName + ".jpg");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "My Images/");
        Uri finalUri = resolver.insert(uri, contentValues);
        return finalUri;
    }

    private void loadAvatarView(boolean hasAvatar)
    {
        if(hasAvatar)
        {
            binding.imageProfileBorder.setVisibility(View.VISIBLE);
            binding.defaultImageProfile.setVisibility(View.GONE);
        }
        else
        {
            binding.imageProfileBorder.setVisibility(View.GONE);
            binding.defaultImageProfile.setVisibility(View.VISIBLE);
        }
    }

    private File UriToFile(Uri uri)
    {
        Bitmap bitmap;
        Bitmap rotatedBitmap;
        File file = null;
        try {
            bitmap = ImageUtils.uriToBitmap(EditProfileActivity.this, uri);
            rotatedBitmap = ImageUtils.rotateBitmapIfRequired(EditProfileActivity.this, bitmap, avatarUri);
            file = ImageUtils.bitmapToFile(EditProfileActivity.this, rotatedBitmap, "avatar.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}

package com.worthybitbuilders.squadsense.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityEditProfileBinding;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.EventChecker;
import com.worthybitbuilders.squadsense.utils.ImageUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private UserViewModel userViewModel;
    private UserModel currentUser;
    private Uri avatarUri;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;

    Dialog loadingDialog;

    Dialog dialog; //current dialog

    EventChecker eventChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        eventChecker = new EventChecker();
        loadingDialog = DialogUtils.GetLoadingDialog(this);
        loadingDialog.show();
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
                        Glide.with(EditProfileActivity.this)
                                .load(user.getProfileImagePath())
                                .into(binding.imageProfile);
                        loadAvatarView(true);
                    }
                    catch (Exception e)
                    {
                        ToastUtils.showToastError(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
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

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                loadingDialog.dismiss();
                ToastUtils.showToastError(EditProfileActivity.this, message, Toast.LENGTH_SHORT);
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
                loadingDialog.show();
                eventChecker.setActionWhenComplete(new EventChecker.CompleteCallback() {
                    @Override
                    public void Action() {
                        loadingDialog.dismiss();
                        EditProfileActivity.this.onBackPressed();
                        finish();
                    }
                });

                if(binding.name.getText().toString() != null && !binding.name.getText().toString().isEmpty())
                    currentUser.setName(binding.name.getText().toString());
                else
                    currentUser.setName("Anonymous");

                if(avatarUri != null)
                {
                    final int SAVE_AVATAR_INDEX = eventChecker.addEventStatusAndGetCode();
                    File avatarFile = UriToFile(avatarUri);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), avatarFile);
                    MultipartBody.Part avatarFilePart = MultipartBody.Part.createFormData("avatar-file", avatarFile.getName(), requestBody);
                    String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
                    userViewModel.uploadAvatar(userId, avatarFilePart, new UserViewModel.UploadAvatarCallback() {
                        @Override
                        public void onSuccess() {
                            eventChecker.markEventAsCompleteAndDoActionIfNeeded(SAVE_AVATAR_INDEX);
                            ToastUtils.showToastSuccess(EditProfileActivity.this, "Save image successfully!", Toast.LENGTH_SHORT);
                        }

                        @Override
                        public void onFailure(String message) {
                            eventChecker.markEventAsCompleteAndDoActionIfNeeded(SAVE_AVATAR_INDEX);
                            ToastUtils.showToastError(EditProfileActivity.this, "Save image failed", Toast.LENGTH_SHORT);
                        }
                    });
                }
                currentUser.setIntroduction(binding.introduction.getText().toString());
                currentUser.setPhoneNumber(binding.phonenumber.getText().toString());
                currentUser.setBirthday(binding.birthday.getText().toString());
                final int SAVE_INFO_USER_INDEX = eventChecker.addEventStatusAndGetCode();
                userViewModel.updateUser(currentUser, new UserViewModel.UserCallback() {
                    @Override
                    public void onSuccess(UserModel user) {
                        eventChecker.markEventAsCompleteAndDoActionIfNeeded(SAVE_INFO_USER_INDEX);
                        ToastUtils.showToastSuccess(EditProfileActivity.this, "user updated!", Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailure(String message) {
                        eventChecker.markEventAsCompleteAndDoActionIfNeeded(SAVE_INFO_USER_INDEX);
                        ToastUtils.showToastError(EditProfileActivity.this, message, Toast.LENGTH_SHORT);
                    }
                });
            }
        });

        setContentView(binding.getRoot());
    }



    //define function here
    private void btnCamera_showPopup() {
        dialog = new Dialog(getWindow().getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_of_camera);
        //Set activity of button in dialog here
        TextView tvOpenCamera = (TextView) dialog.findViewById(R.id.option_open_camera);
        TextView tvUploadPhoto = (TextView) dialog.findViewById(R.id.option_upload_photo);

        tvOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkPermissionAndAskForIt(
                        EditProfileActivity.this,
                        Manifest.permission.CAMERA,
                        CAMERA_PERMISSION_REQUEST_CODE)
                ) return;
                getPhotoFromCamera();
            }
        });

        tvUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageStorage();
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
                    if(dialog != null)
                        dialog.dismiss();
                    return;
                }
            case CAMERA_PERMISSION_REQUEST_CODE:
                if(resultCode == RESULT_OK)
                {
                    if (avatarUri != null) {
                        // Ảnh đã chụp thành công, sử dụng URI để hiển thị ảnh
                        binding.imageProfile.setImageURI(avatarUri);
                        loadAvatarView(true);
                        if(dialog != null)
                            dialog.dismiss();
                    } else {
                        ToastUtils.showToastError(this, "Capture image failed, please try again", Toast.LENGTH_SHORT);
                    }
                }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        return imageFile;
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

    private boolean checkPermissionAndAskForIt(Context context, String permission, int PERMISSION_REQUEST_CODE) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    PERMISSION_REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    private void openImageStorage()
    {
        Intent myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        myFileIntent.setType("image/*");
        startActivityIfNeeded(myFileIntent, STORAGE_PERMISSION_REQUEST_CODE);
    }
    private void getPhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ToastUtils.showToastError(this, "Unable to take picture, please try again", Toast.LENGTH_LONG);
            }

            if (photoFile != null) {
                avatarUri = FileProvider.getUriForFile(this,
                        "com.worthybitbuilders.squadsense.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, avatarUri);
                startActivityIfNeeded(takePictureIntent, CAMERA_PERMISSION_REQUEST_CODE);
            }
        } else {
            ToastUtils.showToastError(this, "No usable camera, operation failed", Toast.LENGTH_LONG);
        }
    }
}

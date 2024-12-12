package com.worthybitbuilders.squadsense.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.AttachFileAdapter;
import com.worthybitbuilders.squadsense.adapters.FriendItemAdapter;
import com.worthybitbuilders.squadsense.adapters.MessageAdapter;
import com.worthybitbuilders.squadsense.adapters.holders.GroupChatMemberAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityMessagingBinding;
import com.worthybitbuilders.squadsense.databinding.ColumnMoreOptionsBinding;
import com.worthybitbuilders.squadsense.databinding.ConfirmDeleteSecondaryBinding;
import com.worthybitbuilders.squadsense.databinding.FriendMoreOptionBinding;
import com.worthybitbuilders.squadsense.databinding.PopupChatSettingBinding;
import com.worthybitbuilders.squadsense.factory.MessageActivityViewModelFactory;
import com.worthybitbuilders.squadsense.models.ChatMessage;
import com.worthybitbuilders.squadsense.models.ChatRoom;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.services.UtilService;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.FriendViewModel;
import com.worthybitbuilders.squadsense.viewmodels.MessageActivityViewModel;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagingActivity extends AppCompatActivity {
    private final int CAMERA_PERMISSION_REQUEST_CODE = 123123;
    private ActivityMessagingBinding binding;
    private MessageActivityViewModel messageViewModel;
    private MessageAdapter messageAdapter;
    private final UtilService utilService = RetrofitServices.getUtilService();
    private final int OPEN_FILE_REQUEST_CODE = 123;
    private final int OPEN_IMAGE_REQUEST_CODE = 1234;
    private final int OPEN_CAMERA_REQUEST_CODE = 12345;
    private AttachFileAdapter attachFileAdapter;
    private final List<Uri> fileUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        binding = ActivityMessagingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent getIntent = getIntent();
        String chatRoomId = getIntent.getStringExtra("chatRoomId");
        MessageActivityViewModelFactory factory = new MessageActivityViewModelFactory(chatRoomId);
        messageViewModel = new ViewModelProvider(this, factory).get(MessageActivityViewModel.class);
        getChatRoomInformation();

        binding.rvAttach.setLayoutManager(new LinearLayoutManager(MessagingActivity.this, LinearLayoutManager.HORIZONTAL, false));
        attachFileAdapter = new AttachFileAdapter(fileUris, this, position -> {
            fileUris.remove(position);
            attachFileAdapter.notifyItemRemoved(position);
            attachFileAdapter.notifyItemRangeChanged(position, fileUris.size());
        });
        binding.rvAttach.setAdapter(attachFileAdapter);

        listenForNewMessage();

        Dialog loadingDialog = DialogUtils.GetLoadingDialog(this);
        loadingDialog.show();
        messageViewModel.getAllMessage(new MessageActivityViewModel.ApiCallHandler() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess() {
                messageAdapter.notifyDataSetChanged();
                binding.rvMessage.scrollToPosition(messageViewModel.getMessageList().size());
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(MessagingActivity.this, "Unable to get your messages", Toast.LENGTH_LONG);
                loadingDialog.dismiss();
            }
        });

        messageAdapter = new MessageAdapter(this, messageViewModel.getMessageList());
        binding.rvMessage.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMessage.setAdapter(messageAdapter);

        binding.btnSetting.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            PopupChatSettingBinding popupBinding = PopupChatSettingBinding.inflate(getLayoutInflater());
            dialog.setContentView(popupBinding.getRoot());

            ChatRoom chatRoom = messageViewModel.getChatRoom();
            popupBinding.popupTitle.setText(chatRoom.getTitle());
            popupBinding.btnCamera.setOnClickListener((v) -> showChangeLogo());

            GroupChatMemberAdapter memberAdapter = new GroupChatMemberAdapter(messageViewModel.getChatRoom().getMembers());
            memberAdapter.setClickHandler((position, anchor) -> showMoreOption(position, anchor, memberAdapter));
            popupBinding.rvGroupMembers.setLayoutManager(new LinearLayoutManager(MessagingActivity.this));
            popupBinding.rvGroupMembers.setAdapter(memberAdapter);
            popupBinding.btnLeave.setOnClickListener((v) -> showConfirmLeave());
//            popupBinding.btnAddMember.setOnClickListener((v) -> );

            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.show();
        });

        binding.btnVideoCall.setOnClickListener(view -> {
            Intent callIntent = new Intent(this, CallVideoActivity.class);
            callIntent.putExtra("chatRoomId", chatRoomId);
            callIntent.putExtra("isVideoCall", true);
            callIntent.putExtra("chatRoomTitle", messageViewModel.getChatRoom().getTitle());
            callIntent.putExtra("isCaller", true);
            startActivity(callIntent);
        });

        binding.btnVoiceCall.setOnClickListener(view -> {
            Intent callIntent = new Intent(this, CallVideoActivity.class);
            callIntent.putExtra("chatRoomId", chatRoomId);
            callIntent.putExtra("isVideoCall", false);
            callIntent.putExtra("chatRoomTitle", messageViewModel.getChatRoom().getTitle());
            callIntent.putExtra("isCaller", true);
            startActivity(callIntent);
        });

        binding.etEnterMessage.setOnClickListener(view -> {
            binding.etEnterMessage.setCursorVisible(true);

            binding.btnAttachFile.setVisibility(View.GONE);
            binding.btnAttachImage.setVisibility(View.GONE);
            binding.btnTakeCamera.setVisibility(View.GONE);
            binding.btnShowMoreIcon.setVisibility(View.VISIBLE);

            Animation iconAppear = AnimationUtils.loadAnimation(this, R.anim.animated_fade_visible);
            binding.btnShowMoreIcon.setAnimation(iconAppear);
            iconAppear.start();
        });

        binding.etEnterMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int s, int start, int after) {
                if (charSequence.length() > 0) binding.etEnterMessage.performClick();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.btnSend.setOnClickListener((view -> {
            String message = String.valueOf(binding.etEnterMessage.getText());
            if(message.isEmpty() && fileUris.size() == 0) return;

            binding.etEnterMessage.setText("");
            if (fileUris.size() == 0) {
                messageViewModel.sendNewMessage(message, new ArrayList<>());
                return;
            }

            List<MultipartBody.Part> parts = new ArrayList<>();
            for (int i = 0; i < fileUris.size(); i++) {
                try {
                    Uri fileUri = fileUris.get(i);
                    String fileName = getFileName(fileUri);

                    InputStream inputStream = getContentResolver().openInputStream(fileUri);
                    File file = createTempFile(fileUri);
                    copyInputStreamToFile(inputStream, file);
                    RequestBody requestFile = RequestBody.create(MediaType.parse(getMimeType(fileUri)), file);
                    parts.add(MultipartBody.Part.createFormData("files", fileName, requestFile));
                } catch (IOException ignored) {}
            }

            fileUris.clear();
            attachFileAdapter.notifyDataSetChanged();
            binding.rvAttach.setVisibility(View.GONE);

            utilService.uploadFiles(parts).enqueue(new Callback<List<ChatMessage.MessageFile>>() {
                @Override
                public void onResponse(Call<List<ChatMessage.MessageFile>> call, Response<List<ChatMessage.MessageFile>> response) {
                    if (response.isSuccessful()) {
                        messageViewModel.sendNewMessage(message, response.body());
                    } else ToastUtils.showToastError(MessagingActivity.this, "Unable to send message, please try again", Toast.LENGTH_LONG);
                }

                @Override
                public void onFailure(Call<List<ChatMessage.MessageFile>> call, Throwable t) {
                    ToastUtils.showToastError(MessagingActivity.this, "Something went wrong while trying to send message", Toast.LENGTH_LONG);
                }
            });
        }));

        binding.btnAttachFile.setOnClickListener(view -> {
            openFileStorage();
        });

        binding.btnAttachImage.setOnClickListener(view -> {
            openImageStorage();
        });

        binding.btnTakeCamera.setOnClickListener(view -> {
            if(!checkPermissionAndAskForIt(MessagingActivity.this, Manifest.permission.CAMERA, OPEN_CAMERA_REQUEST_CODE)) return;
            openCamera();
        });

        binding.btnShowMoreIcon.setOnClickListener(view -> {
            binding.etEnterMessage.setCursorVisible(false);

            binding.btnAttachFile.setVisibility(View.VISIBLE);
            binding.btnAttachImage.setVisibility(View.VISIBLE);
            binding.btnTakeCamera.setVisibility(View.VISIBLE);
            binding.btnShowMoreIcon.setVisibility(View.GONE);

            Animation iconAppear = AnimationUtils.loadAnimation(this, R.anim.animated_fade_visible);

            binding.btnAttachFile.setAnimation(iconAppear);
            binding.btnAttachImage.setAnimation(iconAppear);
            binding.btnTakeCamera.setAnimation(iconAppear);

            iconAppear.start();
        });

        binding.btnClose.setOnClickListener(view -> finish());
    }

    private void showMoreOption(int position, View anchor, GroupChatMemberAdapter memberAdapter) {
        // TODO: change this binding
        FriendMoreOptionBinding popupBinding = FriendMoreOptionBinding.inflate(getLayoutInflater());
        popupBinding.btnAdd.setVisibility(View.GONE);
        PopupWindow popupWindow = new PopupWindow(popupBinding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupBinding.btnDeleteFriend.setOnClickListener(view -> {
            showConfirmDelete(position, memberAdapter);
            popupWindow.dismiss();
        });

        popupWindow.setElevation(50);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(anchor, 0, 0);
    }

    private void getChatRoomInformation() {
        messageViewModel.getChatRoomInfor().enqueue(new Callback<ChatRoom>() {
            @Override
            public void onResponse(@NonNull Call<ChatRoom> call, @NonNull Response<ChatRoom> response) {
                if (response.isSuccessful()) {
                    ChatRoom chatRoom = response.body();
                    // TODO: FIX THIS, MOVE IT TO THE VIEW MODEL INSTEAD OF THIS
                    messageViewModel.setChatRoom(chatRoom);
                    if (chatRoom == null) {
                        ToastUtils.showToastError(MessagingActivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG);
                        return;
                    }

                    if (chatRoom.getTitle() == null || chatRoom.getTitle().isEmpty()) {
                        String otherUserName = null;
                        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
                        // get the user that is different from the current user to take name
                        for (int i = 0; i < chatRoom.getMembers().size(); i++) {
                            if (!Objects.equals(chatRoom.getMembers().get(i).get_id(), userId)) {
                                otherUserName = chatRoom.getMembers().get(i).getName();
                                break;
                            }
                        }
                        chatRoom.setTitle(otherUserName);
                    }

                    // put the chat room image
                    if (chatRoom.getLogoPath() != null && !chatRoom.getLogoPath().isEmpty()) {}
                    else if (chatRoom.isGroup()) {
                        chatRoom.setLogoPath("");
                    } else {
                        String imagePath = null;
                        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
                        // get the first user that is different from the current user to take the image
                        for (int i = 0; i < chatRoom.getMembers().size(); i++) {
                            if (!Objects.equals(chatRoom.getMembers().get(i).get_id(), userId)) {
                                imagePath = chatRoom.getMembers().get(i).getProfileImagePath();
                                break;
                            }
                        }

                        chatRoom.setLogoPath(imagePath);
                    }

                    // TODO: add group call functionality
                    if (chatRoom.isGroup()) {
                        binding.btnVideoCall.setVisibility(View.GONE);
                        binding.btnVoiceCall.setVisibility(View.GONE);
                        binding.btnSetting.setVisibility(View.GONE);
                    }

                    binding.chatRoomTitle.setText(chatRoom.getTitle());

                    int placeHolder;
                    if (chatRoom.isGroup()) placeHolder = R.drawable.ic_group;
                    else placeHolder = R.drawable.ic_user;
                    Glide
                            .with(MessagingActivity.this)
                            .load(chatRoom.getLogoPath())
                            .placeholder(placeHolder)
                            .into(binding.chatRoomImage);
                } else ToastUtils.showToastError(MessagingActivity.this, response.message(), Toast.LENGTH_LONG);
            }

            @Override
            public void onFailure(@NonNull Call<ChatRoom> call, @NonNull Throwable t) {
                ToastUtils.showToastError(MessagingActivity.this, t.getMessage(), Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        messageViewModel.changeSocketEventsOnEnter();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messageViewModel.changeSocketEventsOnLeave();
    }

    // the purpose is only to notify about the new message so we don't need "s"
    private void listenForNewMessage() {
        messageViewModel.getNewMessageLiveData().observe(this, s -> {
            int pos = messageViewModel.getMessageList().size() - 1;
            messageAdapter.notifyItemInserted(pos);
            binding.rvMessage.scrollToPosition(pos);
        });
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

    private void openFileStorage()
    {
        Intent myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        myFileIntent.setType("*/*");
        startActivityIfNeeded(myFileIntent, OPEN_FILE_REQUEST_CODE);
    }

    private void openImageStorage()
    {
        Intent myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        myFileIntent.setType("image/*");
        startActivityIfNeeded(myFileIntent, OPEN_IMAGE_REQUEST_CODE);
    }

    private void openCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // the "if" below not working on some phone
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        try {
            File photoFile = null;
            try {
                photoFile = createTempImageFile();
            } catch (IOException ex) {
                ToastUtils.showToastError(this, "Unable to take picture, please try again", Toast.LENGTH_LONG);
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.worthybitbuilders.squadsense.fileprovider",
                        photoFile);
                fileUris.add(photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityIfNeeded(takePictureIntent, OPEN_CAMERA_REQUEST_CODE);
            }
        } catch (Exception e) {
//            ToastUtils.showToastError(this, "No usable camera, operation failed", Toast.LENGTH_LONG);
            ToastUtils.showToastError(this, "Something wrong with camera", Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode, data);
        switch (requestCode)
        {
            case OPEN_FILE_REQUEST_CODE:
                if(resultCode == RESULT_OK)
                {
                    if (data == null) return;
                    Uri fileUri = data.getData();
                    boolean isAdded = false;
                    for (int i = 0; i < fileUris.size(); i++) {
                        if (areUrisEqual(fileUris.get(i), fileUri)) {
                            isAdded = true;
                            break;
                        }
                    }

                    if (!isAdded) {
                        fileUris.add(fileUri);
                        attachFileAdapter.notifyItemInserted(fileUris.size() - 1);
                    } else {
                        ToastUtils.showToastError(this, "File is already selected", Toast.LENGTH_SHORT);
                    }

                    if(fileUris.size() > 0) binding.rvAttach.setVisibility(View.VISIBLE);
                    else binding.rvAttach.setVisibility(View.GONE);
                }
                break;
            case OPEN_IMAGE_REQUEST_CODE:
                if(resultCode == RESULT_OK)
                {
                    if (data == null) return;
                    Uri fileUri = data.getData();
                    boolean isAdded = false;
                    for (int i = 0; i < fileUris.size(); i++) {
                        if (areUrisEqual(fileUris.get(i), fileUri)) {
                            isAdded = true;
                            break;
                        }
                    }

                    if (!isAdded) {
                        fileUris.add(fileUri);
                        attachFileAdapter.notifyItemInserted(fileUris.size() - 1);
                    } else {
                        ToastUtils.showToastError(this, "Image is already selected", Toast.LENGTH_SHORT);
                    }

                    if(fileUris.size() > 0) binding.rvAttach.setVisibility(View.VISIBLE);
                    else binding.rvAttach.setVisibility(View.GONE);
                }
                break;
            case OPEN_CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    attachFileAdapter.notifyItemInserted(fileUris.size() - 1);
                } else {
                    fileUris.remove(fileUris.size() - 1);
                }

                if(fileUris.size() > 0) binding.rvAttach.setVisibility(View.VISIBLE);
                else binding.rvAttach.setVisibility(View.GONE);
                break;
        }
    }

    public boolean areUrisEqual(Uri firstUri, Uri secondUri) {
        ContentResolver contentResolver = getContentResolver();

        // Get the document IDs for the URIs
        String documentId1 = DocumentsContract.getDocumentId(firstUri);
        String documentId2 = DocumentsContract.getDocumentId(secondUri);

        // Retrieve the DocumentFile instances using the document IDs
        DocumentFile document1 = DocumentFile.fromSingleUri(this, firstUri);
        DocumentFile document2 = DocumentFile.fromSingleUri(this, secondUri);

        // Check if the documents are null or not
        if (document1 == null || document2 == null) {
            return false;
        }

        // Compare the document IDs
        boolean areDocumentIdsEqual = documentId1.equals(documentId2);

        // Compare the actual files using their content URIs
        boolean areFilesEqual = document1.getUri().equals(document2.getUri());

        // Return true if both the document IDs and files are equal
        return areDocumentIdsEqual && areFilesEqual;
    }
    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private File createTempImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private File createTempFile(Uri fileUri) throws IOException {
        String fileName = getFileName(fileUri);
        String fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(fileUri));
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        return File.createTempFile(fileName, fileExtension, storageDir);
    }

    private void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024]; // 4KB buffer
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    private void showChangeLogo() {
        Dialog dialog = new Dialog(getWindow().getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_of_camera);
        //Set activity of button in dialog here
        TextView tvOpenCamera = (TextView) dialog.findViewById(R.id.option_open_camera);
        TextView tvUploadPhoto = (TextView) dialog.findViewById(R.id.option_upload_photo);

        tvOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkPermissionAndAskForIt(
                        MessagingActivity.this,
                        Manifest.permission.CAMERA,
                        CAMERA_PERMISSION_REQUEST_CODE)
                ) return;
                getPhotoFromCamera();
            }
        });

        tvUploadPhoto.setOnClickListener(view -> openImageStorage());
        //
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
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
                Uri avatarUri = FileProvider.getUriForFile(this,
                        "com.worthybitbuilders.squadsense.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, avatarUri);
                startActivityIfNeeded(takePictureIntent, CAMERA_PERMISSION_REQUEST_CODE);
            }
        } else {
            ToastUtils.showToastError(this, "No usable camera, operation failed", Toast.LENGTH_LONG);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        return imageFile;
    }
    private void showConfirmDelete(int position, GroupChatMemberAdapter memberAdapter) {
        final Dialog confirmDialog = new Dialog(this);
        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ConfirmDeleteSecondaryBinding binding = ConfirmDeleteSecondaryBinding.inflate(getLayoutInflater());
        confirmDialog.setContentView(binding.getRoot());

        binding.tvTitle.setText("Delete member");
        binding.tvAdditionalContent.setText(String.format(Locale.US, "Are you sure to remove %s", messageViewModel.getChatRoom().getMembers().get(position).name));
        binding.btnCancel.setOnClickListener(view -> confirmDialog.dismiss());
        binding.btnConfirm.setOnClickListener(view -> {
            messageViewModel.deleteMemberFromGroup(messageViewModel.getChatRoom().getMembers().get(position)._id, new MessageActivityViewModel.ApiCallHandler() {
                @Override
                public void onSuccess() {
                    memberAdapter.notifyItemRemoved(position);
                    memberAdapter.notifyItemRangeChanged(position, messageViewModel.getChatRoom().getMembers().size());
                    ToastUtils.showToastSuccess(MessagingActivity.this, "Updated", Toast.LENGTH_SHORT);
                    confirmDialog.dismiss();
                }

                @Override
                public void onFailure(String message) {
                    ToastUtils.showToastSuccess(MessagingActivity.this, message, Toast.LENGTH_SHORT);
                    confirmDialog.dismiss();
                }
            });
        });

        confirmDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        confirmDialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        confirmDialog.getWindow().setGravity(Gravity.CENTER);
        confirmDialog.show();
    }

    private void showConfirmLeave() {
        final Dialog confirmDialog = new Dialog(this);
        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ConfirmDeleteSecondaryBinding binding = ConfirmDeleteSecondaryBinding.inflate(getLayoutInflater());
        confirmDialog.setContentView(binding.getRoot());

        binding.tvTitle.setText("Leave");
        binding.tvAdditionalContent.setText("Are you sure leave");
        binding.btnCancel.setOnClickListener(view -> confirmDialog.dismiss());
        binding.btnConfirm.setOnClickListener(view -> {
            String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
            messageViewModel.deleteMemberFromGroup(userId, new MessageActivityViewModel.ApiCallHandler() {
                @Override
                public void onSuccess() {
                    finish();
                    confirmDialog.dismiss();
                }

                @Override
                public void onFailure(String message) {
                    ToastUtils.showToastSuccess(MessagingActivity.this, message, Toast.LENGTH_SHORT);
                    confirmDialog.dismiss();
                }
            });
        });

        confirmDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        confirmDialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        confirmDialog.getWindow().setGravity(Gravity.CENTER);
        confirmDialog.show();
    }

//    private void LoadlistFriend()
//    {
//        loadingDialog.show();
//        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
//        friendViewModel.getFriendById(userId, new FriendViewModel.getFriendCallback() {
//            @Override
//            public void onSuccess(List<UserModel> friends) {
//                listFriend.clear();
//                listFriend.addAll(friends);
//
//                binding.rvFriends.setAdapter(friendItemAdapter);
//                LoadListFriendView();
//                loadingDialog.dismiss();
//            }
//
//            @Override
//            public void onFailure(String message) {
//                ToastUtils.showToastError(FriendActivity.this, message, Toast.LENGTH_SHORT);
//                loadingDialog.dismiss();
//            }
//        });
//
//        friendItemAdapter.setOnClickListener(new FriendItemAdapter.OnActionCallback() {
//            @Override
//            public void OnItemClick(int position) {}
//
//            @Override
//            public void OnMoreOptionsClick(int position) {
//                FriendMoreOptionBinding popupBinding = FriendMoreOptionBinding.inflate(getLayoutInflater());
//                popupBinding.btnAdd.setVisibility(View.GONE);
//                PopupWindow popupWindow = new PopupWindow(popupBinding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
//                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                popupWindow.setElevation(50);
//                popupWindow.setTouchable(true);
//                popupWindow.setOutsideTouchable(true);
//                View anchor = binding.rvFriends.getLayoutManager().findViewByPosition(position);
//                if (anchor != null) popupWindow.showAsDropDown(anchor, 0, 0);
//
//                popupBinding.btnDeleteFriend.setOnClickListener(view -> {
//                    showConfirmDelete(position);
//                    popupWindow.dismiss();
//                });
//            }
//        });
//    }
}
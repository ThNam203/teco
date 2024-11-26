package com.worthybitbuilders.squadsense.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
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
import com.worthybitbuilders.squadsense.adapters.MessageAdapter;
import com.worthybitbuilders.squadsense.adapters.NewUpdateTaskFileAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityMessagingBinding;
import com.worthybitbuilders.squadsense.factory.MessageActivityViewModelFactory;
import com.worthybitbuilders.squadsense.models.ChatMessage;
import com.worthybitbuilders.squadsense.models.ChatRoom;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.services.UtilService;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.MessageActivityViewModel;

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
    private ActivityMessagingBinding binding;
    private MessageActivityViewModel messageViewModel;
    private MessageAdapter messageAdapter;
    private String chatRoomImagePath;
    private String chatRoomTitle;
    private UtilService utilService = RetrofitServices.getUtilService();
    private final int OPEN_FILE_REQUEST_CODE = 0;
    private final int OPEN_IMAGE_REQUEST_CODE = 1;
    private final int OPEN_CAMERA_REQUEST_CODE = 2;

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

        chatRoomImagePath = getIntent.getStringExtra("chatRoomImage");
        chatRoomTitle = getIntent.getStringExtra("chatRoomTitle");
        // this most likely to happen if a user navigate to this activity using a notification
        // because a notification only give the id, not chatRoomImagePath and chatRoomTitle
        if (chatRoomImagePath == null || chatRoomTitle == null) getChatRoomInformation();

        binding.rvAttach.setLayoutManager(new LinearLayoutManager(MessagingActivity.this, LinearLayoutManager.HORIZONTAL, false));
        attachFileAdapter = new AttachFileAdapter(fileUris, this, position -> {
            fileUris.remove(position);
            attachFileAdapter.notifyItemRemoved(position);
            attachFileAdapter.notifyItemRangeChanged(position, fileUris.size());
        });
        binding.rvAttach.setAdapter(attachFileAdapter);
        binding.chatRoomTitle.setText(chatRoomTitle);
        Glide
            .with(this)
            .load(chatRoomImagePath)
            .placeholder(R.drawable.ic_user)
            .into(binding.chatRoomImage);


        listenForNewMessage();

        Dialog loadingDialog = DialogUtils.GetLoadingDialog(this);
        loadingDialog.show();
        messageViewModel.getAllMessage(new MessageActivityViewModel.ApiCallHandler() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess() {
                messageAdapter.notifyDataSetChanged();
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

        binding.btnVideoCall.setOnClickListener(view -> {
            Intent callIntent = new Intent(this, CallVideoActivity.class);
            callIntent.putExtra("chatRoomId", chatRoomId);
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

    private void getChatRoomInformation() {
        messageViewModel.getChatRoomInfor().enqueue(new Callback<ChatRoom>() {
            @Override
            public void onResponse(@NonNull Call<ChatRoom> call, @NonNull Response<ChatRoom> response) {
                if (response.isSuccessful()) {
                    ChatRoom chatRoom = response.body();
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
                        MessagingActivity.this.chatRoomTitle = otherUserName;
                    } else MessagingActivity.this.chatRoomTitle = chatRoom.getTitle();

                    // put the chat room image
                    if (chatRoom.getLogoPath() != null && !chatRoom.getLogoPath().isEmpty())
                        MessagingActivity.this.chatRoomImagePath = chatRoom.getLogoPath();
                    else {
                        String imagePath = null;
                        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
                        // get the first user that is different from the current user to take the image
                        for (int i = 0; i < chatRoom.getMembers().size(); i++) {
                            if (!Objects.equals(chatRoom.getMembers().get(i).get_id(), userId)) {
                                imagePath = chatRoom.getMembers().get(i).getImageProfilePath();
                                break;
                            }
                        }

                        MessagingActivity.this.chatRoomImagePath = imagePath;
                    }
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
}
package com.worthybitbuilders.squadsense.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.MessageAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityMessagingBinding;
import com.worthybitbuilders.squadsense.factory.MessageActivityViewModelFactory;
import com.worthybitbuilders.squadsense.models.ChatRoom;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.MessageActivityViewModel;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagingActivity extends AppCompatActivity {
    private ActivityMessagingBinding binding;
    private MessageActivityViewModel messageViewModel;
    private MessageAdapter messageAdapter;
    private String chatRoomImagePath;
    private String chatRoomTitle;

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

        binding.etEnterMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int s, int start, int after) {
                if (charSequence.length() > 0) {
                    binding.btnSend.setVisibility(View.VISIBLE);
                    binding.btnAttachFile.setVisibility(View.GONE);
                    binding.btnTakeCamera.setVisibility(View.GONE);
                } else {
                    binding.btnSend.setVisibility(View.GONE);
                    binding.btnAttachFile.setVisibility(View.VISIBLE);
                    binding.btnTakeCamera.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.btnSend.setOnClickListener((view -> {
            String message = String.valueOf(binding.etEnterMessage.getText());
            messageViewModel.sendNewMessage(message);
            binding.etEnterMessage.setText("");
        }));

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
}
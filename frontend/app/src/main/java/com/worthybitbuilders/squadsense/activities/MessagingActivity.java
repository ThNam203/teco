package com.worthybitbuilders.squadsense.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.InCallService;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.MessageAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityMessagingBinding;
import com.worthybitbuilders.squadsense.factory.MessageActivityViewModelFactory;
import com.worthybitbuilders.squadsense.models.ChatMessage;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.MessageActivityViewModel;

import java.util.Objects;

public class MessagingActivity extends AppCompatActivity {
    private ActivityMessagingBinding binding;
    private MessageActivityViewModel messageViewModel;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        binding = ActivityMessagingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent getIntent = getIntent();
        String chatRoomId = getIntent.getStringExtra("chatRoomId");
        String chatRoomImagePath = getIntent.getStringExtra("chatRoomImage");
        String chatRoomTitle = getIntent.getStringExtra("chatRoomTitle");

        binding.chatRoomTitle.setText(chatRoomTitle);
        Glide
            .with(this)
            .load(chatRoomImagePath)
            .placeholder(R.drawable.ic_user)
            .into(binding.chatRoomImage);


        MessageActivityViewModelFactory factory = new MessageActivityViewModelFactory(chatRoomId);
        messageViewModel = new ViewModelProvider(this, factory).get(MessageActivityViewModel.class);

        listenForNewMessage();

        Dialog loadingDialog = DialogUtils.GetLoadingDialog(this);
        loadingDialog.show();
        messageViewModel.getAllMessage(new MessageActivityViewModel.ApiCallHandler() {
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

    // the purpose is only to notify about the new message so we don't need "s"
    private void listenForNewMessage() {
        messageViewModel.getNewMessageLiveData().observe(this, s -> {
            int pos = messageViewModel.getMessageList().size() - 1;
            messageAdapter.notifyItemInserted(pos);
            binding.rvMessage.scrollToPosition(pos);
        });
    }
}
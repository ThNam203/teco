package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.gson.Gson;
import com.worthybitbuilders.squadsense.adapters.MessageAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityMessagingBinding;
import com.worthybitbuilders.squadsense.models.ChatMessage;
import com.worthybitbuilders.squadsense.utils.SocketUtil;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MessagingActivity extends AppCompatActivity {
    private ActivityMessagingBinding binding;
    private MessageAdapter messageAdapter;
    private final ArrayList<ChatMessage> mMessageList = new ArrayList<>();
    private final Socket socket = SocketUtil.getInstance();
    private final Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            MessagingActivity.this.runOnUiThread(() -> {
                ChatMessage newMessage = new Gson().fromJson((String)args[0], ChatMessage.class);
                String _id = newMessage.get_id();
                String chatRoomId = newMessage.getChatRoomId();
                String senderId = newMessage.getSenderId();
                String message = newMessage.getMessage();
                String createdAt = newMessage.getCreatedAt();

                mMessageList.add(new ChatMessage(_id, chatRoomId, message, senderId, createdAt));
                messageAdapter.notifyItemInserted(mMessageList.size());
                binding.rvMessage.scrollToPosition(mMessageList.size() - 1);
            });
        }
    };

    private void sendMessage() {
        String message = String.valueOf(binding.etEnterMessage.getText());
        ChatMessage sendMessage = new ChatMessage("1", message, "Nam");
        String jsonData = new Gson().toJson(sendMessage);
        socket.emit("newMessage", jsonData);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        socket.connect();
        socket.emit("joinMessageRoom", "1");
        socket.on("newMessage", onNewMessage);
        binding = ActivityMessagingBinding.inflate(getLayoutInflater());

        binding.rvMessage.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(this, mMessageList);
        binding.rvMessage.setAdapter(messageAdapter);

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
            ChatMessage newMessage = new ChatMessage(
                    UUID.randomUUID().toString(),
                    "1",
                    message,
                    "Nam",
                    "sdf"
            );

            sendMessage();
            binding.etEnterMessage.setText("");
        }));

        setContentView(binding.getRoot());
    }
}
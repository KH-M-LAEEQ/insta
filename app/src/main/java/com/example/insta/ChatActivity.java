package com.example.insta;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etMessage;
    private ImageButton btnSend;

    private List<Message> messageList;
    private ChatAdapter adapter;

    private String currentUserId;
    private String chatUserId;
    private String chatId;

    private DatabaseReference messagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Views
        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // User IDs
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatUserId = getIntent().getStringExtra("userId");

        // Safety check
        if (chatUserId == null) {
            finish();
            return;
        }

        // Generate SAME chatId for both users
        if (currentUserId.compareTo(chatUserId) < 0) {
            chatId = currentUserId + "_" + chatUserId;
        } else {
            chatId = chatUserId + "_" + currentUserId;
        }

        // Firebase reference (ONE chat only)
        messagesRef = FirebaseDatabase.getInstance()
                .getReference("Chats")
                .child(chatId);

        // RecyclerView setup
        messageList = new ArrayList<>();
        adapter = new ChatAdapter(this, messageList, currentUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);
        rvChat.setAdapter(adapter);

        // Send button
        btnSend.setOnClickListener(v -> sendMessage());

        // Load messages
        loadMessages();
    }

    // ================= SEND MESSAGE =================
    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        long timestamp = System.currentTimeMillis();

        Message message = new Message(
                currentUserId,
                chatUserId,
                text,
                timestamp
        );

        // Save message
        messagesRef.push().setValue(message);

        // ================= UPDATE CHAT LIST =================
        DatabaseReference chatListRef = FirebaseDatabase.getInstance()
                .getReference("ChatList");

        ChatList chatForMe = new ChatList(chatUserId, text, timestamp);
        ChatList chatForOther = new ChatList(currentUserId, text, timestamp);

        chatListRef.child(currentUserId).child(chatId).setValue(chatForMe);
        chatListRef.child(chatUserId).child(chatId).setValue(chatForOther);

        etMessage.setText("");
    }


    // ================= LOAD MESSAGES =================
    private void loadMessages() {
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Message msg = ds.getValue(Message.class);
                    if (msg != null) {
                        messageList.add(msg);
                    }
                }

                adapter.notifyDataSetChanged();

                if (!messageList.isEmpty()) {
                    rvChat.scrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}

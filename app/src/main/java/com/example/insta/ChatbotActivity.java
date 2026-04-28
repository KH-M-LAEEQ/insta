package com.example.insta;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendBtn;

    private List<ChatMessage> messages;
    private AIAdapter adapter;

    private final OkHttpClient client = new OkHttpClient();

    // 🔴 PUT YOUR NEW API KEY HERE
    private static final String OPENAI_API_KEY = "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";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendBtn = findViewById(R.id.sendBtn);

        messages = new ArrayList<>();
        adapter = new AIAdapter(this, messages);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(adapter);

        sendBtn.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if (!msg.isEmpty()) {
                addMessage(msg, true);
                messageInput.setText("");
                sendBtn.setEnabled(false); // prevent spam
                sendToAI(msg);
            }
        });
    }

    private void addMessage(String text, boolean isUser) {
        messages.add(new ChatMessage(text, isUser));
        adapter.notifyItemInserted(messages.size() - 1);
        chatRecyclerView.scrollToPosition(messages.size() - 1);
    }

    private void sendToAI(String userMessage) {

        try {
            // ---------- REQUEST BODY ----------
            JSONObject bodyJson = new JSONObject();
            bodyJson.put("model", "gpt-4o-mini");

            JSONArray messagesArray = new JSONArray();

            JSONObject userObj = new JSONObject();
            userObj.put("role", "user");
            userObj.put("content", userMessage);

            messagesArray.put(userObj);
            bodyJson.put("messages", messagesArray);

            RequestBody body = RequestBody.create(
                    bodyJson.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        addMessage("AI: Network error", false);
                        sendBtn.setEnabled(true);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.code() == 429) {
                        runOnUiThread(() -> {
                            addMessage("AI: Rate limit / quota exceeded", false);
                            sendBtn.setEnabled(true);
                        });
                        return;
                    }

                    if (!response.isSuccessful()) {
                        runOnUiThread(() -> {
                            addMessage("AI Error: " + response.code(), false);
                            sendBtn.setEnabled(true);
                        });
                        return;
                    }

                    String res = response.body().string();

                    try {
                        JSONObject json = new JSONObject(res);
                        String reply = json
                                .getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        runOnUiThread(() -> {
                            addMessage(reply.trim(), false);
                            sendBtn.setEnabled(true);
                        });

                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            addMessage("AI: Response parse error", false);
                            sendBtn.setEnabled(true);
                        });
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            sendBtn.setEnabled(true);
        }
    }
}

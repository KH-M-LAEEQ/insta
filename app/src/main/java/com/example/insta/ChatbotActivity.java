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

import okhttp3.*;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendBtn;

    private List<ChatMessage> messages;
    private AIAdapter adapter;

    private final OkHttpClient client = new OkHttpClient();

    // TEMP ONLY (we will remove later)
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
            JSONObject user = new JSONObject();
            user.put("role", "user");
            user.put("content", userMessage);

            JSONArray input = new JSONArray();
            input.put(user);

            JSONObject bodyJson = new JSONObject();
            bodyJson.put("model", "gpt-4.1-mini");
            bodyJson.put("input", input);

            RequestBody body = RequestBody.create(
                    bodyJson.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/responses")
                    .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            addMessage("AI: Network error", false));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.code() == 429) {
                        runOnUiThread(() ->
                                addMessage("AI: Quota / rate limit exceeded", false));
                        return;
                    }

                    if (!response.isSuccessful()) {
                        runOnUiThread(() ->
                                addMessage("AI Error: " + response.code(), false));
                        return;
                    }

                    String res = response.body().string();

                    try {
                        JSONObject json = new JSONObject(res);
                        String reply = json
                                .getJSONArray("output")
                                .getJSONObject(0)
                                .getJSONArray("content")
                                .getJSONObject(0)
                                .getString("text");

                        runOnUiThread(() ->
                                addMessage(reply.trim(), false));

                    } catch (Exception e) {
                        runOnUiThread(() ->
                                addMessage("AI: Response parse error", false));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.example.insta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView rvComments;
    private CommentAdapter adapter;
    private List<Comment> commentList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String postId;
    private FloatingActionButton fabAddComment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        rvComments = findViewById(R.id.rvComments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        postId = getIntent().getStringExtra("postId");

        adapter = new CommentAdapter(this, commentList, postId);
        rvComments.setAdapter(adapter);

        fabAddComment = findViewById(R.id.fabAddComment);
        fabAddComment.setOnClickListener(v -> openAddCommentDialog());

        loadComments();
    }

    private void openAddCommentDialog() {
        if (postId == null) return;

        new AddCommentDialog(this, comment -> {

            // 🔥 Firestore auto ID
            DocumentReference ref = db.collection("posts")
                    .document(postId)
                    .collection("comments")
                    .document();

            comment.setCommentId(ref.getId());

            if (comment.getTimestamp() == null) {
                comment.setTimestamp(new Date());
            }

            ref.set(comment);

        }, postId).show();
    }

    private void loadComments() {
        if (postId == null) return;

        db.collection("posts")
                .document(postId)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    commentList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Comment comment = doc.toObject(Comment.class);
                        commentList.add(comment);
                    }

                    adapter.notifyDataSetChanged();

                    if (!commentList.isEmpty()) {
                        rvComments.scrollToPosition(commentList.size() - 1);
                    }
                });
    }

    public static void open(Context context, String postId) {
        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtra("postId", postId);
        context.startActivity(intent);
    }
}

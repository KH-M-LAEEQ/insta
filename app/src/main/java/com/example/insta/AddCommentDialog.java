package com.example.insta;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

public class AddCommentDialog extends Dialog {

    public interface CommentListener {
        void onCommentAdded(Comment comment);
    }

    public AddCommentDialog(Context context, CommentListener listener, String postId) {
        super(context);
        setContentView(R.layout.dialog_add_comment);

        EditText etComment = findViewById(R.id.etComment);
        Button btnPost = findViewById(R.id.btnPost);

        btnPost.setOnClickListener(v -> {
            String text = etComment.getText().toString().trim();

            if (!text.isEmpty()) {
                // Generate a unique comment ID
                String commentId = "cmt_" + System.currentTimeMillis();

                // Create Comment WITHOUT timestamp; Firestore will auto-set it
                Comment comment = new Comment(commentId, "current_user", text);

                // Notify listener
                listener.onCommentAdded(comment);

                // Close dialog
                dismiss();
            }
        });
    }
}

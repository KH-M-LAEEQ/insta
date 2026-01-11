package com.example.insta;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PeopleActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private UserAdapter adapter;
    private List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        rvUsers = findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(this, users);
        rvUsers.setAdapter(adapter);

        loadDummyUsers();
    }

    private void loadDummyUsers() {
        users.clear();

        users.add(new User("1", "Alice", "https://images.pexels.com/photos/415829/pexels-photo-415829.jpeg"));
        users.add(new User("2", "Bob", "https://images.pexels.com/photos/2379005/pexels-photo-2379005.jpeg"));
        users.add(new User("3", "Charlie", "https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg"));
        users.add(new User("4", "David", "https://images.pexels.com/photos/91227/pexels-photo-91227.jpeg"));
        users.add(new User("5", "Eva", "https://images.pexels.com/photos/428340/pexels-photo-428340.jpeg"));
        users.add(new User("6", "Frank", "https://images.pexels.com/photos/2379004/pexels-photo-2379004.jpeg"));
        users.add(new User("7", "Grace", "https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg"));
        users.add(new User("8", "Hannah", "https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg"));
        users.add(new User("9", "Ian", "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg"));
        users.add(new User("10", "Julia", "https://images.pexels.com/photos/301733/pexels-photo-301733.jpeg"));
        users.add(new User("11", "Kevin", "https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500")); // slightly different version
        users.add(new User("12", "Luna", "https://images.pexels.com/photos/247322/pexels-photo-247322.jpeg"));

        adapter.notifyDataSetChanged();
    }

}

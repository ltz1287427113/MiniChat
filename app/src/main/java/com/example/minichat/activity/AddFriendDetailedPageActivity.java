package com.example.minichat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.minichat.R;
import com.example.minichat.data.model.response.FriendDetailResponse;
import com.example.minichat.databinding.ActivityAddFriendDetailedPageBinding;
import com.example.minichat.databinding.ActivityFriendProfileBinding;
import com.example.minichat.viewmodel.FriendProfileViewModel;
import com.example.minichat.viewmodel.ScanViewModel;

public class AddFriendDetailedPageActivity extends AppCompatActivity {
    private ActivityAddFriendDetailedPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFriendDetailedPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.inflateMenu(com.example.minichat.R.menu.chat_detail_menu);
    }
}
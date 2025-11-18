package com.example.minichat.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minichat.databinding.ActivityEditProfileBinding; // [新导入]

/**
 * [注释]
 * 这是一个“可复用”的编辑页面。
 * 它从 Intent 中获取“标题”和“当前值”。
 */
public class EditProfileActivity extends AppCompatActivity {

    // 1. ViewBinding
    private ActivityEditProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2. 加载布局
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 3. [核心] 从 Intent 中获取数据
        String title = getIntent().getStringExtra("EXTRA_TITLE");
        String value = getIntent().getStringExtra("EXTRA_VALUE");

        // 4. 设置 Toolbar
        binding.toolbar.setTitle(title); // (例如 "修改名字")
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // 5. 设置 EditText 的初始值
        binding.etEditValue.setText(value);
        binding.etEditValue.setSelection(value.length()); // (将光标移到末尾)

        // 6. 设置“保存”按钮的点击事件
        binding.btnSave.setOnClickListener(v -> {
            String newValue = binding.etEditValue.getText().toString();

            // TODO: 在这里，你应该将 newValue 保存到数据库或发送给后端

            Toast.makeText(this, "已保存: " + newValue, Toast.LENGTH_SHORT).show();

            // (在实际项目中，我们还会用 setResult() 把新值传回 ProfileActivity)

            finish(); // 关闭当前页面
        });
    }
}
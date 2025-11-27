package com.example.minichat.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.minichat.R;
import com.example.minichat.data.model.response.FriendDTO;
import com.example.minichat.data.model.response.FriendGroupDTO;
import com.example.minichat.data.model.response.FriendListGroupedResponse;
import com.example.minichat.data.repository.AuthRepository;
import com.example.minichat.data.repository.FriendRepository;
import com.example.minichat.model.ContactItem;
import com.example.minichat.model.FunctionItem;
import com.example.minichat.model.HeaderItem;

import java.util.ArrayList;
import java.util.List;

public class ContactsViewModel extends AndroidViewModel {

    private FriendRepository repository;
    // 我们直接把转换好的、Adapter 能用的列表暴露出去
    private MutableLiveData<List<Object>> contactList = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();

    public ContactsViewModel(@NonNull Application application) {
        super(application);
        repository = new FriendRepository(application);
    }

    public LiveData<List<Object>> getContactList() { return contactList; }
    public LiveData<String> getErrorMsg() { return errorMsg; }

    public void loadContacts() {
        MutableLiveData<AuthRepository.Result<FriendListGroupedResponse>> rawResult = new MutableLiveData<>();

        // 1. 观察 Repository 的原始返回
        rawResult.observeForever(result -> {
            if (result.error != null) {
                errorMsg.postValue(result.error.getMessage());
            } else if (result.data != null) {
                // 2. [核心] 数据转换：从 "分组结构" -> "扁平列表"
                List<Object> flatList = transformData(result.data);
                contactList.postValue(flatList);
            }
        });

        // 3. 发起请求
        repository.getFriendList(rawResult);
    }

    /**
     * 将后端的分组数据，转换为 Adapter 需要的混合列表
     */
    private List<Object> transformData(FriendListGroupedResponse response) {
        List<Object> list = new ArrayList<>();

        // 如果 response 为空，则只返回功能区，避免 NullPointerException
        if (response == null) {
            list.add(new FunctionItem("新的朋友", R.drawable.ic_addfriend));
            list.add(new FunctionItem("群聊", R.drawable.ic_group_chat));
            return list;
        }

        

        // 2. 遍历分组
        if (response.getGroups() != null && !response.getGroups().isEmpty()) {
            Log.d("ContactsViewModel", "Groups found: " + response.getGroups().size());
            for (FriendGroupDTO group : response.getGroups()) {
                // 添加字母标题
                list.add(new HeaderItem(group.getInitial()));

                // 添加联系人
                if (group.getFriends() != null && !group.getFriends().isEmpty()) {
                    Log.d("ContactsViewModel", "Friends in group " + group.getInitial() + ": " + group.getFriends().size());
                    for (FriendDTO friend : group.getFriends()) {
                        // [核心修改] 使用新的构造函数
                        list.add(new ContactItem(
                                friend.getFriendUsername(), // username
                                friend.getFriendRemark(),   // displayName
                                friend.getFriendAvatarUrl() // avatarUrl
                        ));
                    }
                } else {
                    Log.d("ContactsViewModel", "No friends in group " + group.getInitial());
                }
            }
        } else {
            Log.d("ContactsViewModel", "No groups found in response.");
        }

        // [新] 无论是否有好友，都添加功能区
        list.add(0, new FunctionItem("新的朋友", R.drawable.ic_addfriend));
        list.add(1, new FunctionItem("群聊", R.drawable.ic_group_chat));

        return list;
    }
}
package com.example.minichat.viewmodel;

import android.app.Application;
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

        // 1. 先添加顶部的“功能区” (写死)
        list.add(new FunctionItem("新的朋友", R.drawable.ic_addfriend));
        list.add(new FunctionItem("群聊", R.drawable.ic_group_chat));

        // 2. 遍历后端返回的分组
        if (response.getGroups() != null) {
            for (FriendGroupDTO group : response.getGroups()) {
                // a. 添加字母标题 (HeaderItem)
                list.add(new HeaderItem(group.getInitial()));

                // b. 添加该组下的所有联系人 (ContactItem)
                if (group.getFriends() != null) {
                    for (FriendDTO friend : group.getFriends()) {
                        list.add(new ContactItem(
                                friend.getFriendUsername(), // id
                                friend.getFriendRemark() != null ? friend.getFriendRemark() : friend.getFriendUsername()    // name
                                // friend.getFriendAvatarUrl() // avatar (未来支持)
                        ));
                    }
                }
            }
        }
        return list;
    }
}
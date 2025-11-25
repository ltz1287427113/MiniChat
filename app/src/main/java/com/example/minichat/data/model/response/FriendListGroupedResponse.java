package com.example.minichat.data.model.response;

import com.example.minichat.data.model.response.FriendGroupDTO;

import java.util.List;

public class FriendListGroupedResponse {
    private int totalCount;
    private int groupCount;
    private List<FriendGroupDTO> groups;
    private int groupTotalCount;

    public int getTotalCount() {
        return totalCount;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public int getGroupTotalCount() {
        return groupTotalCount;
    }

    public List<FriendGroupDTO> getGroups() { return groups; }
}
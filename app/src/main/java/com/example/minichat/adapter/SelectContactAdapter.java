package com.example.minichat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minichat.R;
import com.example.minichat.model.ContactItem;
import com.example.minichat.model.HeaderItem;
import com.example.minichat.utils.UserDisplayUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_CONTACT = 2;

    private List<Object> items = new ArrayList<>();
    private Set<String> selectedUsernames = new HashSet<>();
    private OnSelectionChangedListener selectionListener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int count);
    }

    public SelectContactAdapter(OnSelectionChangedListener listener) {
        this.selectionListener = listener;
    }

    public void updateList(List<Object> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public Set<String> getSelectedUsernames() {
        return selectedUsernames;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof HeaderItem) {
            return VIEW_TYPE_HEADER;
        } else if (items.get(position) instanceof ContactItem) {
            return VIEW_TYPE_CONTACT;
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_contact_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_contact_select, parent, false);
            return new ContactViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((HeaderItem) item);
        } else if (holder instanceof ContactViewHolder) {
            ((ContactViewHolder) holder).bind((ContactItem) item);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvLetter;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLetter = itemView.findViewById(R.id.tv_header_letter);
        }

        void bind(HeaderItem item) {
            tvLetter.setText(item.getLetter());
        }
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;
        CheckBox cbSelect;

        ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_contact_avatar);
            tvName = itemView.findViewById(R.id.tv_contact_name);
            cbSelect = itemView.findViewById(R.id.cb_select);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Object itemObj = items.get(position);
                    if (itemObj instanceof ContactItem) {
                        ContactItem item = (ContactItem) itemObj;
                        toggleSelection(item.getUsername());
                        notifyItemChanged(position);
                    }
                }
            });
        }

        void bind(ContactItem item) {
            tvName.setText(item.getDisplayName());
            UserDisplayUtils.loadAvatar(itemView.getContext(), item.getAvatarUrl(), ivAvatar);
            cbSelect.setChecked(selectedUsernames.contains(item.getUsername()));
        }
    }

    private void toggleSelection(String username) {
        if (selectedUsernames.contains(username)) {
            selectedUsernames.remove(username);
        } else {
            selectedUsernames.add(username);
        }
        if (selectionListener != null) {
            selectionListener.onSelectionChanged(selectedUsernames.size());
        }
    }
}

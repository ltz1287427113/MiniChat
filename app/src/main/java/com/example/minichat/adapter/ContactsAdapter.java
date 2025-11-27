package com.example.minichat.adapter; // (确保这是你的包名)

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minichat.R;
// [新导入]
import com.example.minichat.model.ContactItem;
import com.example.minichat.model.FunctionItem;
import com.example.minichat.model.HeaderItem;

import java.util.List;

/**
 * [注释]
 * 这是通讯录的适配器（复杂版）。
 * 它使用 "List<Object>" 来存储 *混合* 类型的数据
 * (FunctionItem, HeaderItem, ContactItem)。
 */
public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Object item);
    }

    private static OnItemClickListener listener;

    // [注释]
    // 我们用常量来定义视图类型
    private static final int VIEW_TYPE_FUNCTION = 1;
    private static final int VIEW_TYPE_HEADER = 2;
    private static final int VIEW_TYPE_CONTACT = 3;

    // [注释]
    // 这是一个可以容纳 *任何* 对象的列表
    private List<Object> items;

    /**
     * 构造函数
     *
     * @param items 混合了所有类型的列表
     */
    public ContactsAdapter(List<Object> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    /**
     * [注释]
     * [核心]
     * 此方法告诉 RecyclerView 该位置使用哪种布局。
     */
    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof FunctionItem) {
            return VIEW_TYPE_FUNCTION;
        } else if (items.get(position) instanceof HeaderItem) {
            return VIEW_TYPE_HEADER;
        } else if (items.get(position) instanceof ContactItem) {
            return VIEW_TYPE_CONTACT;
        }
        return super.getItemViewType(position);
    }

    /**
     * [注释]
     * [核心]
     * 此方法根据 viewType 加载正确的 XML 布局。
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_FUNCTION:
                // [注释] 加载 item_contact_function.xml
                View funcView = inflater.inflate(R.layout.item_contact_function, parent, false);

                return new FunctionViewHolder(funcView, listener);
            case VIEW_TYPE_HEADER:
                // [注释] 加载我们新创建的 item_contact_header.xml
                View headerView = inflater.inflate(R.layout.item_contact_header, parent, false);
                return new HeaderViewHolder(headerView);
            case VIEW_TYPE_CONTACT:
                // [注释] 加载 item_contact_person.xml
                View contactView = inflater.inflate(R.layout.item_contact_person, parent, false);
                return new ContactViewHolder(contactView, listener);
        }
        return null; // 不应该发生
    }

    /**
     * [注释]
     * [核心]
     * 此方法将数据绑定到正确的 ViewHolder。
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_FUNCTION:
                ((FunctionViewHolder) holder).bind((FunctionItem) items.get(position));
                break;
            case VIEW_TYPE_HEADER:
                ((HeaderViewHolder) holder).bind((HeaderItem) items.get(position));
                break;
            case VIEW_TYPE_CONTACT:
                ((ContactViewHolder) holder).bind((ContactItem) items.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    // --- ViewHolder 1: 用于“功能” (item_contact_function.xml) ---
    static class FunctionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        private OnItemClickListener listener;

        public FunctionViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            ivIcon = itemView.findViewById(R.id.iv_function_icon);
            tvName = itemView.findViewById(R.id.tv_function_name);
        }

        void bind(FunctionItem item) {
            tvName.setText(item.getName());
            Log.d("ContactsAdapter", "FunctionViewHolder bind called for: " + item.getName());
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    // --- ViewHolder 2: 用于“字母” (item_contact_header.xml) ---
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvLetter;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLetter = itemView.findViewById(R.id.tv_header_letter);
        }

        void bind(HeaderItem item) {
            tvLetter.setText(item.getLetter());
        }
    }

    // --- ViewHolder 3: 用于“联系人” (item_contact_person.xml) ---
    // [注释] (这与你已有的 ContactViewHolder 相同)
    static class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;

        public ContactViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_contact_avatar);
            tvName = itemView.findViewById(R.id.tv_contact_name);
        }

        void bind(ContactItem item) {
            // [修改] 使用 getDisplayName()
            tvName.setText(item.getDisplayName());

            // [新] 加载头像
            // 如果你有引入 Glide，可以这样写：
            // Glide.with(itemView).load(item.getAvatarUrl()).into(ivAvatar);

            // 暂时用默认头像
            ivAvatar.setImageResource(R.mipmap.ic_launcher_round);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    // [新] 更新列表数据的方法
    public void updateList(List<Object> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }
}
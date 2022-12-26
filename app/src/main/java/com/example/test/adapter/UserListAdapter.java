package com.example.test.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.listener.OnItemClickListener;
import com.example.test.vo.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private List<User> mData;
    private final Context mContext;
    private final OnItemClickedListener mListener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.TAIWAN);

    public UserListAdapter(Context context, OnItemClickedListener listener) {
        this.mData = new ArrayList<>();
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        if (mListener != null) {
            viewHolder.deleteBtn.setOnClickListener(v -> mListener.onDeleteBtnClicked((User) v.getTag()));
            viewHolder.editBtn.setOnClickListener(v -> mListener.onEditBtnClicked((User) v.getTag()));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        User user = mData.get(position);

        viewHolder.userIdTv.setText(TextUtils.concat("使用者Id：", user.userId));
        viewHolder.userNameTv.setText(TextUtils.concat("使用者姓名：", user.userName));
        viewHolder.ageTv.setText(TextUtils.concat("年齡：", String.valueOf(user.age)));
        viewHolder.sexTv.setText(TextUtils.concat("性別：", user.sex));
        viewHolder.addressTv.setText(TextUtils.concat("地址：", user.address));
        viewHolder.updateTimeTv.setText(TextUtils.concat("更新時間：", sdf.format(user.updateTime)));
        viewHolder.createTimeTv.setText(TextUtils.concat("建立時間：", sdf.format(user.createTime)));

        viewHolder.deleteBtn.setTag(user);
        viewHolder.editBtn.setTag(user);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<User> list) {
        if (list == null)
            mData = new ArrayList<>();
        else
            mData = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userIdTv;
        TextView userNameTv;
        TextView ageTv;
        TextView sexTv;
        TextView addressTv;
        TextView updateTimeTv;
        TextView createTimeTv;
        ImageButton deleteBtn;
        ImageButton editBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdTv = itemView.findViewById(R.id.userIdTv);
            userNameTv = itemView.findViewById(R.id.userNameTv);
            ageTv = itemView.findViewById(R.id.userAgeTv);
            sexTv = itemView.findViewById(R.id.userSexTv);
            addressTv = itemView.findViewById(R.id.userAddressTv);
            updateTimeTv = itemView.findViewById(R.id.updateTimeTv);
            createTimeTv = itemView.findViewById(R.id.createTimeTv);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            editBtn = itemView.findViewById(R.id.editBtn);
        }
    }

    public interface OnItemClickedListener {
        void onDeleteBtnClicked(User user);

        void onEditBtnClicked(User user);
    }
}

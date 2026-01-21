package com.example.lenmiriotmessage.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lenmiriotmessage.GeneralActivity;
import com.example.lenmiriotmessage.Models.UserModel;
import com.example.lenmiriotmessage.R;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {
    private final List<UserModel> mUserList;
    private final Context mContext;

    public UserListAdapter(List<UserModel> mUserList, Context mContext) {
        this.mUserList = mUserList;
        this.mContext = mContext;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public UserListAdapter.UserListViewHolder onCreateViewHolder
            (@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.user_list_item, null);

        return new UserListViewHolder(v); // view holder for header items
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        final UserModel model = mUserList.get(position);

        holder.username.setText(model.getusername());
        holder.userlvl.setText(model.getuserLVL());
        if (model.getisOnline())
            holder.isonline.setImageResource(R.drawable.ic_isonline);

        holder.ibCall.setOnClickListener(v -> {
            if (mContext instanceof GeneralActivity) {
                ((GeneralActivity)mContext).p2pCall();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public static class UserListViewHolder extends RecyclerView.ViewHolder {
        public View layout;
        public TextView username, userlvl;
        public ImageView isonline;
        public ImageButton ibCall;

        public UserListViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.tv_username_list);
            userlvl = itemView.findViewById(R.id.tv_userlvl_list);
            isonline = itemView.findViewById(R.id.iv_isonline_list);

            ibCall = itemView.findViewById(R.id.button_voicecall);
        }
    }
}

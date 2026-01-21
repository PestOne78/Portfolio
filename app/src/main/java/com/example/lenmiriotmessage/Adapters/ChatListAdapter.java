package com.example.lenmiriotmessage.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lenmiriotmessage.Models.ChatModel;
import com.example.lenmiriotmessage.R;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.RoomsViewHolder> {

    private final List<ChatModel> Chatlist;
    private final Context mContext;
    private final OnItemClickListener listener;

    public ChatListAdapter(List<ChatModel> roomlist
            , Context mContext, OnItemClickListener listener) {
        this.Chatlist = roomlist;
        this.mContext = mContext;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.chat_list_item, null);
        return new RoomsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomsViewHolder holder, int position) {
        ChatModel model = Chatlist.get(position);

        holder.textVMnumber.setText(model.getMnumber());
        holder.textVMdesc.setText(model.getMdesc());
        if (!model.getIsOnline()) holder.statusIMG.setImageResource(R.drawable.ic_isofline);
        holder.bind(Chatlist.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return Chatlist.size();
    }

    public interface OnItemClickListener {
        void onItemClick(ChatModel items);
    }

    public static class RoomsViewHolder extends RecyclerView.ViewHolder {


        public View layout;
        public TextView textVMnumber, textVMdesc;
        public ImageView statusIMG;

        public RoomsViewHolder(View itemView) {
            super(itemView);

            layout = itemView;

            statusIMG = itemView.findViewById(R.id.status_is);
            textVMnumber = itemView.findViewById(R.id.tv_chat_name);
            textVMdesc = itemView.findViewById(R.id.tv_chat_desc);
        }

        public void bind(final ChatModel item, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }

}

package com.example.lenmiriotmessage.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lenmiriotmessage.Models.MessageModel;
import com.example.lenmiriotmessage.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int ITEM_TYPE_SENT = 0;
    private static final int ITEM_TYPE_RECEIVED = 1;

    private final List<MessageModel> mMessageList;
    private final Context mContext;

    public MessageAdapter(List<MessageModel> mMessageList, Context context) {
        this.mMessageList = mMessageList;
        this.mContext = context;
    }


    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        if (viewType == ITEM_TYPE_SENT) {
            v = LayoutInflater.from(mContext)
                    .inflate(R.layout.using_message_sender, null);
        } else if (viewType == ITEM_TYPE_RECEIVED) {
            v = LayoutInflater.from(mContext)
                    .inflate(R.layout.using_message_receiver, null);
        }
        return new MessageViewHolder(v); // view holder for header items
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        final MessageModel model = mMessageList.get(position);

        holder.MessageTime.setText(model.getTimeMess());
        holder.MessageName.setText(model.getSname());
        holder.MessageText.setText(model.getTextMess());

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessageList.get(position).getSid()
                .equals(Objects.requireNonNull
                                (FirebaseAuth.getInstance().getCurrentUser())
                        .getUid())) {
            return ITEM_TYPE_SENT;
        } else {
            return ITEM_TYPE_RECEIVED;
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public View layout;
        public TextView MessageText, MessageName, MessageTime;

        public MessageViewHolder(View itemView) {
            super(itemView);
            layout = itemView;

            MessageText = itemView.findViewById(R.id.txtMessageBody);
            MessageTime = itemView.findViewById(R.id.txtMessageTime);
            MessageName = itemView.findViewById(R.id.txtMessageName);
        }
    }
}

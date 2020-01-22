package com.example.pestone.conference.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pestone.conference.Models.MessageModel;
import com.example.pestone.conference.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private static final int ITEM_TYPE_SENT = 0;
    private static final int ITEM_TYPE_RECEIVED = 1;

    private List<MessageModel> mMessageList;
    private Context mContext;

    public MessageAdapter(List<MessageModel> mMessageList, Context context) {
        this.mMessageList = mMessageList;
        this.mContext = context;
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        if (viewType == ITEM_TYPE_SENT) {
            v = LayoutInflater.from(mContext).inflate(R.layout.using_message_sender, null);
        } else if (viewType == ITEM_TYPE_RECEIVED) {
            v = LayoutInflater.from(mContext).inflate(R.layout.using_message_receiver, null);
        }
        return new MessageViewHolder(v); // view holder for header items
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        final MessageModel model = mMessageList.get(position);

        holder.MessageTime.setText(model.getMessageTime());
        if(getItemViewType(position) == ITEM_TYPE_SENT){
            holder.MessageName.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }else if(getItemViewType(position) == ITEM_TYPE_RECEIVED){
            holder.MessageName.setText(model.getSenderName());
        }

        holder.MessageText.setText(model.getTextOfMessage());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessageList.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return ITEM_TYPE_SENT;
        } else {
            return ITEM_TYPE_RECEIVED;
        }
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public View layout;
        public TextView MessageText, MessageName, MessageTime;


        public MessageViewHolder(View itemView) {
            super(itemView);

            layout = itemView;
            MessageText = (TextView) itemView.findViewById(R.id.txtMessageBody);
            MessageTime = (TextView) itemView.findViewById(R.id.txtMessageTime);
            MessageName = (TextView) itemView.findViewById(R.id.txtMessageName);

        }
    }
}

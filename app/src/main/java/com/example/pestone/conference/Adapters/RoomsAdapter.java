package com.example.pestone.conference.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pestone.conference.R;
import com.example.pestone.conference.Models.RoomModel;

import java.util.List;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomsViewHolder> {

    public interface OnItemClickListener{
        void onItemClick(RoomModel items);
    }

    private List<RoomModel> roomlist;
    private Context mContext;
    private final OnItemClickListener listener;


    public RoomsAdapter(List<RoomModel> roomlist, Context mContext, OnItemClickListener listener) {
        this.roomlist = roomlist;
        this.mContext = mContext;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(mContext).inflate(R.layout.room_list_item, null);
        return new RoomsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomsViewHolder holder, int position) {
        RoomModel model = roomlist.get(position);

        holder.txtRoomName.setText(model.getRoomName());
        holder.txtRoomDescription.setText(model.getRoomDescription());
        holder.txtRoomTime.setText(model.getRoomDate());

        holder.bind(roomlist.get(position),listener);
    }

    @Override
    public int getItemCount() {
        return roomlist.size();
    }

    public class RoomsViewHolder extends  RecyclerView.ViewHolder{


        public View layout;
        public TextView txtRoomName,txtRoomDescription, txtRoomTime;

        public RoomsViewHolder (View itemView){
            super(itemView);

            layout = itemView;

            txtRoomTime = (TextView) itemView.findViewById(R.id.txtRoomTime);
            txtRoomName = (TextView) itemView.findViewById(R.id.txtRoomName);
            txtRoomDescription = (TextView) itemView.findViewById(R.id.txtRoomDescription);
        }

        public void bind(final RoomModel item, final OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

}

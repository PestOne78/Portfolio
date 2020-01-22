package com.example.pestone.conference.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pestone.conference.Models.UserProfileModel;
import com.example.pestone.conference.R;

import java.util.List;

public class UserSpinnerAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private List<UserProfileModel> ListItems;
    private LayoutInflater mInflater;
    private int mResource;

    public UserSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List ListItems) {
        super(context, resource, 0, ListItems);

        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mResource = resource;
        this.ListItems = ListItems;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = mInflater.inflate(mResource, parent, false);

        TextView txtUserSpin, txtEmailSpin;
        CardView cardViewSpin;

        txtEmailSpin = (TextView) view.findViewById(R.id.txtUserEmailSpinner);
        txtUserSpin = (TextView) view.findViewById(R.id.txtUserNameSpinner);
        cardViewSpin = (CardView) view.findViewById(R.id.card_view_spinner_item);

        UserProfileModel model = ListItems.get(position);

        txtUserSpin.setText(model.getUsername());
        txtEmailSpin.setText(model.getEmail());
        if(position %2 == 0){
            cardViewSpin.setCardBackgroundColor(Color.WHITE);
        }else {
            cardViewSpin.setCardBackgroundColor(Color.LTGRAY);
        }


        return view;
    }
}

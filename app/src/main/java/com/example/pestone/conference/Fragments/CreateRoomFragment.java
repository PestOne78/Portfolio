package com.example.pestone.conference.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pestone.conference.Adapters.UserSpinnerAdapter;
import com.example.pestone.conference.GeneralActivity;
import com.example.pestone.conference.Models.RoomModel;
import com.example.pestone.conference.Models.UserProfileModel;
import com.example.pestone.conference.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateRoomFragment extends Fragment {

    private RoomsFragment roomsFragment;
    private FragmentTransaction fTransaction;

    private Button btnCreateRoomCreate, btnCancelCreate;
    private EditText inputNameRoom, inputDescriptionRoom;
    private Spinner SpinUserName;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.US);
    private Calendar calendar = Calendar.getInstance();

    private UserSpinnerAdapter adapter;

    public final String LOG_TAG = "TAG";

    public static String API_KEY;
    private String roomName, roomDescription, roomDate, senderUID,
            reciverUID, senderEmail, reciverEmail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference("Rooms");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference("users");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_createroom, null);

        btnCancelCreate = (Button) v.findViewById(R.id.btn_cancel);
        btnCreateRoomCreate = (Button) v.findViewById(R.id.btn_createroom);
        inputNameRoom = (EditText) v.findViewById(R.id.room_name);
        inputDescriptionRoom = (EditText) v.findViewById(R.id.room_description);
        SpinUserName = (Spinner) v.findViewById(R.id.spinnerSelectUsers);

        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<UserProfileModel> ListUsers = new ArrayList<UserProfileModel>();

                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    UserProfileModel model = Snapshot.getValue(UserProfileModel.class);
                    if (!model.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        ListUsers.add(model);
                    }
                }
                adapter = new UserSpinnerAdapter(getActivityNonNull(), R.layout.user_spinner_item, ListUsers);
                SpinUserName.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        SpinUserName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String UserEmailSpinnerText = ((TextView) view.findViewById(R.id.txtUserEmailSpinner)).getText().toString();

                mDatabaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                            UserProfileModel model = Snapshot.getValue(UserProfileModel.class);
                            if (model != null) {
                                if (model.getEmail().equals(UserEmailSpinnerText)
                                        & !model.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                    reciverEmail = model.getEmail();
                                    reciverUID = model.getUID();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnCreateRoomCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                roomName = inputNameRoom.getText().toString().trim();
                roomDescription = inputDescriptionRoom.getText().toString().trim();
                roomDate = dateFormat.format(calendar.getTime());

                senderUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                senderEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                if (TextUtils.isEmpty(roomName)) {
                    Toast.makeText(getActivity(), "Введите название комнаты!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(roomDescription)) {
                    Toast.makeText(getActivity(), "Введите описание комнаты!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String pushKey = mDatabase.push().getKey();
                RoomModel roomModel = new RoomModel(roomName, roomDescription, roomDate, senderUID, reciverUID,
                        senderEmail, reciverEmail, pushKey, null, null);
                writeNewRoom(roomModel);
            }
        });


        btnCancelCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomsFragment = new RoomsFragment();
                fTransaction = getActivityNonNull().getSupportFragmentManager().beginTransaction();
                fTransaction.replace(R.id.fragmentlayout, roomsFragment).commit();
                GeneralActivity.navItemIndex = 0;
                GeneralActivity generalActivity = new GeneralActivity();
                generalActivity.visiblityFAB();
            }
        });

        return v;
    }


    public void writeNewRoom(RoomModel roomModel) {

        try {
            mDatabase.child(roomModel.getPushKey()).setValue(roomModel);

            Toast.makeText(getActivity(), "Комната создана!", Toast.LENGTH_SHORT).show();

            roomsFragment = new RoomsFragment();
            fTransaction = getActivityNonNull().getSupportFragmentManager().beginTransaction();
            fTransaction.replace(R.id.fragmentlayout, roomsFragment).commit();

        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Ошибка" + ex, Toast.LENGTH_LONG).show();
        }
    }

    protected FragmentActivity getActivityNonNull() {
        if (super.getActivity() != null) {
            return super.getActivity();
        } else {
            throw new RuntimeException("GetActivity() вернул Null");
        }
    }
}

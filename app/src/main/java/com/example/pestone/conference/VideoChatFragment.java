package com.example.pestone.conference;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pestone.conference.Models.RoomModel;
import com.example.pestone.conference.Utility.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoChatFragment extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener {

    private static String API_KEY;
    private static String SESSION_ID;
    private static String TOKEN;
    private static final String LOG_TAG = GeneralActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    private int id;
    private Session mSession;
    private Publisher mPublisher;

    private Subscriber mSubscriber;

    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;
    private ImageButton mFabBack;
    private ImageButton mFabSwitchCamera;

    private DatabaseReference RoomReference;
    private DatabaseReference UserReference;

    // Connect to database/ create Token for video chat

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_video_chat);

        RoomReference = FirebaseDatabase.getInstance().getReference("Rooms");
        UserReference = FirebaseDatabase.getInstance().getReference("users");


        mFabBack = (ImageButton) findViewById(R.id.back_to_chat);
        mFabSwitchCamera = (ImageButton) findViewById(R.id.switch_camera);

        mFabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mFabSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPublisher.cycleCamera();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        addDB();
        requestPermissions();
    }

    //Permissions for Camera/Audio
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize view objects from your layout
            mPublisherViewContainer = (FrameLayout) findViewById(R.id.publisher_container);
            mSubscriberViewContainer = (FrameLayout) findViewById(R.id.subscriber_container);

            //addDB();

        } else {
            EasyPermissions.requestPermissions(this,
                    "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

    // SessionListener methods

    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG, "Session Connected");

        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        mPublisherViewContainer.addView(mPublisher.getView());
        try {
            mSession.publish(mPublisher);
        } catch (Exception ex) {
            Log.e(Constants.LOG_TAG, "Failed" + ex);
            onBackPressed();
        }
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG, "Session Disconnected");
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Received");

        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG, "Session error: " + opentokError.getMessage());
    }


    // PublisherListener methods

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamCreated");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamDestroyed");
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.e(LOG_TAG, "Publisher error: " + opentokError.getMessage());
    }

    @Override
    public void onBackPressed() {
        closeConnection();
//        onStop();
//        finish();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void addDB() {
        Query query = RoomReference.orderByChild("pushKey").equalTo(Constants.pushKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot room : dataSnapshot.getChildren()) {
                        RoomModel rooms = room.getValue(RoomModel.class);
                        if (rooms != null) {
                            if (rooms.getToken() == null) {
                                fetchSessionConnectionData(false, null, null);
                                Map<String, Object> map = new HashMap<>();
                                map.put("Token", TOKEN);
                                room.getRef().updateChildren(map);
                                map.put("Session", SESSION_ID);
                                room.getRef().updateChildren(map);
                            } else {
                                fetchSessionConnectionData(true, rooms.getSession(), rooms.getToken());
                            }
                        }
                    }
                    id=0;
                } else {
                    id = 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void fetchSessionConnectionData(final Boolean Check, final String session, final String token) {
        RequestQueue reqQueue = Volley.newRequestQueue(this);
        reqQueue.add(new JsonObjectRequest(Request.Method.GET,
                "https://conference6742.herokuapp.com/" + "/session",
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (Check.equals(false)) {
                        API_KEY = response.getString("apiKey");
                        SESSION_ID = response.getString("sessionId");
                        TOKEN = response.getString("token");
                    } else if (Check.equals(true)) {
                        SESSION_ID = session;
                        TOKEN = token;
                    }
                    mSession = new Session.Builder(VideoChatFragment.this, API_KEY, SESSION_ID).build();
                    mSession.setSessionListener(VideoChatFragment.this);
                    mSession.connect(TOKEN);

                } catch (JSONException error) {
                    Log.e(LOG_TAG, "Web Service error: " + error.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Web Service error: " + error.getMessage());
            }
        }));
    }

    public void closeConnection() {
        Query query = RoomReference.orderByChild("pushKey").equalTo(Constants.pushKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot room : dataSnapshot.getChildren()) {
                    RoomModel rooms = room.getValue(RoomModel.class);
                    if (rooms != null) {
                        if (rooms.getToken() != null) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("Token", null);
                            room.getRef().updateChildren(map);
                            map.put("Session", null);
                            room.getRef().updateChildren(map);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
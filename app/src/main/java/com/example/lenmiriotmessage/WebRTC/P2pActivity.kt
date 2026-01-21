package com.example.lenmiriotmessage.WebRTC

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lenmiriotmessage.Models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import io.getstream.video.android.compose.permission.LaunchCallPermissions
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.compose.ui.components.call.renderer.FloatingParticipantVideo
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantVideo
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.RealtimeConnection
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User

class P2pActivity : ComponentActivity() {
    val apiKey = "yxr3bxsc5y9g"
    val apiSecret = "6yyrge848wtqtq7e385j2fnde7au5az5pgjgsbjs5842beczpv4hbyfdp8fsw9v9"
    val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val callId = "hKsxYOb0QDRb"
    var username = ""
    var token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoia2hVRWZOZW5TelhHU1RUTWVGMWN2bklCeHg2MyJ9.mlsYaQlsi_wZ3I1OU_fQQqsG4l_1Kmys5S6b8glgGUk"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mDatabase : DatabaseReference = FirebaseDatabase.getInstance().getReference()
        mDatabase.addValueEventListener(getUser())

        // Create a user.
        val user = User(
            id = userId, // any string
            name = username, // name and image are used in the UI
            image = "https://bit.ly/2TIt8NR",
        )
        // Initialize StreamVideo.
        val client = StreamVideoBuilder(
            context = applicationContext,
            apiKey = apiKey,
            geo = GEO.GlobalEdgeNetwork,
            user = user,
            token = token
        ).build()

        setContent {
            // Request permissions and join a call, which type is `default` and id is `123`.
            val call = client.call(type = "default", id = callId)
            LaunchCallPermissions(
                call = call,
                onAllPermissionsGranted = {
                    // All permissions are granted so that we can join the call.
                    val result = call.join(create = true)
                    result.onError {
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            )

            VideoTheme {
                val remoteParticipants by call.state.remoteParticipants.collectAsState()
                val remoteParticipant = remoteParticipants.firstOrNull()
                val me by call.state.me.collectAsState()
                val connection by call.state.connection.collectAsState()
                var parentSize: IntSize by
                remember { mutableStateOf(IntSize(0, 0)) }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(VideoTheme.colors.baseSenary)
                        .onSizeChanged { parentSize = it }
                ) {
                    if (remoteParticipant != null) {
                        ParticipantVideo(
                            modifier = Modifier.fillMaxSize(),
                            call = call,
                            participant = remoteParticipant
                        )
                    } else {
                        if (connection != RealtimeConnection.Connected) {
                            Text(
                                text = "waiting for a remote participant...",
                                fontSize = 30.sp,
                                color = VideoTheme.colors.basePrimary
                            )
                        } else {
                            Text(
                                modifier = Modifier.padding(30.dp),
                                text = "Join call ${call.id} in your browser to see the video here",
                                fontSize = 30.sp,
                                color = VideoTheme.colors.basePrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // floating video UI for the local video participant
                    me?.let { localVideo ->
                        FloatingParticipantVideo(
                            modifier = Modifier.align(Alignment.TopEnd),
                            call = call,
                            participant = localVideo,
                            parentBounds = parentSize
                        )
                    }

                    CallContent(
                        modifier = Modifier.fillMaxSize(),
                        call = call,
                        onBackPressed = { onBackPressed() },
                    )

                }
            }
        }
    }


    private fun getUser(): ValueEventListener {
        val userListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.child("users")
                    .child(userId).getValue<UserModel>()
                username = user?.getusername().toString()
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        return userListener
    }

}


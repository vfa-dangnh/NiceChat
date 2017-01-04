package com.haidangkf.nicechat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dangnguyen on 12/28/16.
 */

public class ChatActivity extends AppCompatActivity {
    LinearLayout layout;
    EditText etMessage;
    ImageView btnSend;
    ScrollView scrollView;
    Firebase reference1, reference2;
    String url = "https://android-chat-app-e711d.firebaseio.com/messages/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = (LinearLayout) findViewById(R.id.linearLayout);
        btnSend = (ImageView) findViewById(R.id.btnSend);
        etMessage = (EditText) findViewById(R.id.etMessage);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        Firebase.setAndroidContext(this);
        reference1 = new Firebase(url + UserDetail.username + "_" + UserDetail.chatWith);
        reference2 = new Firebase(url + UserDetail.chatWith + "_" + UserDetail.username);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = etMessage.getText().toString();

                if (!messageText.isEmpty()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("message", messageText);
                    map.put("user", UserDetail.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    etMessage.setText("");
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if (userName.equals(UserDetail.username)) {
                    addMessageBox("You:\n" + message, 1);
                } else {
                    addMessageBox(UserDetail.chatWith + ":\n" + message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(ChatActivity.this);
        textView.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        textView.setLayoutParams(lp);

        if (type == 1) { // my message
            textView.setBackgroundResource(R.drawable.rounded_corner1);
        } else { // partner's message
            textView.setBackgroundResource(R.drawable.rounded_corner2);
        }

        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}
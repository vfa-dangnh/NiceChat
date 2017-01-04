package com.haidangkf.nicechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by dangnguyen on 12/28/16.
 */

public class UserActivity extends AppCompatActivity {
    public static final String TAG = "my_log";
    TextView tvInform;
    ListView listView;
    ArrayList<String> arrayList = new ArrayList<>();
    int availableUsers = 0;
    ProgressDialog pd;
    String url = "https://android-chat-app-e711d.firebaseio.com/users.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        tvInform = (TextView) findViewById(R.id.tvInform);
        listView = (ListView) findViewById(R.id.listView);

        pd = new ProgressDialog(UserActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, volleyError.toString());
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(UserActivity.this);
        rQueue.add(request);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetail.chatWith = arrayList.get(position);
                startActivity(new Intent(UserActivity.this, ChatActivity.class));
            }
        });
    }

    public void doOnSuccess(String s) {
        try {
            JSONObject obj = new JSONObject(s);
            Iterator i = obj.keys();
            String key = "";

            while (i.hasNext()) {
                key = i.next().toString();
//                Log.d(TAG, key);
                if (!key.equals(UserDetail.username)) {
                    arrayList.add(key);
                    availableUsers++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (availableUsers < 1) {
            tvInform.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvInform.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList));
        }

        pd.dismiss();
    }
}
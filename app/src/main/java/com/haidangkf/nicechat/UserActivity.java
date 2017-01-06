package com.haidangkf.nicechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableRow;
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
    EditText etSearch;
    TableRow rowSearch;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> arrayListCopy = new ArrayList<>();
    ArrayAdapter adapter;
    int availableUsers = 0;
    ProgressDialog pd;
    String url = "https://android-chat-app-e711d.firebaseio.com/users.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        tvInform = (TextView) findViewById(R.id.tvInform);
        listView = (ListView) findViewById(R.id.listView);
        etSearch = (EditText) findViewById(R.id.etSearch);
        rowSearch = (TableRow) findViewById(R.id.rowSearch);

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

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                searchFriend(s);
            }

            @Override
            public void afterTextChanged(Editable ed) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void searchFriend(CharSequence s) {
        arrayList.clear();
        for (String name : arrayListCopy) {
            if (name.startsWith("" + s)) {
                arrayList.add(name);
            }
        }
        adapter.notifyDataSetChanged();
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

            arrayListCopy.addAll(arrayList); // make a copy
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (availableUsers < 1) {
            tvInform.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            rowSearch.setVisibility(View.GONE);
        } else {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(adapter);
        }

        pd.dismiss();
    }
}
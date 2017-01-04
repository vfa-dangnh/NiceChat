package com.haidangkf.nicechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dangnguyen on 12/28/16.
 */

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "";
    EditText etUsername, etPassword, etConfirmPassword;
    Button btnRegister;
    TextView btnLogIn;
    String username, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLogIn = (TextView) findViewById(R.id.btnLogIn);

        Firebase.setAndroidContext(this);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                confirmPassword = etConfirmPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill all the blanks", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                } else if (!username.matches("[A-Za-z0-9]+")) {
                    etUsername.setError("Only alphabet or number allowed");
                } else if (username.length() < 5) {
                    etUsername.setError("At least 5 characters");
                } else if (password.length() < 5) {
                    etPassword.setError("At least 5 characters");
                } else {
                    final ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    String url = "https://android-chat-app-e711d.firebaseio.com/users.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Firebase reference = new Firebase("https://android-chat-app-e711d.firebaseio.com/users");

                            if (s.equals("null")) {
                                reference.child(username).child("password").setValue(password);
                                Toast.makeText(RegisterActivity.this, "Register successful", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(username)) {
                                        reference.child(username).child("password").setValue(password);
                                        Toast.makeText(RegisterActivity.this, "Register successful", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd.dismiss();
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.d(TAG, volleyError.toString());
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(RegisterActivity.this);
                    rQueue.add(request);
                }
            }
        });
    }
}
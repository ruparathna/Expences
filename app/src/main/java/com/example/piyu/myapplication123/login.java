package com.example.piyu.myapplication123;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class login extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static final int CONNECTION_TIMEOUT = 1000;
    public static final int READ_TIMEOUT = 15000;
    EditText name, password;
    Button login;
    String name_str, pass_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name = findViewById(R.id.et_username_login);
        password = findViewById(R.id.et_password_login);
        login = findViewById(R.id.login_btn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length() != 0) {
                    if (password.getText().length() != 0) {
                        name_str = name.getText().toString();
                        pass_str = password.getText().toString();
                        new AsyncLogin().execute(name_str, pass_str);

                    } else
                        password.setError("Enter Nic/Pasport No");
                } else
                    name.setError("Enter Room No");
            }
        });
    }

    private class AsyncLogin extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(login.this);
        HttpURLConnection conn;
        URL url = null;

        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        //comment
        protected String doInBackground(String... params) {
            try {
                url = new URL("http://foodie.sanila.tech/tera/login.php");
                // url = new URL("http://localhost/Login.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(30000);
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("user_name", params[0])
                        .appendQueryParameter("password", params[1]);

                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                e1.printStackTrace();
                return "exception";
            }

            try {
                int response_code = conn.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    return (result.toString());
                } else {
                    return ("unsuccessful");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        // fggdhjhujk
        protected void onPostExecute(String result) {
            try {
                pdLoading.dismiss();
                JSONArray jsonarray = new JSONArray(result);
                JSONObject jsonobject = jsonarray.getJSONObject(0);
                String status = jsonobject.getString("cusid");

                if (!status.equals("0")) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("userid", status);
                    editor.putString("username", name_str);
                    editor.putString("password", pass_str);
                    editor.apply();
                    editor.commit();
                    Intent intent = new Intent(login.this, Main.class);
                    startActivity(intent);
                    login.this.finish();
                } else if (status.equalsIgnoreCase("0")) {
                    Toast.makeText(login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    return;
                } else if (status.equalsIgnoreCase("0")) {
                    Toast.makeText(login.this, "OOPs! something went wrong.connection problem", Toast.LENGTH_SHORT).show();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

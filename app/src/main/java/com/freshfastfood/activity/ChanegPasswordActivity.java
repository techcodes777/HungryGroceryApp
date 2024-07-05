package com.freshfastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.freshfastfood.R;
import com.freshfastfood.model.RestResponse;
import com.freshfastfood.retrofit.APIClient;
import com.freshfastfood.retrofit.GetResult;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class ChanegPasswordActivity extends AppCompatActivity implements GetResult.MyListener {

    TextInputEditText edPassword;
    TextInputLayout edPassword1;
    TextInputEditText edConpassword;
    TextInputLayout edConpassword1;
    TextView btnSubmit;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chaneg_password);
        phoneNumber = getIntent().getStringExtra("phone");

        init();
        onClick();
    }

    private void init() {
        edPassword = findViewById(R.id.ed_password);
        edPassword1 = findViewById(R.id.ed_password1);
        edConpassword = findViewById(R.id.ed_conpassword);
        edConpassword1 = findViewById(R.id.ed_conpassword1);
        btnSubmit = findViewById(R.id.btn_submit);
    }

    public void onClick() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation())
                    setPassword();
            }
        });
    }

    public boolean validation() {

        if (edPassword.getText().toString().isEmpty()) {
            edPassword.setError(getString(R.string.epassword));
            return false;
        }
        if (edConpassword.getText().toString().isEmpty()) {
            edConpassword.setError(getString(R.string.econfirm));
            return false;
        }
        if (!edConpassword.getText().toString().equals(edPassword.getText().toString())) {
            edConpassword.setError(getString(R.string.mispassword));
            edPassword.setError(getString(R.string.mispassword));
            return false;
        }
        return true;
    }
    private void setPassword() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pin", phoneNumber);
            jsonObject.put("password", edPassword.getText().toString());
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getPinmatch((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void callback(JsonObject result, String callNo) {
        Gson gson = new Gson();
        RestResponse response = gson.fromJson(result.toString(), RestResponse.class);
        Toast.makeText(ChanegPasswordActivity.this, "" + response.getResponseMsg(), Toast.LENGTH_LONG).show();
        if (response.getResult().equals("true")) {
            Intent intent = new Intent(ChanegPasswordActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}

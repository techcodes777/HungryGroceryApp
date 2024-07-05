package com.freshfastfood.activity;

import static com.freshfastfood.utils.SessionManager.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.freshfastfood.R;
import com.freshfastfood.model.LoginUser;
import com.freshfastfood.retrofit.APIClient;
import com.freshfastfood.retrofit.GetResult;
import com.freshfastfood.utils.CustPrograssbar;
import com.freshfastfood.utils.SessionManager;
import com.freshfastfood.utils.Utiles;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class LoginActivity extends BaseActivity implements GetResult.MyListener,View.OnClickListener {

    EditText edUsername;
    EditText edPassword;
    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;
    TextView txtForgotPassword,btnLogin,btnSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(LoginActivity.this);
    }

    private void init() {
        edUsername = findViewById(R.id.ed_username);
        edPassword = findViewById(R.id.ed_password);
        btnLogin = findViewById(R.id.btn_login);
        btnSign = findViewById(R.id.btn_sign);
        txtForgotPassword = findViewById(R.id.txt_forgotpassword);

        btnLogin.setOnClickListener(this);
        btnSign.setOnClickListener(this);
        txtForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login){
            if (validation()) {
                loginUser();
            }
        } else if (view.getId() == R.id.btn_sign) {
            startActivity(new Intent(LoginActivity.this, SingActivity.class));
            finish();
        } else {
            startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
        }
    }
    private void loginUser() {
        custPrograssbar.prograssCreate(LoginActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", edUsername.getText().toString());
            jsonObject.put("password", edPassword.getText().toString());
            jsonObject.put("imei", Utiles.getIMEI(LoginActivity.this));
            JsonParser jsonParser = new JsonParser();

            Call<JsonObject> call = APIClient.getInterface().getLogin((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public boolean validation() {
        if (edUsername.getText().toString().isEmpty()) {
            edUsername.setError(getString(R.string.emobileno));
            return false;
        }
        if (edPassword.getText().toString().isEmpty()) {
            edPassword.setError(getString(R.string.epassword));
            return false;
        }
        return true;
    }
    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            Gson gson = new Gson();
            LoginUser response = gson.fromJson(result.toString(), LoginUser.class);
            Toast.makeText(LoginActivity.this, "" + response.getResponseMsg(), Toast.LENGTH_LONG).show();
            if (response.getResult().equals("true")) {
                sessionManager.setUserDetails(response.getUser());
                sessionManager.setBooleanData(login,true);
//                OneSignal.sendTag("userId", response.getUser().getId());
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            }
        } catch (Exception e) {
            Log.e("error"," --> "+e.toString());
        }
    }
}

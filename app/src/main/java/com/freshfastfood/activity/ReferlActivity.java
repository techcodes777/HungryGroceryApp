package com.freshfastfood.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.freshfastfood.R;
import com.freshfastfood.model.RestResponse;
import com.freshfastfood.model.User;
import com.freshfastfood.retrofit.APIClient;
import com.freshfastfood.retrofit.GetResult;
import com.freshfastfood.utils.CustPrograssbar;
import com.freshfastfood.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;

public class ReferlActivity extends BaseActivity implements GetResult.MyListener,View.OnClickListener {

    TextView txtT1;
    TextView txtT2;
    TextView txtT3;
    TextView txtCode;
    TextView txtShare;

    TextView txtCopy;

    User user;
    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referl);
        init();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(R.string.referearn);
        sessionManager = new SessionManager(ReferlActivity.this);
        custPrograssbar = new CustPrograssbar();
        user = sessionManager.getUserDetails();
        getData();
    }

    private void init() {
       txtT1  = findViewById(R.id.txt_t1);
       txtT2  = findViewById(R.id.txt_t2);
       txtT3  = findViewById(R.id.txt_t3);
       txtCode  = findViewById(R.id.txt_code);
       txtShare  = findViewById(R.id.txt_share);
       txtCopy  = findViewById(R.id.txt_copy);
    }

    private void getData() {
        try {
            custPrograssbar.prograssCreate(ReferlActivity.this);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("uid", user.getId());
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getDta((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                RestResponse restResponse = gson.fromJson(result.toString(), RestResponse.class);
                if (restResponse.getResult().equalsIgnoreCase("true")) {
                    txtT2.setText("Friends get " + sessionManager.getStringData(SessionManager.currncy) + restResponse.getRefercredit() + " on their first Order");
                    txtT3.setText("You get " + sessionManager.getStringData(SessionManager.currncy) + restResponse.getSignupcredit() + " on your wallet");
                    txtCode.setText("" + restResponse.getCode());

                }

            }
        } catch (Exception e) {
            Log.e("Error",""+e.toString());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txt_share){
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                String shareMessage = "Hey! Now use our app to share with your family or friends. User will get wallet amount on your 1st successful order. Enter my referral code *" + txtCode.getText().toString() + "* & Enjoy your shopping !!!" ;
                shareMessage = shareMessage + " https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName() + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                Log.e("error", Objects.requireNonNull(e.getMessage()));
            }
        }else {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(txtCode.getText().toString());
        }
    }
}
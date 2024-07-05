package com.freshfastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.freshfastfood.R;
import com.freshfastfood.model.User;
import com.freshfastfood.retrofit.APIClient;
import com.freshfastfood.retrofit.GetResult;
import com.freshfastfood.utils.CustPrograssbar;
import com.freshfastfood.utils.SessionManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class PopRatesActivity extends BaseActivity implements GetResult.MyListener,View.OnClickListener{
    EditText edFeedback;
    User user;
    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;
    LinearLayout lvlVgood;
    LinearLayout lvlGood;
    LinearLayout lvlNotgood;
    String id;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// app icon in action bar clicked; go home
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_poprates);
        init();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(PopRatesActivity.this);
        user = sessionManager.getUserDetails();
        custPrograssbar = new CustPrograssbar();
        Intent intent = getIntent();
        id = intent.getStringExtra("oid");
    }

    private void init() {
        lvlVgood = findViewById(R.id.lvl_vgood);
        lvlGood = findViewById(R.id.lvl_good);
        lvlNotgood = findViewById(R.id.lvl_notgood);
    }

    private void UserFeedBack() {
        custPrograssbar.prograssCreate(PopRatesActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg", edFeedback.getText().toString());
            jsonObject.put("rate", String.valueOf(selectRate));
            jsonObject.put("uid", user.getId());
            jsonObject.put("oid", id);
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().rates((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        custPrograssbar.closePrograssBar();
        if (result != null) {
            try {
                JSONObject object = new JSONObject(result.toString());
                Toast.makeText(PopRatesActivity.this, "" + object.getString("ResponseMsg"), Toast.LENGTH_SHORT).show();
                if (object.getString("Result").equals("true")) {
                    edFeedback.setText("");
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validation() {
        if (selectRate == 0) {
            Toast.makeText(PopRatesActivity.this, "Pleas select Rates..", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    int selectRate = 0;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_vgood){
            selectRate = 1;
            imgClick(1);
        } else if (view.getId() == R.id.img_good){
            selectRate = 2;
            imgClick(2);
        }else if (view.getId() == R.id.img_notgood){
            selectRate = 3;
            imgClick(3);
        }else {
            if (validation()) {
                UserFeedBack();
            }
        }
    }

    public void imgClick(int item) {
        switch (item) {
            case 1:
                lvlGood.setBackgroundResource(R.drawable.rounded_search);
                lvlVgood.setBackgroundResource(R.drawable.rounded_search1);
                lvlNotgood.setBackgroundResource(R.drawable.rounded_search);
                break;
            case 2:
                lvlGood.setBackgroundResource(R.drawable.rounded_search1);
                lvlVgood.setBackgroundResource(R.drawable.rounded_search);
                lvlNotgood.setBackgroundResource(R.drawable.rounded_search);
                break;
            case 3:
                lvlGood.setBackgroundResource(R.drawable.rounded_search);
                lvlVgood.setBackgroundResource(R.drawable.rounded_search);
                lvlNotgood.setBackgroundResource(R.drawable.rounded_search1);
                break;
            default:
                break;
        }
    }
}

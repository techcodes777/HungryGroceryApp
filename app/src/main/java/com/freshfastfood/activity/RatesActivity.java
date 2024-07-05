package com.freshfastfood.activity;

import static com.freshfastfood.utils.Utiles.isrates;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

public class RatesActivity extends BaseActivity implements GetResult.MyListener,View.OnClickListener{

    EditText edFeedback;
    User user;
    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;
    LinearLayout lvlVgood;
    LinearLayout lvlGood;
    LinearLayout lvlNotgood;
    String id;

    ImageView imgVGood,imgGood,imgNotGood;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);
        init();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessionManager = new SessionManager(RatesActivity.this);
        user = sessionManager.getUserDetails();
        custPrograssbar = new CustPrograssbar();
        Intent intent = getIntent();
        id = intent.getStringExtra("oid");
    }

    private void init() {
        edFeedback = findViewById(R.id.ed_feedback);
        lvlVgood = findViewById(R.id.lvl_vgood);
        lvlGood = findViewById(R.id.lvl_good);
        lvlNotgood = findViewById(R.id.lvl_notgood);
        imgVGood = findViewById(R.id.img_notgood);
        imgGood = findViewById(R.id.img_good);
        imgNotGood = findViewById(R.id.img_notgood);

        imgNotGood.setOnClickListener(this);
        imgGood.setOnClickListener(this);
        imgVGood.setOnClickListener(this);
    }

    private void userFeedBack() {
        custPrograssbar.prograssCreate(RatesActivity.this);
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
        Log.e("Feedback Respons", ":" + result);
        if (result != null) {
            try {
                JSONObject object = new JSONObject(result.toString());
                Toast.makeText(RatesActivity.this, "" + object.getString("ResponseMsg"), Toast.LENGTH_SHORT).show();
                if (object.getString("Result").equals("true")) {
                    edFeedback.setText("");
                    isrates = true;
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validation() {
        if (edFeedback.getText().toString().isEmpty()) {
            edFeedback.setError("Enter FeedBack");
            return false;
        }
        if (selectRate == 0) {
            Toast.makeText(RatesActivity.this, getString(R.string.plaseselctrates), Toast.LENGTH_LONG).show();
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
        } else if (view.getId() == R.id.img_good) {
            selectRate = 2;
            imgClick(2);
        }else if (view.getId() == R.id.img_notgood) {
            selectRate = 3;
            imgClick(3);
        }else {
            if (validation()) {
                userFeedBack();
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

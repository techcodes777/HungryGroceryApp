package com.freshfastfood.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.freshfastfood.R;
import com.freshfastfood.model.User;
import com.freshfastfood.retrofit.APIClient;
import com.freshfastfood.retrofit.GetResult;
import com.freshfastfood.utils.CustPrograssbar;
import com.freshfastfood.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class FeedBackActivity extends BaseActivity implements GetResult.MyListener {

    TextInputEditText edFeedback;
    TextView btnSubmit;
    RatingBar ratingBar;
    User user;
    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        onViewClicked();
        sessionManager = new SessionManager(FeedBackActivity.this);
        user = sessionManager.getUserDetails();
        custPrograssbar = new CustPrograssbar();
    }

    private void init() {
        edFeedback = findViewById(R.id.ed_feedback);
        btnSubmit = findViewById(R.id.btn_submit);
        ratingBar = findViewById(R.id.ratingBar);
    }

    private void userFeedBack() {
        custPrograssbar.prograssCreate(FeedBackActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg", edFeedback.getText().toString());
            jsonObject.put("rate", String.valueOf(ratingBar.getRating()));
            jsonObject.put("uid", user.getId());
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().sendFeed((JsonObject) jsonParser.parse(jsonObject.toString()));
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
        try {
            if (result != null) {

                    JSONObject object = new JSONObject(result.toString());
                    Toast.makeText(FeedBackActivity.this, "" + object.getString("ResponseMsg"), Toast.LENGTH_SHORT).show();
                    if (object.getString("Result").equals("true")) {
                        ratingBar.setRating(0.0f);
                        edFeedback.setText("");
                    }

            }
        } catch (Exception e) {
            e.toString();
        }
    }
    public void onViewClicked() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    userFeedBack();
                }
            }
        });

    }
    private boolean validation() {
        if (edFeedback.getText().toString().isEmpty()) {
            edFeedback.setError(getString(R.string.efeedback));
            return false;
        }
        if (String.valueOf(ratingBar.getRating()).equals("0.0")) {
            Toast.makeText(FeedBackActivity.this, getString(R.string.giverating), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

package com.freshfastfood.activity;

import static com.freshfastfood.utils.SessionManager.currncy;
import static com.freshfastfood.utils.Utiles.isrates;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.freshfastfood.R;
import com.freshfastfood.model.MyOrder;
import com.freshfastfood.model.Productinfo;
import com.freshfastfood.model.RestResponse;
import com.freshfastfood.model.User;
import com.freshfastfood.retrofit.APIClient;
import com.freshfastfood.retrofit.GetResult;
import com.freshfastfood.utils.CustPrograssbar;
import com.freshfastfood.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kofigyan.stateprogressbar.StateProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MyOrderListActivity extends BaseActivity implements GetResult.MyListener {

    User user;
    SessionManager sessionManager;
    String oid;
    String id;
    TextView txtOrderid;
    TextView txtSubtotal;
    TextView txtDate;
    TextView txtDelivery;
    TextView txtTimeslot;
    TextView txtTax;
    TextView txtTattt;
    TextView txtQty;
    TextView txtTotal;
    LinearLayout lvlItems;
    LinearLayout lvlData;
    TextView txtStutus;
    TextView txtPtype;
    TextView txtPikupmyself;
    TextView txtCoupon;
    LinearLayout lvlCouponcode;
    TextView txtAddress;
    TextView txtWdiscount;
    String phonecall;
    StateProgressBar timeView;
    CustPrograssbar custPrograssbar;
    public static MyOrderListActivity orderListActivity = null;

    public static MyOrderListActivity getInstance() {
        return orderListActivity;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        init();
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("OrderList ");
        getSupportActionBar().setElevation(0);
        custPrograssbar=new CustPrograssbar();
        sessionManager = new SessionManager(MyOrderListActivity.this);
        user = sessionManager.getUserDetails();
        String[] descriptionData = {"Pending", "Ready to Ship", "Delivered"};
        timeView.setStateDescriptionData(descriptionData);
        timeView.checkStateCompleted(true);
        Intent intent = getIntent();
        id = intent.getStringExtra("oid");
        getHistry(id);
    }

    private void init() {
        txtOrderid = findViewById(R.id.txt_orderid);
        txtSubtotal = findViewById(R.id.txt_subtotal);
        txtDate = findViewById(R.id.txt_date);
        txtDelivery = findViewById(R.id.txt_delivery);
        txtTimeslot = findViewById(R.id.txt_timeslot);
        txtTax = findViewById(R.id.txt_tax);
        txtTattt = findViewById(R.id.txt_tattt);
        txtQty = findViewById(R.id.txt_qty);
        txtTotal = findViewById(R.id.txt_total);
        lvlItems = findViewById(R.id.lvl_items);
        lvlData = findViewById(R.id.lvl_data);
        txtStutus = findViewById(R.id.txt_stutus);
        txtPtype = findViewById(R.id.txt_ptype);
        txtPikupmyself = findViewById(R.id.txt_pikupmyself);
        txtCoupon = findViewById(R.id.txt_coupon);
        lvlCouponcode = findViewById(R.id.lvlcouponcode);
        txtAddress = findViewById(R.id.txt_Address);
        txtWdiscount = findViewById(R.id.txt_wdiscount);
        timeView = findViewById(R.id.time_view);
    }

    private void getHistry(String id) {
        custPrograssbar.prograssCreate(MyOrderListActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("uid", user.getId());
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getPlist((JsonObject) jsonParser.parse(jsonObject.toString()));
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
            if (callNo.equalsIgnoreCase("1")) {
                List<Productinfo> list = new ArrayList<>();
                Gson gson = new Gson();
                MyOrder myOrder = gson.fromJson(result.toString(), MyOrder.class);
                if (myOrder.getResult().equals("true")) {
                    list.addAll(myOrder.getProductinfo());
                    if (myOrder.getpMethod().equalsIgnoreCase(getResources().getString(R.string.pic_myslf))) {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lvlData.getLayoutParams();
                        params.setMargins(0, 0, 0, 100);
                        lvlData.setLayoutParams(params);
                        txtPikupmyself.setVisibility(View.VISIBLE);
                    } else {
                        txtPikupmyself.setVisibility(View.GONE);

                    }
                    phonecall = myOrder.getRiderMobile();
                    txtOrderid.setText("" + myOrder.getmOrderid());
                    txtCoupon.setText(sessionManager.getStringData(currncy)+ myOrder.getCounponDiscount());
                    txtStutus.setText("" + myOrder.getStatus());
                    txtDate.setText("" + myOrder.getOrderDate());
                    txtTimeslot.setText("" + myOrder.getTimesloat());
                    txtDelivery.setText("" + sessionManager.getStringData(currncy) + myOrder.getdCharge());
                    txtPtype.setText(" " + myOrder.getpMethod() + " ");
                    txtQty.setText("" + list.size());
                    txtAddress.setText("" + myOrder.getAddress());
                    txtWdiscount.setText("" + myOrder.getWalletDiscount());
                    double tex = ((myOrder.getSubTotal()) / 100.0f) * myOrder.getTax();
                    txtTax.setText(sessionManager.getStringData(currncy) + new DecimalFormat("##.##").format(tex));
                    txtTattt.setText("Tax(" + myOrder.getTax() + " %):");

                    txtSubtotal.setText("" + sessionManager.getStringData(currncy) + new DecimalFormat("##.##").format(myOrder.getSubTotal()));
                    txtTotal.setText("" + sessionManager.getStringData(currncy) + new DecimalFormat("##.##").format(myOrder.getTotalAmt()));
                    oid = myOrder.getmOrderid();
                    if(myOrder.getCounponDiscount().equalsIgnoreCase("0")){
                        lvlCouponcode.setVisibility(View.GONE);
                    }else {
                        lvlCouponcode.setVisibility(View.VISIBLE);

                    }
                    if (myOrder.getmIsrated().equalsIgnoreCase("No") && myOrder.getStatus().equalsIgnoreCase("completed")) {
                        item.setVisible(true);
                    } else {
                        item.setVisible(false);
                    }
                    if (myOrder.getStatus().equalsIgnoreCase("processing") || myOrder.getStatus().equalsIgnoreCase(getResources().getString(R.string.pic_myslf))) {
                        itemC.setVisible(true);
                    } else {
                        itemC.setVisible(false);
                    }
                    if ( myOrder.getpMethod().equalsIgnoreCase(getResources().getString(R.string.pic_myslf))) {
                        timeView.setVisibility(View.GONE);
                    }

                    if (myOrder.getStatus().equalsIgnoreCase("Pending")) {
                        timeView.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                    } else if (myOrder.getStatus().equalsIgnoreCase("processing")) {
                        timeView.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                    } else if (myOrder.getStatus().equalsIgnoreCase("completed")) {
                        timeView.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                    } else if (myOrder.getStatus().equalsIgnoreCase("cancelled")) {
                        timeView.setVisibility(View.GONE);
                    }
                    setOrderList(lvlItems, list);
                }else {
                    custPrograssbar.closePrograssBar();

                }
            } else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result.toString(), RestResponse.class);
                Toast.makeText(MyOrderListActivity.this, response.getResponseMsg(), Toast.LENGTH_LONG).show();
                if (response.getResult().equalsIgnoreCase("true")) {
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOrderList(LinearLayout lnrView, List<Productinfo> list) {
        lnrView.removeAllViews();
        int a = 0;
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                LayoutInflater inflater = LayoutInflater.from(MyOrderListActivity.this);
                a = a + 1;
                View view = inflater.inflate(R.layout.custome_myoder, null);
                ImageView imgIcon = view.findViewById(R.id.img_icon);
                TextView txtName = view.findViewById(R.id.txt_name);
                TextView txtqtr = view.findViewById(R.id.txt_qty);
                TextView txtWeight = view.findViewById(R.id.txt_weight);
                TextView txtPrice = view.findViewById(R.id.txt_price);
                Glide.with(MyOrderListActivity.this).load(APIClient.baseUrl + "/" + list.get(i).getProductImage()).thumbnail(Glide.with(MyOrderListActivity.this).load(R.drawable.lodingimage)).into(imgIcon);
                txtName.setText(" " + list.get(i).getProductName());
                txtqtr.setText(getString(R.string.qty)+" " + list.get(i).getProductQty());
                txtWeight.setText(" " + list.get(i).getProductWeight());
                double ress = (Double.parseDouble(list.get(i).getProductPrice()) * list.get(i).getDiscount()) / 100;
                ress = Double.parseDouble(list.get(i).getProductPrice()) - ress;
                txtPrice.setText(sessionManager.getStringData(currncy) + ress);
                lnrView.addView(view);
            }
        }
        custPrograssbar.closePrograssBar();

    }

    MenuItem item;
    MenuItem itemC;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rates_menu, menu);
        item = menu.findItem(R.id.item_rates);
        itemC = menu.findItem(R.id.item_cancel);
        item.setVisible(false);
        itemC.setVisible(false);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_rates){
            startActivity(new Intent(MyOrderListActivity.this, RatesActivity.class).putExtra("oid", oid));
        }else if (item.getItemId() == R.id.item_cancel){
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phonecall));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);

                return true;
            }
            startActivity(intent);
        } else {
            finish();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (item != null && isrates) {
            isrates = false;
            item.setVisible(false);
        }

    }
}

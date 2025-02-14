package com.freshfastfood.fragment;

import static com.freshfastfood.utils.SessionManager.coupon;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.freshfastfood.R;
import com.freshfastfood.activity.HomeActivity;
import com.freshfastfood.model.Payment;
import com.freshfastfood.model.PaymentItem;
import com.freshfastfood.model.Times;
import com.freshfastfood.retrofit.APIClient;
import com.freshfastfood.retrofit.GetResult;
import com.freshfastfood.utils.CustPrograssbar;
import com.freshfastfood.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;

public class PlaceOrderFragment extends Fragment implements View.OnClickListener, GetResult.MyListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RadioGroup rdgTime;
    TextView txtSelectdate;
    LinearLayout lvlPaymnet;
    int day = 1;
    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    private ImageView imgLeftDate,imgRightDate;

    public PlaceOrderFragment() {
        // Required empty public constructor
    }

    public static PlaceOrderFragment newInstance(String param1, String param2) {
        PlaceOrderFragment fragment = new PlaceOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plase_order, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rdgTime = view.findViewById(R.id.radiogroup);
        txtSelectdate = view.findViewById(R.id.txt_selectdate);
        lvlPaymnet = view.findViewById(R.id.lvl_paymnet);
        imgLeftDate = view.findViewById(R.id.img_ldate);
        imgRightDate = view.findViewById(R.id.img_rdate);

        custPrograssbar = new CustPrograssbar();
        sessionManager=new SessionManager(getActivity());
        getTimeSlot();
        txtSelectdate.setText("" + getCurrentDate());
        HomeActivity.getInstance().setFrameMargin(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.getInstance().serchviewHide();
        HomeActivity.getInstance().setFrameMargin(0);
    }

    private void getTimeSlot() {
        custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        JsonParser jsonParser = new JsonParser();
        Call<JsonObject> call = APIClient.getInterface().getTimeslot((JsonObject) jsonParser.parse(jsonObject.toString()));
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");
    }

    private void getPayment() {

        JSONObject jsonObject = new JSONObject();
        JsonParser jsonParser = new JsonParser();
        Call<JsonObject> call = APIClient.getInterface().getpaymentgateway((JsonObject) jsonParser.parse(jsonObject.toString()));
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "2");
    }


    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            if (callNo.equalsIgnoreCase("1")) {
                RadioButton rdbtn = null;
                Log.e("Response", "->" + result);
                Gson gson = new Gson();
                Times times = gson.fromJson(result.toString(), Times.class);
                for (int i = 0; i < times.getData().size(); i++) {
                    rdbtn = new RadioButton(getActivity());
                    rdbtn.setId(View.generateViewId());
                    rdbtn.setText(times.getData().get(i).getMintime() + " - " + times.getData().get(i).getMaxtime());
                    rdbtn.setOnClickListener(this);
                    rdgTime.addView(rdbtn);
                }
                rdgTime.check(rdbtn.getId());
                getPayment();
            } else if (callNo.equalsIgnoreCase("2")) {
                custPrograssbar.closePrograssBar();
                Gson gson = new Gson();
                Payment payment = gson.fromJson(result.toString(), Payment.class);
                setJoinPlayrList(lvlPaymnet, payment.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setJoinPlayrList(LinearLayout lnrView, List<PaymentItem> paymentList) {
        lnrView.removeAllViews();
        for (int i = 0; i < paymentList.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            PaymentItem paymentItem = paymentList.get(i);
            View view = inflater.inflate(R.layout.custome_paymen, null);
            ImageView imageView = view.findViewById(R.id.img_icon);
            TextView txtTitle = view.findViewById(R.id.txt_title);
            txtTitle.setText("" + paymentList.get(i).getTitle());
            Glide.with(getActivity()).load(APIClient.baseUrl + "/" + paymentList.get(i).getImg()).thumbnail(Glide.with(getActivity()).load(R.drawable.ezgifresize)).into(imageView);
            view.setOnClickListener(v -> {
                try {


                        int selectedId = rdgTime.getCheckedRadioButtonId();
                        RadioButton selectTime = rdgTime.findViewById(selectedId);
                        sessionManager.setIntData(coupon, 0);
                        OrderSumrryFragment fragment = new OrderSumrryFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("DATE", txtSelectdate.getText().toString());
                        bundle.putString("TIME", selectTime.getText().toString());
                        bundle.putString("PAYMENT", paymentItem.getTitle());
                        bundle.putSerializable("PAYMENTDETAILS", paymentItem);
                        fragment.setArguments(bundle);
                        HomeActivity.getInstance().callFragment(fragment);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            lnrView.addView(view);
        }
    }



    @Override
    public void onClick(View view) {
        if (view.getId() ==  R.id.img_ldate){
            minusDate(txtSelectdate.getText().toString());
        }else {
            addDate(txtSelectdate.getText().toString());
        }
    }

    private String getCurrentDate() {
        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(d);
        try {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, day);  // number of days to add
            formattedDate = df.format(c.getTime());
            c.setTime(df.parse(formattedDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    private String addDate(String dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        Date strDate = null;
        try {
            strDate = sdf.parse(dt);
            if ((System.currentTimeMillis() + 432000000) < strDate.getTime()) {
                Log.e("date change ", "--> 1");
                return dt;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        try {

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, day);  // number of days to add
            dt = sdf.format(c.getTime());
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        day++;
        txtSelectdate.setText("" + dt);
        return dt;
    }

    private String minusDate(String dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date strDate = null;
        try {
            strDate = sdf.parse(dt);
            if ((System.currentTimeMillis() + 86400000) > strDate.getTime()) {
                Log.e("date change ", "--> 1");
                return dt;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        day--;
        try {

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, day);  // number of days to add
            dt = sdf.format(c.getTime());
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        txtSelectdate.setText("" + dt);
        return dt;
    }
}

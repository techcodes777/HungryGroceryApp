package com.freshfastfood.activity;

import static com.freshfastfood.fragment.ItemListFragment.itemListFragment;
import static com.freshfastfood.fragment.OrderSumrryFragment.isorder;
import static com.freshfastfood.utils.SessionManager.currncy;
import static com.freshfastfood.utils.SessionManager.language;
import static com.freshfastfood.utils.SessionManager.login;
import static com.freshfastfood.utils.Utiles.isSelect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.freshfastfood.R;
import com.freshfastfood.database.DatabaseHelper;
import com.freshfastfood.fragment.AddressFragment;
import com.freshfastfood.fragment.CardFragment;
import com.freshfastfood.fragment.HomeFragment;
import com.freshfastfood.fragment.ItemListFragment;
import com.freshfastfood.fragment.MyOrderFragment;
import com.freshfastfood.fragment.OrderSumrryFragment;
import com.freshfastfood.model.User;
import com.freshfastfood.utils.CustPrograssbar;
import com.freshfastfood.utils.SessionManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;


public class HomeActivity extends BaseActivity implements View.OnClickListener {
    EditText edSearch;
    ImageView imgSearch;
    ImageView imgCart;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    LinearLayout lvlActionsearch;
    LinearLayout lvlHome;
    LinearLayout lvlMainhome;
    TextView txtActiontitle;
    TextView txtLogintitel;
    FrameLayout fragmentFrame;
    AppBarLayout appBarLayout;
    LinearLayout lvlHidecart,lllanguage;
    TextView txtMob;
    TextView txtfirstl;

    TextView txtWallet;
    TextView txtEmail;
    ImageView imgClose;
    LinearLayout myprofile;
    LinearLayout myoder;
    LinearLayout address;
    LinearLayout feedback;
    LinearLayout contect;
    LinearLayout logout;
    LinearLayout about;
    LinearLayout tramscondition;
    LinearLayout privecy;
    LinearLayout termcondition;
    LinearLayout drawer;
    RelativeLayout rltCart;
    RelativeLayout rltNoti;
    TextView txtCountcard;
    ImageView imgNoti;
    LinearLayout share,refer,mywallet;

    User user;
    public static TextView txtCountcard1;
    public static HomeActivity homeActivity = null;
    public static TextView txtNoti;
    public static CustPrograssbar custPrograssbar;
    Fragment fragment1 = null;
    DatabaseHelper databaseHelper;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();

        txtNoti = findViewById(R.id.txt_noti);
        custPrograssbar = new CustPrograssbar();
        databaseHelper = new DatabaseHelper(HomeActivity.this);
        sessionManager = new SessionManager(HomeActivity.this);
        user = sessionManager.getUserDetails();
        homeActivity = this;
        setDrawer();
    }

    private void init() {
        edSearch = findViewById(R.id.ed_search);
        imgSearch = findViewById(R.id.img_search);
        imgCart = findViewById(R.id.img_cart);
        drawerLayout = findViewById(R.id.drawer_layout);
        lvlActionsearch = findViewById(R.id.lvl_actionsearch);
        lvlHome = findViewById(R.id.lvl_home);
        lvlMainhome = findViewById(R.id.lvl_mainhome);
        txtActiontitle = findViewById(R.id.txt_actiontitle);
        txtLogintitel = findViewById(R.id.txt_logintitel);
        fragmentFrame = findViewById(R.id.fragment_frame);
        appBarLayout = findViewById(R.id.appBarLayout);
        lvlHidecart = findViewById(R.id.lvl_hidecart);
        txtMob = findViewById(R.id.txt_mob);
        txtfirstl = findViewById(R.id.txtfirstl);
        txtWallet = findViewById(R.id.txt_wallet);
        txtEmail = findViewById(R.id.txt_email);
        imgClose = findViewById(R.id.img_close);
        myprofile = findViewById(R.id.myprofile);
        myoder = findViewById(R.id.myoder);
        address = findViewById(R.id.address);
        feedback = findViewById(R.id.feedback);
        contect = findViewById(R.id.contect);
        logout = findViewById(R.id.logout);
        about = findViewById(R.id.about);
        tramscondition = findViewById(R.id.tramscondition);
        privecy = findViewById(R.id.privecy);
        termcondition = findViewById(R.id.termcondition);
        drawer = findViewById(R.id.drawer);
        rltCart = findViewById(R.id.rlt_cart);
        rltNoti = findViewById(R.id.rlt_noti);
        txtCountcard = findViewById(R.id.txt_countcard);
        imgNoti = findViewById(R.id.img_noti);
        share = findViewById(R.id.share);
        refer = findViewById(R.id.refer);
        mywallet = findViewById(R.id.mywallet);
        lllanguage = findViewById(R.id.language);
        toolbar = findViewById(R.id.toolbar);

        termcondition.setOnClickListener(this);
        privecy.setOnClickListener(this);
        about.setOnClickListener(this);
        share.setOnClickListener(this);
        imgNoti.setOnClickListener(this);
        imgCart.setOnClickListener(this);
        imgSearch.setOnClickListener(this);
        logout.setOnClickListener(this);
        contect.setOnClickListener(this);
        feedback.setOnClickListener(this);
        address.setOnClickListener(this);
        myoder.setOnClickListener(this);
        myprofile.setOnClickListener(this);
        lvlHome.setOnClickListener(this);
        refer.setOnClickListener(this);
        mywallet.setOnClickListener(this);
        lllanguage.setOnClickListener(this);

    }

    public static HomeActivity getInstance() {
        return homeActivity;
    }

    public void showMenu() {
        rltNoti.setVisibility(View.GONE);
        rltCart.setVisibility(View.VISIBLE);
    }

    public void setFrameMargin(int top) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) lvlMainhome.getLayoutParams();
        params.setMargins(0, top, 0, 0);
        lvlMainhome.setLayoutParams(params);
    }

    public void setTxtWallet(String wallet) {
        if (sessionManager.getBooleanData(login)) {
            txtWallet.setVisibility(View.VISIBLE);
        } else {
            txtWallet.setVisibility(View.GONE);
        }
        txtWallet.setText(sessionManager.getStringData(currncy) + wallet);
    }

    @SuppressLint("SetTextI18n")
    private void setDrawer() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
//        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_icon);
        char first = user.getName().charAt(0);
        Log.e("first", "-->" + first);
        txtfirstl.setText("" + first);

        txtMob.setText("" + user.getMobile());
        txtEmail.setText("" + user.getEmail());

        titleChange();
        txtCountcard1 = findViewById(R.id.txt_countcard);
        Cursor res = databaseHelper.getAllData();
        if (res.getCount() == 0) {
            txtCountcard1.setText("0");
        } else {
            txtCountcard1.setText("" + res.getCount());
        }
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment).addToBackStack("HomePage").commit();
        addTextWatcher();
        if (sessionManager.getBooleanData(login)) {
            txtLogintitel.setText(R.string.logout);
        } else {
            txtLogintitel.setText(R.string.login);

        }
    }

    public EditText passThisEditText() {
        return edSearch;
    }

    private void addTextWatcher() {
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edSearch.getText().toString().trim().length() == 0) {

                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
                    if (fragment instanceof HomeFragment && fragment.isVisible()) {
                        Log.e("no", "jsd");
                    } else {
                        getSupportFragmentManager().popBackStackImmediate();

                    }
                }
            }
        });
        edSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (edSearch.getText().toString().trim().length() != 0) {
                        Bundle args;
                        Fragment fragment;
                        args = new Bundle();
                        args.putInt("id", 0);
                        args.putString("search", edSearch.getText().toString().trim());
                        fragment = new ItemListFragment();
                        fragment.setArguments(args);
                        callFragment(fragment);
                    } else {
                        fragment1 = null;
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
            if (fragment instanceof HomeFragment && fragment.isVisible()) {
                finish();
            } else if (fragment instanceof OrderSumrryFragment && fragment.isVisible() && isorder) {
                isorder = false;
                Intent i = new Intent(this, HomeActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(0, 0);
            } else if (fragment instanceof MyOrderFragment && fragment.isVisible()) {
                Intent i = new Intent(this, HomeActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(0, 0);
            }

            if (fragment instanceof OrderSumrryFragment && fragment.isVisible()) {
                edSearch.setText("");
                lvlActionsearch.setVisibility(View.GONE);
            } else if (fragment instanceof AddressFragment && fragment.isVisible()) {
                edSearch.setText("");
            } else {
                edSearch.setText("");
            }
            if (itemListFragment == null) {
                rltNoti.setVisibility(View.VISIBLE);
                rltCart.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setdata() {
        rltNoti.setVisibility(View.VISIBLE);
        rltCart.setVisibility(View.VISIBLE);
    }

    public void hideActionbar() {
        appBarLayout.setVisibility(View.GONE);
        lvlHidecart.setVisibility(View.GONE);
        drawer.setVisibility(View.GONE);
    }

    public void serchviewHide() {
        lvlActionsearch.setVisibility(View.GONE);
    }

    public void serchviewShow() {
        lvlActionsearch.setVisibility(View.VISIBLE);
    }

    public void titleChange(String s) {
        txtActiontitle.setText(s);
    }

    public void titleChange() {
        txtActiontitle.setText("Hello " + user.getName());
    }


    public void callFragment(Fragment fragmentClass) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragmentClass).addToBackStack("adds").commit();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String shareMessage = "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName() + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bottonPaymentList() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.custome_launguage, null);
        LinearLayout lvlenglish = sheetView.findViewById(R.id.lvl_english);
        LinearLayout lvlGujrati = sheetView.findViewById(R.id.lvl_gujrati);
        LinearLayout lvlarb = sheetView.findViewById(R.id.lvl_arb);
        LinearLayout lvlhind = sheetView.findViewById(R.id.lvl_hind);

        lvlenglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sessionManager.setStringData(language, "en");
                startActivity(new Intent(HomeActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });

        lvlGujrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sessionManager.setStringData(language, "es");
                startActivity(new Intent(HomeActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();


            }
        });

        lvlarb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sessionManager.setStringData(language, "ar");
                startActivity(new Intent(HomeActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();


            }
        });

        lvlhind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sessionManager.setStringData(language, "hi");
                startActivity(new Intent(HomeActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();


            }
        });


        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
    }

    @Override
    public void onClick(View view) {
        Fragment fragment;
        Bundle args;
            if (view.getId() == R.id.language) {
                bottonPaymentList();
            } else if (view.getId() == R.id.mywallet) {
                if (sessionManager.getBooleanData(login)) {
                    startActivity(new Intent(HomeActivity.this, WalletActivity.class).putExtra("wallat", txtWallet.getText().toString()));
                } else {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
            } else if (view.getId() == R.id.refer) {
                if (sessionManager.getBooleanData(login)) {
                    startActivity(new Intent(HomeActivity.this, ReferlActivity.class));
                } else {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
            }else if (view.getId() == R.id.lvl_home) {
                lvlActionsearch.setVisibility(View.VISIBLE);
                titleChange();
                edSearch.setText("");
                fragment = new HomeFragment();
                callFragment(fragment);
            }else if (view.getId() == R.id.myprofile) {
                titleChange();
                if (sessionManager.getBooleanData(login)) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));

                } else {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
            }else if (view.getId() == R.id.myoder) {
                titleChange();

                if (sessionManager.getBooleanData(login)) {
                    lvlActionsearch.setVisibility(View.GONE);
                    fragment = new MyOrderFragment();
                    callFragment(fragment);
                } else {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
            }else if (view.getId() == R.id.address) {
                titleChange();

                if (sessionManager.getBooleanData(login)) {
                    lvlActionsearch.setVisibility(View.GONE);
                    isSelect = false;
                    fragment = new AddressFragment();
                    callFragment(fragment);
                } else {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
            }else if (view.getId() == R.id.feedback) {
                titleChange();
                if (sessionManager.getBooleanData(login)) {
                    startActivity(new Intent(HomeActivity.this, FeedBackActivity.class));

                } else {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
            }else if (view.getId() == R.id.contect) {
                titleChange();

                startActivity(new Intent(HomeActivity.this, ContectusActivity.class));
            }else if (view.getId() == R.id.logout) {
                if (sessionManager.getBooleanData(login)) {
                    sessionManager.logoutUser();
                    databaseHelper.deleteCard();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
            }else if (view.getId() == R.id.img_search) {
                if (edSearch.getText().toString().trim().length() != 0) {
                    args = new Bundle();
                    args.putInt("id", 0);
                    args.putString("search", edSearch.getText().toString().trim());
                    fragment = new ItemListFragment();
                    fragment.setArguments(args);
                    callFragment(fragment);
                } else {
                    fragment1 = null;
                }
            }else if (view.getId() == R.id.img_cart) {
                txtActiontitle.setVisibility(View.VISIBLE);
                rltNoti.setVisibility(View.GONE);
                rltCart.setVisibility(View.VISIBLE);
                txtActiontitle.setText("MyCart");
                fragment = new CardFragment();
                callFragment(fragment);
            }else if (view.getId() == R.id.img_noti) {
                titleChange();

                startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
            }else if (view.getId() == R.id.share) {
                shareApp();
            }else if (view.getId() == R.id.about) {
                titleChange();

                startActivity(new Intent(HomeActivity.this, AboutsActivity.class));
            }else if (view.getId() == R.id.privecy) {
                titleChange();

                startActivity(new Intent(HomeActivity.this, PrivecyPolicyActivity.class));
            }else if (view.getId() == R.id.termcondition) {
                titleChange();

                startActivity(new Intent(HomeActivity.this, TramsAndConditionActivity.class));
            }else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public static void notificationCount(int i) {
        if (i <= 0) {
            txtNoti.setVisibility(View.GONE);
        } else {
            txtNoti.setVisibility(View.VISIBLE);
            txtNoti.setText("" + i);
        }
    }

}

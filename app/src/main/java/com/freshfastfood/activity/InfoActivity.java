package com.freshfastfood.activity;

import static com.freshfastfood.utils.SessionManager.isopen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.freshfastfood.R;
import com.freshfastfood.fragment.Info1Fragment;
import com.freshfastfood.fragment.Info2Fragment;
import com.freshfastfood.fragment.Info3Fragment;
import com.freshfastfood.model.User;
import com.freshfastfood.utils.SessionManager;
import com.merhold.extensiblepageindicator.ExtensiblePageIndicator;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener{


    public static ViewPager vpPager;
    MyPagerAdapter adapterViewPager;
    public static TextView btnNext;
    TextView btnSkip;
    int selectPage = 0;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        btnNext = findViewById(R.id.btn_next);
        vpPager = findViewById(R.id.vpPager);
        btnSkip = findViewById(R.id.btn_skip);
        sessionManager = new SessionManager(InfoActivity.this);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        ExtensiblePageIndicator extensiblePageIndicator = (ExtensiblePageIndicator) findViewById(R.id.flexibleIndicator);
        extensiblePageIndicator.initViewPager(vpPager);
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectPage = position;

                if (position == 0 || position == 1) {
                    btnSkip.setVisibility(View.VISIBLE);
                    btnNext.setText("Next");
                } else if (position == 2) {
                    btnSkip.setVisibility(View.GONE);
                    btnNext.setText("Finish");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        btnNext.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_next){
            if (selectPage == 0) {
                vpPager.setCurrentItem(1);
            } else if (selectPage == 1) {
                vpPager.setCurrentItem(2);
            } else if (selectPage == 2) {
                sessionManager.setBooleanData(isopen, true);
                User user = new User();
                user.setId("0");
                user.setName("User");
                user.setEmail("user@gmail.com");
                user.setMobile("+91 8888888888");
                sessionManager.setUserDetails(user);
                startActivity(new Intent(InfoActivity.this, HomeActivity.class));
                finish();
            }
        }else {
            sessionManager.setBooleanData(isopen, true);
            User user = new User();
            user.setId("0");
            user.setName("User");
            user.setEmail("user@gmail.com");
            user.setMobile("+91 8888888888");
            sessionManager.setUserDetails(user);
            startActivity(new Intent(InfoActivity.this, HomeActivity.class));
            finish();
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private int anInt = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return anInt;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return Info1Fragment.newInstance();
                case 1:
                    return Info2Fragment.newInstance();
                case 2:
                    return Info3Fragment.newInstance();
                default:
                    return null;
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.e("page", "" + position);
            return "Page " + position;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            return fragment;
        }

    }
}

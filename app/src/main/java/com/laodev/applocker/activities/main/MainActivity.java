package com.laodev.applocker.activities.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.laodev.applocker.R;
import com.laodev.applocker.base.AppConstants;
import com.laodev.applocker.base.BaseActivity;
import com.laodev.applocker.model.CommLockInfo;
import com.laodev.applocker.activities.setting.SettingsActivity;
import com.laodev.applocker.gesture.contract.LockMainContract;
import com.laodev.applocker.gesture.presenter.LockMainPresenter;
import com.laodev.applocker.services.BackgroundManager;
import com.laodev.applocker.services.LockService;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements LockMainContract.View, View.OnClickListener {

    private ImageView btn_setting;
    private TabLayout tab_layout;
    private ViewPager view_pager;
    private PagerAdapter mPagerAdapter;
    private LockMainPresenter mLockMainPresenter;
    private List<String> titles;
    private List<Fragment> fragmentList;


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        btn_setting = findViewById(R.id.btn_setting);
        tab_layout = findViewById(R.id.tab_layout);
        view_pager = findViewById(R.id.view_pager);

        mLockMainPresenter = new LockMainPresenter(this, this);
        mLockMainPresenter.loadAppInfo(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intents = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intents, 0);
                Toast.makeText(MainActivity.this, "Allow this permission for display lockscreen", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void initData() {

        PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(AppConstants.APP_PACKAGE_NAME)) {
                @SuppressLint("BatteryLife")
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + AppConstants.APP_PACKAGE_NAME));
                startActivity(intent);
            }
        }
        if(!BackgroundManager.getInstance().init(this).isServiceRunning(LockService.class)){
            BackgroundManager.getInstance().init(this).startService(LockService.class);
        }
        BackgroundManager.getInstance().init(this).startAlarmManager();
    }


    @Override
    protected void initAction() {
        btn_setting.setOnClickListener(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void loadAppInfoSuccess(@NonNull List<CommLockInfo> list) {
        int sysNum = 0;
        int userNum = 0;
        for (CommLockInfo info : list) {
            if (info.isSysApp()) {
                sysNum++;
            } else {
                userNum++;
            }
        }
        titles = new ArrayList<>();
        titles.add("System Apps" + " (" + sysNum + ")");
        titles.add("Your Apps" + " (" + userNum + ")");

        SysAppFragment systemAppFragment = SysAppFragment.newInstance(list);
        UserAppFragment userAppFragment = UserAppFragment.newInstance(list);

        fragmentList = new ArrayList<>();
        fragmentList.add(systemAppFragment);
        fragmentList.add(userAppFragment);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragmentList, titles);
        view_pager.setAdapter(mPagerAdapter);
        tab_layout.setupWithViewPager(view_pager);

    }

    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btn_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

        }
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragmentList ;
        private List<String> titles ;


        public PagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titles) {
            super(fm);
            this.fragmentList = fragmentList;
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public int getCount() {
            return titles.size();
        }
    }

}

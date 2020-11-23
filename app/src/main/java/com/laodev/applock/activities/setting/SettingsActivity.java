package com.laodev.applock.activities.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.laodev.applock.R;
import com.laodev.applock.activities.protect.ChangePasswordActivity;
import com.laodev.applock.base.AppConstants;
import com.laodev.applock.base.BaseActivity;
import com.laodev.applock.model.LockAutoTime;
import com.laodev.applock.services.BackgroundManager;
import com.laodev.applock.services.LockService;
import com.laodev.applock.utils.MainUtil;
import com.laodev.applock.utils.ToastUtil;
import com.laodev.applock.widget.AppLockTimeDialog;

public class SettingsActivity extends BaseActivity implements View.OnClickListener
        , DialogInterface.OnDismissListener, CompoundButton.OnCheckedChangeListener {

    public static final String ON_ITEM_CLICK_ACTION = "on_item_click_action";
    private static final int REQUEST_CHANGE_PWD = 3;

    private CheckBox chk_on_off;
    private CheckBox chk_lock_with_phone_lock;
    private CheckBox chk_hide_pattern;

    private TextView
            app_lock_time,
            btn_change_pass,
            btn_privacy;

    private LockSettingReceiver mLockSettingReceiver;
    private AppLockTimeDialog appLockTimeDialog;
    private ImageView btn_back;

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        chk_on_off =findViewById(R.id.chk_on_off);
        chk_lock_with_phone_lock =findViewById(R.id.chk_lock_with_phone_lock);
        chk_hide_pattern =findViewById(R.id.chk_hide_pattern);

        btn_change_pass = findViewById(R.id.btn_change_pass);
        app_lock_time = findViewById(R.id.app_lock_time);

        btn_privacy = findViewById(R.id.btn_privacy);

        btn_back = findViewById(R.id.btn_back);
    }

    @Override
    protected void initData() {
        mLockSettingReceiver = new LockSettingReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ON_ITEM_CLICK_ACTION);
        registerReceiver(mLockSettingReceiver, filter);
        appLockTimeDialog = new AppLockTimeDialog(this, "");
        appLockTimeDialog.setOnDismissListener(this);
        boolean isLockOpen = MainUtil.getInstance().getBoolean(AppConstants.LOCK_STATE);
        chk_on_off.setChecked(isLockOpen);

        boolean isLockAutoScreen = MainUtil.getInstance().getBoolean(AppConstants.LOCK_AUTO_SCREEN, false);
        chk_lock_with_phone_lock.setChecked(isLockAutoScreen);

        boolean isHidePattern = MainUtil.getInstance().getBoolean(AppConstants.LOCK_IS_HIDE_LINE, false);
        chk_hide_pattern.setChecked(isHidePattern);

        app_lock_time.setText(MainUtil.getInstance().getString(AppConstants.LOCK_APART_TITLE, "immediately"));
    }

    @Override
    protected void initAction() {

        chk_on_off.setOnCheckedChangeListener(this);
        chk_lock_with_phone_lock.setOnCheckedChangeListener(this);
        chk_hide_pattern.setOnCheckedChangeListener(this);

        app_lock_time.setOnClickListener(this);
        btn_change_pass.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_privacy.setOnClickListener(this);
    }

    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btn_change_pass: {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivityForResult(intent, REQUEST_CHANGE_PWD);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            }

            case R.id.app_lock_time:
                String title = MainUtil.getInstance().getString(AppConstants.LOCK_APART_TITLE, "");
                appLockTimeDialog.setTitle(title);
                appLockTimeDialog.show();
                break;

            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_privacy: {
                Uri webpage = Uri.parse("https://privacy.laodev.info/AppLock.php");
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
            }
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean b) {
        switch (buttonView.getId()) {
            case R.id.chk_on_off:
                MainUtil.getInstance().putBoolean(AppConstants.LOCK_STATE, b);
                if (b) {
                    BackgroundManager.getInstance().init(SettingsActivity.this).stopService(LockService.class);
                    BackgroundManager.getInstance().init(SettingsActivity.this).startService(LockService.class);

                    BackgroundManager.getInstance().init(SettingsActivity.this).startAlarmManager();

                } else {
                    BackgroundManager.getInstance().init(SettingsActivity.this).stopService(LockService.class);
                    BackgroundManager.getInstance().init(SettingsActivity.this).stopAlarmManager();
                }
                break;
            case R.id.chk_lock_with_phone_lock:
                MainUtil.getInstance().putBoolean(AppConstants.LOCK_AUTO_SCREEN, b);
                break;

            case R.id.chk_hide_pattern:
                MainUtil.getInstance().putBoolean(AppConstants.LOCK_IS_HIDE_LINE, b);
                break;


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CHANGE_PWD:
                    ToastUtil.showToast("Password reset succeeded");
                    break;
            }
        }
    }


    @Override
    public void onDismiss(DialogInterface dialogInterface) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLockSettingReceiver);
    }

    private class LockSettingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            String action = intent.getAction();
            if (action.equals(ON_ITEM_CLICK_ACTION)) {
                LockAutoTime info = intent.getParcelableExtra("info");
                boolean isLast = intent.getBooleanExtra("isLast", true);
                if (isLast) {
                    app_lock_time.setText(info.getTitle());
                    MainUtil.getInstance().putString(AppConstants.LOCK_APART_TITLE, info.getTitle());
                    MainUtil.getInstance().putLong(AppConstants.LOCK_APART_MILLISECONDS, 0L);
                    MainUtil.getInstance().putBoolean(AppConstants.LOCK_AUTO_SCREEN_TIME, false);
                } else {
                    app_lock_time.setText(info.getTitle());
                    MainUtil.getInstance().putString(AppConstants.LOCK_APART_TITLE, info.getTitle());
                    MainUtil.getInstance().putLong(AppConstants.LOCK_APART_MILLISECONDS, info.getTime());
                    MainUtil.getInstance().putBoolean(AppConstants.LOCK_AUTO_SCREEN_TIME, true);
                }
                appLockTimeDialog.dismiss();
            }
        }
    }

}

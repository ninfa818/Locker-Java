package com.laodev.applock.activities.password;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.laodev.applock.R;
import com.laodev.applock.base.AppConstants;
import com.laodev.applock.base.BaseActivity;
import com.laodev.applock.model.LockStage;
import com.laodev.applock.activities.main.MainActivity;
import com.laodev.applock.gesture.contract.GestureCreateContract;
import com.laodev.applock.gesture.presenter.GestureCreatePresenter;
import com.laodev.applock.services.BackgroundManager;
import com.laodev.applock.services.LockService;
import com.laodev.applock.utils.AppUtils;
import com.laodev.applock.utils.LockPatternUtils;
import com.laodev.applock.utils.MainUtil;
import com.laodev.applock.widget.LockPatternView;
import com.laodev.applock.widget.LockPatternViewPattern;
import com.laodev.applock.widget.PermissionDialogMI;
import com.laodev.applock.widget.PermissionDialogVivo;

import java.util.List;

public class DefinePasswordActivity extends BaseActivity implements View.OnClickListener,
        GestureCreateContract.View {

    @Nullable
    private List<LockPatternView.Cell> mChosenPattern = null;

    private TextView mLockTip;
    private LockPatternView mLockPatternView;
    @NonNull
    private final Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };
    private TextView mBtnReset;
    private LockStage mUiStage = LockStage.Introduction;
    private LockPatternUtils mLockPatternUtils;
    private LockPatternViewPattern mPatternViewPattern;
    private GestureCreatePresenter mGestureCreatePresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_define_password;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mLockPatternView = findViewById(R.id.lock_pattern_view);
        mLockTip = findViewById(R.id.lock_tip);
        mBtnReset = findViewById(R.id.btn_reset);


        if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi") || Build.MANUFACTURER.equalsIgnoreCase("redmi")) {

            PermissionDialogMI dialog = new PermissionDialogMI(DefinePasswordActivity.this);
            dialog.show();
            dialog.setOnClickListener(new PermissionDialogMI.onClickListener() {
                @Override
                public void onClick() {
                    AppUtils.autoStartMi(DefinePasswordActivity.this);
                }
            });
        }else {
            if (Build.MANUFACTURER.equalsIgnoreCase("vivo")){
                PermissionDialogVivo dialog = new PermissionDialogVivo(DefinePasswordActivity.this);
                dialog.show();
                dialog.setOnClickListener(new PermissionDialogVivo.onClickListener() {
                    @Override
                    public void onClick() {
                    }
                });
            }
            AppUtils.autoStart(DefinePasswordActivity.this);
        }
    }

    @Override
    protected void initData() {
        mGestureCreatePresenter = new GestureCreatePresenter(this, this);
        initLockPatternView();
    }

    private void initLockPatternView() {
        mLockPatternUtils = new LockPatternUtils(this);
        mPatternViewPattern = new LockPatternViewPattern(mLockPatternView);
        mPatternViewPattern.setPatternListener(new LockPatternViewPattern.onPatternListener() {
            @Override
            public void onPatternDetected(@NonNull List<LockPatternView.Cell> pattern) {
                mGestureCreatePresenter.onPatternDetected(pattern, mChosenPattern, mUiStage);
            }
        });
        mLockPatternView.setOnPatternListener(mPatternViewPattern);
        mLockPatternView.setTactileFeedbackEnabled(true);
    }

    @Override
    protected void initAction() {
        mBtnReset.setOnClickListener(this);
    }

    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btn_reset:
                setStepOne();
                break;
        }
    }

    private void setStepOne() {
        mGestureCreatePresenter.updateStage(LockStage.Introduction);
        mLockTip.setText(getString(R.string.lock_recording_intro_header));
    }

    private void gotoLockMainActivity() {
        MainUtil.getInstance().putBoolean(AppConstants.LOCK_STATE, true);
        BackgroundManager.getInstance().init(this).startService(LockService.class);
        MainUtil.getInstance().putBoolean(AppConstants.LOCK_IS_FIRST_LOCK, false);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void updateUiStage(LockStage stage) {
        mUiStage = stage;
    }

    @Override
    public void updateChosenPattern(List<LockPatternView.Cell> mChosenPattern) {
        this.mChosenPattern = mChosenPattern;
    }

    @Override
    public void updateLockTip(String text, boolean isToast) {
        mLockTip.setText(text);
    }

    @Override
    public void setHeaderMessage(int headerMessage) {
        mLockTip.setText(headerMessage);
    }

    @Override
    public void lockPatternViewConfiguration(boolean patternEnabled, LockPatternView.DisplayMode displayMode) {
        if (patternEnabled) {
            mLockPatternView.enableInput();
        } else {
            mLockPatternView.disableInput();
        }
        mLockPatternView.setDisplayMode(displayMode);
    }

    @Override
    public void Introduction() {
        clearPattern();
    }

    @Override
    public void HelpScreen() {

    }

    @Override
    public void ChoiceTooShort() {
        mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
        mLockPatternView.removeCallbacks(mClearPatternRunnable);
        mLockPatternView.postDelayed(mClearPatternRunnable, 500);
    }

    @Override
    public void moveToStatusTwo() {

    }


    @Override
    public void clearPattern() {
        mLockPatternView.clearPattern();
    }


    @Override
    public void ConfirmWrong() {
        mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
        mLockPatternView.removeCallbacks(mClearPatternRunnable);
        mLockPatternView.postDelayed(mClearPatternRunnable, 500);
    }


    @Override
    public void ChoiceConfirmed() {
        mLockPatternUtils.saveLockPattern(mChosenPattern);
        clearPattern();
        gotoLockMainActivity();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGestureCreatePresenter.onDestroy();
    }
}

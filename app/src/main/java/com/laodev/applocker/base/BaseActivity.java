package com.laodev.applocker.base;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.laodev.applocker.MyApp;
import com.laodev.applocker.R;
import com.laodev.applocker.utils.SystemBarHelper;


public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mCustomTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().doForCreate(this);
        setContentView(getLayoutId());

        initViews(savedInstanceState);
        initToolBar();
        initData();
        initAction();
    }

    public abstract int getLayoutId();

    protected abstract void initViews(Bundle savedInstanceState);

    protected void initToolBar() {
        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("");
            SystemBarHelper.immersiveStatusBar(this);
            SystemBarHelper.setHeightAndPadding(this, mToolbar);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            resetToolbar();
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setElevation(0);
        }
    }

    public void resetToolbar() {
        if (mCustomTitleTextView == null) {
            mCustomTitleTextView = (TextView) getLayoutInflater().inflate(R.layout.layout_toolbar_title, null);
        }
        getSupportActionBar().setCustomView(mCustomTitleTextView, new ActionBar.LayoutParams(Gravity.CENTER));
        if (getTitle() != null) {
            mCustomTitleTextView = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
            mCustomTitleTextView.setText(getTitle());
        }
    }

    public void hiddenActionBar() {
        getSupportActionBar().hide();
    }

    protected abstract void initData();

    protected abstract void initAction();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApp.getInstance().doForFinish(this);
    }

    public final void clear() {
        super.finish();
    }
}

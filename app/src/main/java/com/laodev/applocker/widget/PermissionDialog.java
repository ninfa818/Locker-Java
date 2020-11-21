package com.laodev.applocker.widget;

import android.animation.AnimatorSet;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.laodev.applocker.R;


public class PermissionDialog extends BaseDialog {

    private TextView mBtnPermission;
    private onClickListener mOnClickListener;

    public PermissionDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected float setWidthScale() {
        return 0.9f;
    }

    @Nullable
    @Override
    protected AnimatorSet setEnterAnim() {
        return null;
    }

    @Nullable
    @Override
    protected AnimatorSet setExitAnim() {
        return null;
    }

    @Override
    protected void init() {
        mBtnPermission = findViewById(R.id.btn_permission);
        mBtnPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null) {
                    dismiss();
                    mOnClickListener.onClick();
                }
            }
        });
    }

    public void setOnClickListener(onClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface onClickListener {
        void onClick();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_permission;
    }

}

package com.laodev.applock.gesture.contract;

import android.content.Context;

import com.laodev.applock.base.BasePresenter;
import com.laodev.applock.base.BaseView;
import com.laodev.applock.model.CommLockInfo;

import java.util.List;


public interface MainContract {
    interface View extends BaseView<Presenter> {
        void loadAppInfoSuccess(List<CommLockInfo> list);
    }

    interface Presenter extends BasePresenter {
        void loadAppInfo(Context context, boolean isSort);

        void loadLockAppInfo(Context context);

        void onDestroy();
    }
}

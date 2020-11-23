package com.laodev.applock.gesture.contract;

import android.content.Context;

import com.laodev.applock.base.BasePresenter;
import com.laodev.applock.base.BaseView;
import com.laodev.applock.model.CommLockInfo;
import com.laodev.applock.gesture.presenter.LockMainPresenter;

import java.util.List;


public interface LockMainContract {
    interface View extends BaseView<Presenter> {

        void loadAppInfoSuccess(List<CommLockInfo> list);
    }

    interface Presenter extends BasePresenter {
        void loadAppInfo(Context context);

        void searchAppInfo(String search, LockMainPresenter.ISearchResultListener listener);

        void onDestroy();
    }
}

package com.laodev.applocker.gesture.contract;

import android.content.Context;

import com.laodev.applocker.base.BasePresenter;
import com.laodev.applocker.base.BaseView;
import com.laodev.applocker.model.CommLockInfo;
import com.laodev.applocker.gesture.presenter.LockMainPresenter;

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

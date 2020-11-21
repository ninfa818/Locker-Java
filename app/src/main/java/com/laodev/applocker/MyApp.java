package com.laodev.applocker;

import com.laodev.applocker.base.BaseActivity;
import com.laodev.applocker.activities.protect.UnlockAppActivity;
import com.laodev.applocker.utils.MainUtil;

import org.litepal.LitePalApplication;

import java.util.ArrayList;
import java.util.List;



public class MyApp extends LitePalApplication {

    private static MyApp application;
    private static List<BaseActivity> activityList;

    public static MyApp getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;


        MainUtil.getInstance().init(application);
        activityList = new ArrayList<>();
    }

    public void doForCreate(BaseActivity activity) {
        activityList.add(activity);
    }

    public void doForFinish(BaseActivity activity) {
        activityList.remove(activity);
    }

    public void clearAllActivity() {
        try {
            for (BaseActivity activity : activityList) {
                if (activity != null && !clearAllWhiteList(activity))
                    activity.clear();
            }
            activityList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean clearAllWhiteList(BaseActivity activity) {
        return activity instanceof UnlockAppActivity;
    }
}

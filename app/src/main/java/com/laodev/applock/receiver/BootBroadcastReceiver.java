package com.laodev.applock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.laodev.applock.base.AppConstants;
import com.laodev.applock.services.BackgroundManager;
import com.laodev.applock.services.LoadAppListService;
import com.laodev.applock.services.LockService;
import com.laodev.applock.utils.LogUtil;
import com.laodev.applock.utils.MainUtil;


public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        LogUtil.i("Boot service....");
        //TODO: pie compatable done
        BackgroundManager.getInstance().init(context).startService(LoadAppListService.class);
        if (MainUtil.getInstance().getBoolean(AppConstants.LOCK_STATE, false)) {
            BackgroundManager.getInstance().init(context).startService(LockService.class);
            BackgroundManager.getInstance().init(context).startAlarmManager();
        }
    }
}

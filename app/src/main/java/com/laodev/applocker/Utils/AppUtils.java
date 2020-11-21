package com.laodev.applocker.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


public class AppUtils {

    public static void hideStatusBar(Window window, boolean enable) {
        WindowManager.LayoutParams p = window.getAttributes();
        if (enable)

            p.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        else

            p.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);

        window.setAttributes(p);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static void autoStart(Context context) {

        if (Build.MANUFACTURER.equalsIgnoreCase("vivo")) {
            autoStartVivo(context);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("oppo")) {
            autoStartOppo(context);
        }


    }

    static void autoStartVivo(Context context) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
            context.startActivity(intent);
            Toast.makeText(context, "Allow permission to locked your apps", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                context.startActivity(intent);
            } catch (Exception ex) {
                try {
                    Intent intent = new Intent();
                    intent.setClassName("com.iqoo.secure",
                            "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager");
                    context.startActivity(intent);
                } catch (Exception exx) {
                    ex.printStackTrace();
                }
            }
        }
        Toast.makeText(context, "Allow permission to locked your apps", Toast.LENGTH_SHORT).show();
    }

    static void autoStartOppo(Context context) {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity");
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent();
                intent.setClassName("com.oppo.safe",
                        "com.oppo.safe.permission.startup.StartupAppListActivity");
                context.startActivity(intent);

            } catch (Exception ex) {
                try {
                    Intent intent = new Intent();
                    intent.setClassName("com.coloros.safecenter",
                            "com.coloros.safecenter.startupapp.StartupAppListActivity");
                    context.startActivity(intent);
                } catch (Exception exx) {

                }
            }
        }
        Toast.makeText(context, "Allow permission to locked your apps", Toast.LENGTH_SHORT).show();
    }


    public static void autoStartMi(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity");
        context.startActivity(intent);
        allPermMi(context);
    }

    public static void allPermMi(Context context){
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        context.startActivity(intent);
    }
}

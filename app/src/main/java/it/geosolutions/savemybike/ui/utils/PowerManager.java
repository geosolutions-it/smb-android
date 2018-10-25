package it.geosolutions.savemybike.ui.utils;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import it.geosolutions.savemybike.R;

public class PowerManager {
    public static List<Intent> POWERMANAGER_INTENTS = Arrays.asList(
            // TODO: review and test this list
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.power.ui.HwPowerManagerActivity")), // Confirmed working
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(android.net.Uri.parse("mobilemanager://function/entry/AutoStart"))
    );

    /**
     * Checks if there is an intent avaialble from the list of supported power managers, then show a dialog with the instructions for the user about how to proceed.
     *
     * @param context
     */
    public static void startPowerSaverIntent(Context context) {
        SharedPreferences settings = context.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean("skipProtectedAppCheck", false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            boolean foundCorrectIntent = false;
            for (Intent intent : POWERMANAGER_INTENTS) {
                if (isCallable(context, intent)) {
                    foundCorrectIntent = true;


                    String message = context.getResources().getString(R.string.no_powersaving_instructions);
                    switch (intent.getComponent().getClassName()) {
                        case "com.huawei.systemmanager.power.ui.HwPowerManagerActivity":
                            message = context.getString(R.string.huauei_emui_powersaving_instructions);
                            break;
                    }
                    CompoundButton.OnCheckedChangeListener dontShowListener = (CompoundButton buttonView, boolean isChecked) -> {
                        editor.putBoolean("skipProtectedAppCheck", isChecked);
                        editor.apply();
                    };
                    View content = getContentView(context, message, dontShowListener);
                    new AlertDialog.Builder(context)
                            .setTitle(Build.MANUFACTURER + " " + context.getResources().getString(R.string.protected_apps))
                            .setView(content)
                            .setPositiveButton(R.string.go_to_settings, (DialogInterface dialog, int which) -> context.startActivity(intent))
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                    break;
                }
            }
            if (!foundCorrectIntent) {
                editor.putBoolean("skipProtectedAppCheck", true);
                editor.apply();
            }
        }
    }

    @NonNull
    private static View getContentView(Context context, String message, CompoundButton.OnCheckedChangeListener dontShowListener) {
        View content = LayoutInflater.from(context).inflate(R.layout.powermanagement_dialog,null);
        AppCompatCheckBox dontShowAgain = content.findViewById(R.id.pm_dialog_dont_show_again);

        dontShowAgain.setOnCheckedChangeListener(dontShowListener);
        TextView description = content.findViewById(R.id.pm_dialog_description);
        description.setText(
                Html.fromHtml(context.getResources().getString(R.string.powersaving_description) )
        );
        TextView instructions = content.findViewById(R.id.pm_dialog_instructions);
        instructions.setText(Html.fromHtml(message));

        // WebView instructions = content.findViewById(R.id.pm_dialog_instructions);
        // instructions.loadData(message, "text/html", "utf-8");
        return content;
    }


    private static boolean isCallable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
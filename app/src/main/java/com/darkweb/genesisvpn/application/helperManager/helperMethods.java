package com.darkweb.genesisvpn.application.helperManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.darkweb.genesisvpn.application.appManager.appListRowModel;
import com.darkweb.genesisvpn.application.constants.strings;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static com.darkweb.genesisvpn.application.constants.strings.SE_UNKNOWN_EXCEPTION;
import static com.darkweb.genesisvpn.application.constants.strings.SE_VPN_PERMISSION;

public class helperMethods
{
    public static String createErrorMessage(VpnException p_exception){
        if(p_exception.getMessage().equals(SE_VPN_PERMISSION)){
            return SE_VPN_PERMISSION;
        }
        else if(p_exception == null || p_exception.getCause() == null || p_exception.getCause().getLocalizedMessage() == null || p_exception.getCause().getLocalizedMessage().equals(strings.EMPTY_STR)){
            return SE_UNKNOWN_EXCEPTION;
        }else {
            return p_exception.getCause().getLocalizedMessage();
        }
    }

    public static float pxToDp(Context p_context,float p_value){
        float dip = p_value;
        Resources r = p_context.getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return px;
    }

    public static void onOpenURL(AppCompatActivity p_context, String p_link){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(p_link));
        p_context.startActivity(browserIntent);
    }

    public static void shareApp(AppCompatActivity p_context) {
        ShareCompat.IntentBuilder.from(p_context)
                .setType(strings.SH_TYPE)
                .setChooserTitle(strings.SH_TITLE)
                .setSubject(strings.SH_SUBJECT)
                .setText(strings.SH_DESC + p_context.getPackageName())
                .startChooser();
    }

    public static void hideKeyboard(AppCompatActivity context) {
        View view = context.findViewById(android.R.id.content);
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void sendEmail(AppCompatActivity p_context)
    {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                strings.EM_MAIL_TO,strings.EM_EMAIL, null));
        intent.setData(Uri.parse(strings.CO_TYPE));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{strings.EM_EMAIL});
        intent.putExtra(Intent.EXTRA_SUBJECT, strings.EM_SUBJECT);
        if (intent.resolveActivity(p_context.getPackageManager()) != null) {
            p_context.startActivity(intent);
        }
    }

    public static void quit(AppCompatActivity activity) {
        activity.finish();
    }

    public static void openActivity( Class<?> p_cls, AppCompatActivity p_context){
        Intent myIntent = new Intent(p_context, p_cls);
        p_context.startActivity(myIntent);
    }

    public static void openActivityWithBundle( Class<?> p_cls, AppCompatActivity p_context, String p_key, Boolean p_value){
        Intent myIntent = new Intent(p_context, p_cls);
        myIntent.putExtra(p_key, p_value);
        p_context.startActivity(myIntent);
    }

    public static int screenWidth()
    {
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;

        if(width>height)
        {
            return height;
        }
        {
            return width;
        }
    }

    public static String convertToCamelHump(String text) {
        return WordUtils.capitalizeFully(text, ' ', '.');
    }

    public static ArrayList<appListRowModel> getSystemInstalledApps(Context p_context){
        ArrayList<appListRowModel> m_list_model = new ArrayList<>();
        List<PackageInfo> packs = p_context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo m_package = packs.get(i);
            if (!m_package.packageName.equals(p_context.getPackageName()) && isSystemPackage(m_package)) {
                try {
                    String appName = convertToCamelHump(m_package.applicationInfo.loadLabel(p_context.getPackageManager()).toString());
                    Drawable icon = m_package.applicationInfo.loadIcon(p_context.getPackageManager());
                    String packages = m_package.applicationInfo.packageName;
                    appListRowModel row_model = new appListRowModel(appName,packages,icon);
                    m_list_model.add(row_model);
                } catch (Exception e) {
                    continue;
                }
            }
        }
        Collections.sort(m_list_model);
        return m_list_model;
    }

    public static ArrayList<appListRowModel> getUserInstalledApps(Context p_context){
        ArrayList<appListRowModel> m_list_model = new ArrayList<>();
        List<PackageInfo> packs = p_context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo m_package = packs.get(i);
            if (!m_package.packageName.equals(p_context.getPackageName()) && !isSystemPackage(m_package)) {
                try {
                    String appName = convertToCamelHump(m_package.applicationInfo.loadLabel(p_context.getPackageManager()).toString());
                    Drawable icon = m_package.applicationInfo.loadIcon(p_context.getPackageManager());
                    String packages = m_package.applicationInfo.packageName;
                    appListRowModel row_model = new appListRowModel(appName,packages,icon);
                    m_list_model.add(row_model);
                } catch (Exception e) {
                    continue;
                }
            }
        }
        Collections.sort(m_list_model);
        return m_list_model;
    }

    public static ArrayList<appListRowModel> getStarredApps(Context p_context){
        ArrayList<appListRowModel> m_list_model = new ArrayList<>();
        List<PackageInfo> packs = p_context.getPackageManager().getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo m_package = packs.get(i);

            if (m_package.requestedPermissions == null)
                continue;
            boolean internetPermission = false;
            for (String permission : m_package.requestedPermissions) {

                if (TextUtils.equals(permission, android.Manifest.permission.INTERNET)) {
                    internetPermission = true;
                    break;
                }
            }

            if (!m_package.packageName.equals(p_context.getPackageName()) && (isSystemPackage(m_package) && (m_package.packageName.equals("com.android.chrome") || m_package.packageName.equals("com.google.android.apps.docs") || m_package.packageName.equals("com.google.android.googlequicksearchbox") || m_package.packageName.equals("com.android.vending") || m_package.packageName.equals("com.google.android.apps.maps") || m_package.packageName.equals("com.android.mms") || m_package.packageName.equals("com.google.android.apps.photos") || m_package.packageName.equals("com.google.android.youtube"))
            || (!isSystemPackage(m_package) && (internetPermission)))) {
                try {
                    String appName = convertToCamelHump(m_package.applicationInfo.loadLabel(p_context.getPackageManager()).toString());
                    Drawable icon = m_package.applicationInfo.loadIcon(p_context.getPackageManager());
                    String packages = m_package.applicationInfo.packageName;
                    appListRowModel row_model = new appListRowModel(appName,packages,icon);
                    m_list_model.add(row_model);
                } catch (Exception e) {
                    continue;
                }
            }
        }
        Collections.sort(m_list_model);
        return m_list_model;
    }


    public static boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

}

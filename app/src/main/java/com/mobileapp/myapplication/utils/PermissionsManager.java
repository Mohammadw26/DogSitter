package com.mobileapp.myapplication.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionsManager {
    public static String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
    };


    public static String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    public static int LOCATION_CODE = 1;
    public static int STORAGE_CODE = 2;

    public static boolean isLocationPermissionsEnabled(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return !(context.checkSelfPermission(LOCATION_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(LOCATION_PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED);
        } else {
            return true;
        }
    }

    public static boolean isStoragePermissionsEnabled(Context context){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return !(context.checkSelfPermission(STORAGE_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(STORAGE_PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED  &&
                    context.checkSelfPermission(STORAGE_PERMISSIONS[2]) != PackageManager.PERMISSION_GRANTED);
        } else {
            return true;
        }

    }

}

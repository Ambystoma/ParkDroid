package com.parkingdroid.parkingdroid;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import java.util.List;


public class Utils {

    public List <String> permissionsNeeded;

    public static void checkAllPermissions(final Context context, final Activity activity){

            // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
            // checking the build version since Context.checkSelfPermission(...) is only available
            // in Marshmallow
            // 2) Always check for permission (even if permission has already been granted)
            // since the user can revoke permissions at any time through Settings

            //List Of permissions
            final String[] permissionsList = {Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.INTERNET};

            //permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // The permission is NOT already granted.
                // Check if the user has been asked about this permission already and denied
                // it. If so, we want to give more explanation about why the permission is needed.
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    // Show our own UI to explain to the user why we need to read the contacts
                    // before actually requesting the permission and showing the default UI
                }

                // Fire off an async request to actually get the permission
                // This will show the standard permission request dialog UI
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_COARSE_REQUEST);
            }*/
        for (final String permission : permissionsList)
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {

                // The permission is NOT already granted.
                // Check if the user has been asked about this permission already and denied
                // it. If so, we want to give more explanation about why the permission is needed.
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    // 1. Instantiate an AlertDialog.Builder with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);


                    if (permission.contains("LOCATION")) {

                        builder.setMessage("We need Location permission, because we want to know where your car is.")
                                .setTitle("Give me permission please!");

                    } else if (permission.contains("INTERNET")) {
                        builder.setMessage("We need Internet permission, because we need to get data from the net, like maps, your profile info, etc")
                                .setTitle("Give me permission please!");
                    }

                    builder.setNegativeButton("Refuse", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            activity.finish();
                        }
                    });
                    builder.setPositiveButton("Other Chance", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Fire off an async request to actually get the permission
                            // This will show the standard permission request dialog UI

                            ActivityCompat.requestPermissions(activity,permissionsList,Constant.LOCATION_FINE_REQUEST);
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else {

                    ActivityCompat.requestPermissions(activity, new String[]{permission}, Constant.LOCATION_FINE_REQUEST);
                }
            }

        }


}

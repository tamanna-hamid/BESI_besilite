package com.uva.inertia.besilite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
/**
 * Created by Ben on 2/27/2016.
 */
public class NetworkErrorHandlers {


    public static Response.ErrorListener toastHandler(final Context context) {


        return new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    String err_msg = new String(error.networkResponse.data);
                    Log.e("ERROR", err_msg);
                    Toast toast = Toast.makeText(context, err_msg, Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                        ConnectivityManager c = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        WifiManager wifiM = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        int state = wifiM.getWifiState();
                        //if our wifi is disabled we should attempt to turn it on
                        if (state == WifiManager.WIFI_STATE_DISABLED || state == WifiManager.WIFI_STATE_DISABLING || state == WifiManager.WIFI_STATE_UNKNOWN) {
                            wifiM.setWifiEnabled(true);
//                            Toast.makeText(context,"Wifi was disabled and has been enabled. Please ensure that Wifi is enabled when using the app. You may need to reload the page",Toast.LENGTH_LONG).show();
                        } else {
                            //if our wifi was actually enabled we should attempt to reconnect
                            wifiM.reconnect();
//                            Toast.makeText(context,"Wifi provided no response. Wifi is reconnecting. There may be a problem with your network. You may need to reload the page",Toast.LENGTH_LONG).show();

                        }
//
                    }
                }
        };
    }
}

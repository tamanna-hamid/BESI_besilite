package com.uva.inertia.besilite;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONObject;

public class besiliteFirebaseInstanceIDService extends FirebaseInstanceIdService {
   @Override
   public void onTokenRefresh() {
       // Get updated InstanceID token.
       String refreshedToken = FirebaseInstanceId.getInstance().getToken();
       Log.d("FIREBASE_BESI", "Refreshed token: " + refreshedToken);

       // If you want to send messages to this application instance or
       // manage this apps subscriptions on the server side, send the
       // Instance ID token to your app server.
       updateFireID(refreshedToken);
   }

   boolean updateFireID(String new_token){

       SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
       RequestQueue netQueue = NetworkSingleton.getInstance(getApplicationContext()).getRequestQueue();

       String base_url = sharedPref.getString("pref_key_base_url", "");
       String update_fire_endpoint = "/api/v1/athena/fireid/smart/";
       String api_token = sharedPref.getString("pref_key_api_token", "");
       JSONObject FireIDObj = new JSONObject();
       try{
           FireIDObj.put("reg_id",new_token);
       } catch (org.json.JSONException e){

       }
       JsonObjectRequestWithToken updateFireIDRequest = new JsonObjectRequestWithToken(
               Request.Method.PUT, base_url+update_fire_endpoint,FireIDObj, api_token,
               new Response.Listener<JSONObject>() {

                   @Override
                   public void onResponse(JSONObject response) {
                       try{
                           int test = response.getInt("id");
                       } catch (org.json.JSONException e){
                       }
                   }
               },NetworkErrorHandlers.toastHandler(getApplicationContext()));

       netQueue.add(updateFireIDRequest);
       return true;
   }
}

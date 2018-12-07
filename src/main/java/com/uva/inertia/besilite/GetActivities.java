package com.uva.inertia.besilite;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

public class GetActivities extends AppCompatActivity {

    //Holds all activites
    ArrayList<String> emotionsList = new ArrayList<String>();

    //Allows us to add more items
    ArrayAdapter<String> adapter;

    SharedPreferences sharedPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_activities);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final ListView mListView = (ListView) findViewById(R.id.activitiesListView);

//      Create our adapter to add items
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                emotionsList);
        mListView.setAdapter(adapter);

        RequestQueue queue = Volley.newRequestQueue(this);
        String base_url = sharedPref.getString("pref_key_base_url","");
        String endpoint ="api/v1/survey/fields/a/?format=json";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, base_url + endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray emotions = (JSONArray) new JSONTokener(response).nextValue();
                            for (int i = 0; i < emotions.length(); i++){
                                JSONObject o = (JSONObject) emotions.get(i);
                                adapter.add(o.getString("pk") + ": " +o.getString("value"));
                            }
                        } catch (org.json.JSONException e){
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
}

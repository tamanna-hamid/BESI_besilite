package com.uva.inertia.besilite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ScheduleActivity extends AppCompatActivity {

    SharedPreferences sharedPref;

    //Holds all activites
    ArrayList<String> ScheduleList = new ArrayList<>();
    ArrayList<Integer> pkList = new ArrayList<>();

    //Allows us to add more items
    ArrayAdapter<String> adapter;


    //Holds all activity name strings
    ArrayList<String> ActivityList = new ArrayList<>();

    Map<String,String> ActivityIndexer = new HashMap<>();
    Map<String,String> ActivityMap = new HashMap<>();
    Map<String,String> TimeMap = new HashMap<>();

    String base_url;
    String endpoint;
    String activityEndpoint;
    String api_token;
    String deploy_id;
    RequestQueue netQueue;
    TextView bundleInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        //Back button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.AppTheme_Theme_Styled_ActionBar_TitleTextStyle);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        netQueue = NetworkSingleton.getInstance(getApplicationContext()).getRequestQueue();


        final ListView mListView = (ListView) findViewById(R.id.scheduleEvents);

        bundleInfo = (TextView) findViewById(R.id.lastBundleInfo);
//      Create our adapter to add items
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ScheduleList);

        mListView.setAdapter(adapter);

        updateEventList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddActivityBundle.class);
                startActivityForResult(intent, 90);
            }
        });

    }

    /* Called when the second activity's finished */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch(requestCode) {
//            case 90:
//                if (resultCode == RESULT_OK) {\
        Log.v("CHECK POSITION", "" + requestCode);
        updateEventList();
//                }
//                break;
//        }
    }

    void updateEventList(){
        bundleInfo.setText("Loading...");
        deploy_id = sharedPref.getString("pref_key_deploy_id","");
        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token","");
        endpoint ="/api/v1/survey/activ/smart/recent/";
        activityEndpoint="/api/v1/survey/fields/smart/a/";

//        Toast toast = Toast.makeText(getApplicationContext(),
//                "New Event", Toast.LENGTH_SHORT);
//        toast.show();

        JsonArrayRequestWithToken activitySurveyRequestArray = new JsonArrayRequestWithToken(base_url+endpoint, api_token, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                adapter.clear();
                try {
                    if (response.length() > 0) {
                        JSONObject bundle = (JSONObject) response.get(response.length()-1);
                        Log.v("TEST", bundle.toString());
                        bundleInfo.setText("Your last activity log:");

                        JSONArray actlist = bundle.getJSONArray("activities");
                        Log.v("TEST", actlist.toString());
                        for (int i = 0; i < actlist.length(); i++) {
                            adapter.add(actlist.getString(i));
                            Log.v("TEST", actlist.getString(i));
                        }

                    } else {
                        bundleInfo.setText("No activity logs found");
                    }
                }
                catch (JSONException e){
                    Log.v("ERROR", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                adapter.clear();
                adapter.add("Unable to connect to server. Please check your internet connection");
            }
        });
        this.netQueue.add(activitySurveyRequestArray);
    }

}

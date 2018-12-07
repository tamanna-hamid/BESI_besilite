package com.uva.inertia.besilite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.snowplowanalytics.snowplow.tracker.Emitter;
import com.snowplowanalytics.snowplow.tracker.Subject;
import com.snowplowanalytics.snowplow.tracker.Tracker;
import com.snowplowanalytics.snowplow.tracker.emitter.BufferOption;
import com.snowplowanalytics.snowplow.tracker.emitter.HttpMethod;
import com.snowplowanalytics.snowplow.tracker.events.ScreenView;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    SharedPreferences sharedPref;

    //Holds all activites
    ArrayList<String> agiSurveyList = new ArrayList<>();

    //Allows us to add more items
    ArrayAdapter<String> adapter;

    String base_url;
    String endpoint;
    String api_token;
    String deploy_id;
    RequestQueue netQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        netQueue = Volley.newRequestQueue(this);

        final ImageView goToEmotions = (ImageView) findViewById(R.id.daily_reports);
        final ImageView goToActivities = (ImageView) findViewById(R.id.go_to_activities);
        final Button goToAgiReports = (Button) findViewById(R.id.go_to_agi_report);

        ////////////////////////Android Analytics Tracking Code////////////////////////////////////
        // Create an Emitter
        Emitter e1 = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getApplicationContext())
                .method(HttpMethod.POST) // Optional - Defines how we send the request
                .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                // Optional - Defines what protocol used to send events
                .build();

        Subject s1 = new Subject.SubjectBuilder().build();
        s1.setUserId(sharedPref.getString("pref_key_api_token", ""));
        // Make and return the Tracker object
        Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "dailyPWDSleepSurvey", "com.uva.inertia.besilite", getApplicationContext())
                .base64(false)
                .subject(s1)
                .build()
        );

        t1.track(ScreenView.builder()
                .name("Home")
                .id("home")
                .build());
        ///////////////////////////////////////////////////////////////////////////////////////////
        goToEmotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////////////////////////Android Analytics Tracking Code////////////////////////////////////
                // Create an Emitter
                Emitter emitter = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getApplicationContext())
                        .method(HttpMethod.POST) // Optional - Defines how we send the request
                        .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                        // Optional - Defines what protocol used to send events
                        .build();

                Subject subject = new Subject.SubjectBuilder().build();
                subject.setUserId(sharedPref.getString("pref_key_api_token", ""));
                // Make and return the Tracker object
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "homeGoToEmotionsButton", "com.uva.inertia.besilite", getApplicationContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("Home -> Emotions")
                        .id("goToEmotionsButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////

                Intent intent = new Intent(v.getContext(), DailySurvey.class);
                startActivity(intent);
            }
        });

        goToAgiReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////////////////////////Android Analytics Tracking Code////////////////////////////////////
                // Create an Emitter
                Emitter emitter = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getApplicationContext())
                        .method(HttpMethod.POST) // Optional - Defines how we send the request
                        .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                        // Optional - Defines what protocol used to send events
                        .build();

                Subject subject = new Subject.SubjectBuilder().build();
                subject.setUserId(sharedPref.getString("pref_key_api_token", ""));
                // Make and return the Tracker object
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "homeGoToAgiReportsButton", "com.uva.inertia.besilite", getApplicationContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("Home -> Agitation Report")
                        .id("goToAgiReportsButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////


                Intent intent = new Intent(v.getContext(), AgitationReports.class);
                startActivity(intent);
            }
        });

        goToActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////////////////////////Android Analytics Tracking Code////////////////////////////////////
                // Create an Emitter
                Emitter emitter = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getApplicationContext())
                        .method(HttpMethod.POST) // Optional - Defines how we send the request
                        .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                        // Optional - Defines what protocol used to send events
                        .build();

                Subject subject = new Subject.SubjectBuilder().build();
                subject.setUserId(sharedPref.getString("pref_key_api_token", ""));
                // Make and return the Tracker object
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "homeGoToActivitiesButton", "com.uva.inertia.besilite", getApplicationContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("Home -> Activities")
                        .id("goToActivitiesButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////

                Intent intent = new Intent(v.getContext(), AddActivityBundle.class);
                startActivity(intent);
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this.getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    void updateAgiList(){
//        deploy_id = sharedPref.getString("pref_key_deploy_id","");
//        base_url = sharedPref.getString("pref_key_base_url", "");
//        api_token = sharedPref.getString("pref_key_api_token","");
//        endpoint ="/api/v1/survey/agi/"+deploy_id+"/";
//
//
//        JsonArrayRequestWithToken agiSurveyRequestArray = new JsonArrayRequestWithToken(base_url+endpoint, api_token, new Response.Listener<JSONArray>() {
//
//            @Override
//            public void onResponse(JSONArray response) {
//                adapter.clear();
//                try {
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject o = (JSONObject) response.get(i);
//                        adapter.add(o.getString("timestamp") + " | Level: " + o.getString("level") + " |  Activity: " + o.getString("activity"));
//                    }
//                } catch (JSONException e){
//                    adapter.add("Server responded with incorrect JSON");
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                adapter.clear();
//                adapter.add("Server responded with error: " + error.getLocalizedMessage());
//
//            }
//        });
//
//        this.netQueue.add(agiSurveyRequestArray);



    }
}

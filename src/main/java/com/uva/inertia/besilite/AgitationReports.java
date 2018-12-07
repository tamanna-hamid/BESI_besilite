package com.uva.inertia.besilite;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.snowplowanalytics.snowplow.tracker.Emitter;
import com.snowplowanalytics.snowplow.tracker.Subject;
import com.snowplowanalytics.snowplow.tracker.Tracker;
import com.snowplowanalytics.snowplow.tracker.emitter.BufferOption;
import com.snowplowanalytics.snowplow.tracker.emitter.HttpMethod;
import com.snowplowanalytics.snowplow.tracker.events.ScreenView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.UUID;

public class AgitationReports extends AppCompatActivity implements ConfirmFragment.OnConfirmClickedListener{
    final int NUMBER_OF_TABS = 5;       //CHANGE THIS VALUE WHEN YOU ADD OR DELETE TABS

    String base_url;
    String api_token;
    String deploy_id;
    String complete_endpoint;
    String EmotionEndpoint;
    String ObservationEndpoint;
    String AgitationEndpoint;
    String NotificationEndpoint;

    SharedPreferences sharedPref;
    RequestQueue netQueue;

    HashMap<String, Boolean> pwdObs;
    HashMap<String, Boolean> pwdEmo;
    HashMap<String, String> pwdGen;
//    HashMap<Integer, Boolean> pwdNotif;
    HashMap<String, Boolean> pwdNotif;

    java.text.DateFormat df;
    TimeZone tz;

    //int agiSurveyPK;
    int obsSurveyPK;
    int emoSurveyPK;
    int notifSurveyPK;

    TabLayout tabLayout;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agitation_reports);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);        // THIS LINE WILL CHANGE THE LEFT-MOST ICON IN THE TOOLBAR.  TOOK LOTS OF GOOGLING: https://stackoverflow.com/questions/9252354/how-to-customize-the-back-button-on-actionbar (answer by hitman snipe) ~jjp5nw


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        netQueue = NetworkSingleton.getInstance(getApplicationContext()).getRequestQueue();


        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token", "");
        deploy_id = sharedPref.getString("pref_key_deploy_id", "");
        complete_endpoint = "/api/v1/survey/agi/smart/";
        ObservationEndpoint = "/api/v1/survey/obs/create/";
        EmotionEndpoint = "/api/v1/survey/emo/create/";
        AgitationEndpoint = "/api/v1/survey/agi/smart/";

        // NEW NOTIFICATION ENDPOINT FOR SENDING INFORMATION TO SERVER
        NotificationEndpoint = "/api/v1/survey/notif/create/";

        ////////////////////////Android Analytics Tracking Code////////////////////////////////////
        // Create an Emitter
        Emitter e1 = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getApplicationContext())
                .method(HttpMethod.POST) // Optional - Defines how we send the request
                .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                .build();

        Subject s1 = new Subject.SubjectBuilder().build();
        s1.setUserId(api_token);

        // Make and return the Tracker object
        Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "agitationReport", "com.uva.inertia.besilite", getApplicationContext())
                .base64(false) // Optional - Defines what protocol used to send events
                .subject(s1)
                .build()
        );

        t1.track(ScreenView.builder()
                .name("Add New Agitation Report")
                .id("addAgitationReport")
                .build());

        ///////////////////////////////////////////////////////////////////////////////////////////


        // Set up the ViewPager with the sections adapter.
        mViewPager = (noSwipeViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // https://stackoverflow.com/questions/28576198/android-tab-fragment-not-calling-oncreateview-switching-from-tab2-to-tab1/28576429
        mViewPager.setOffscreenPageLimit(NUMBER_OF_TABS);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabTextColors(Color.BLACK,Color.BLACK);

        pwdObs = new HashMap<>();
        pwdEmo = new HashMap<>();
        pwdGen = new HashMap<>();
        pwdNotif = new HashMap<>();

        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_agitation_reports, menu);
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
            return true;
        }

        if(id == android.R.id.home) {
            Log.v("agiReport jjp5nw", "home pressed");

            ////////////////////////Android Analytics Tracking Code////////////////////////////////////
            // Create an Emitter
            Emitter e1 = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getApplicationContext())
                    .method(HttpMethod.POST) // Optional - Defines how we send the request
                    .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST\
                    .build();

            Subject s1 = new Subject.SubjectBuilder().build();
            s1.setUserId(sharedPref.getString("pref_key_api_token", ""));

            // Make and return the Tracker object
            Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "addAgiReportGenInfo", "com.uva.inertia.besilite", getApplicationContext())
                    .base64(false) // Optional - Defines what protocol used to send events
                    .subject(s1)
                    .build()
            );

            t1.track(ScreenView.builder()
                    .name("Agitation Report -> Toolbar -> Home")
                    .id("agitationReportHomeButton")
                    .build());

            ///////////////////////////////////////////////////////////////////////////////////////////
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnConfirmClicked() {
        Log.v("jjp5nw", "AgitationReports OnConfirmClicked() called");
        createReport();
    }

    public void createReport(){
        Log.v("jjp5nw", "AgitationReports createReport() called");
        String file_uuid = dumpSurveyToFile();
        createSubsurveys_Notif(file_uuid);
        createSubsurveys_Obs(file_uuid);
        finish();

    }


    private String dumpSurveyToFile(){
        Log.v("jjp5nw", "AgitationReports dumpSurveyToFile() called");
        String uuid = UUID.randomUUID().toString();
        JSONObject surveyDump = new JSONObject();
        JSONObject obsSubsurvey = new JSONObject(pwdObs);
        try {
            //survey data
            surveyDump.put("timestamp", df.format(new Date()));
            surveyDump.put("obsSubsurvey", obsSubsurvey);
            surveyDump.put("agitimestamp", pwdGen.get("agitimestamp"));
            surveyDump.put("level", pwdGen.get("level"));
            surveyDump.put("agiloc",pwdGen.get("agiloc"));

            //file type for parser
            surveyDump.put("filetype", "agiReport");
        } catch (JSONException e){
            Log.e("ERROR", e.getMessage());
        }

        String filename = uuid;
        FileHelpers.writeStringToInternalStorage(surveyDump.toString(),"offline",uuid,getApplicationContext());

        return uuid;
    }

    private void createSubsurveys_Notif(final String file_uuid) {
        Log.v("jjp5nw", "createSubsurveys_Notif(" + file_uuid + ") called");
        JSONObject subsurveyObject = new JSONObject(pwdNotif);
        Log.v("jjp5nw", subsurveyObject.toString());

        JsonObjectRequestWithToken requestNewPWDNotifSub = new JsonObjectRequestWithToken(
                Request.Method.POST, base_url + NotificationEndpoint, subsurveyObject, api_token,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            notifSurveyPK = response.getInt("id");
                            Log.v("jjp5nw", "created notif subsurvey, notifSurveyPK = " + notifSurveyPK);
                        }   catch(org.json.JSONException e) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Server failed to return a PK for notif", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }, NetworkErrorHandlers.toastHandler(getApplicationContext())
        );

        this.netQueue.add(requestNewPWDNotifSub);
    }

    private void createSubsurveys_Obs(final String file_uuid){
        Log.v("jjp5nw", "createSubsurveys_Obs(" + file_uuid + ") called");
        JSONObject subsurveyObject = new JSONObject(pwdObs);
        Log.v("jjp5nw TEST",subsurveyObject.toString());
        JsonObjectRequestWithToken requestNewPWDSleepSub = new JsonObjectRequestWithToken(
                Request.Method.POST, base_url+ObservationEndpoint,subsurveyObject, api_token,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            obsSurveyPK = response.getInt("id");
                             Log.v("jjp5nw obs response","woo created obs subsurvey");
                            createCompleteSurvey(file_uuid);
                        } catch (org.json.JSONException e){
                            Toast toast = Toast.makeText(getApplicationContext(), "Server failed to return a PK for obs", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                },NetworkErrorHandlers.toastHandler(getApplicationContext()));

        this.netQueue.add(requestNewPWDSleepSub);

    }

    private void createCompleteSurvey(final String file_uuid){

            try {
                JSONObject surveyObject = new JSONObject();

                Log.v("jjp5nw MAPS", pwdGen.toString());

                surveyObject.put("notifications", notifSurveyPK);        // added 5/18/2018 jjp5nw
                surveyObject.put("timestamp", df.format(new Date()));
                surveyObject.put("observations", obsSurveyPK);
                surveyObject.put("agitimestamp", pwdGen.get("agitimestamp"));
                surveyObject.put("level", pwdGen.get("level"));
                surveyObject.put("agiloc",pwdGen.get("agiloc"));


                Log.v("jjp5nw surveyObject", surveyObject.toString());
                JsonObjectRequestWithToken postNewAgiSurvey = new JsonObjectRequestWithToken(
                        Request.Method.POST, base_url + AgitationEndpoint, surveyObject, api_token,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "Agitation Report Submitted", Toast.LENGTH_SHORT);
                                    toast.show();
                                    Log.v("jjp5nw complete resp", "full survey made");

                                    FileHelpers.deleteFileFromInternalStorage("offline",file_uuid,getApplicationContext());

                                    finish();
                                } catch (Exception e) {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "Server failed to return a PK for obs",
                                            Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        }, NetworkErrorHandlers.toastHandler(getApplicationContext()) );
                this.netQueue.add(postNewAgiSurvey);
            }
            catch(JSONException e) {
                Log.e("ERROR", e.getMessage());
            }

    }


    void selectPage(int pageIndex){
        tabLayout.setScrollPosition(pageIndex,0f,true);
        mViewPager.setCurrentItem(pageIndex);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.v("jjp5nw", "AgitationReports getItem(" + position + ")");
            switch (position) {
                case 0:
//                    return AgiGenInfoFragment.newInstance();
                    return AgiGenTimeFragment.newInstance();
//                case 1:
//                    return RadioPWDEmotionSubsurveyFragment.newInstance();
                case 1:
                    return AgiGenLvlLocFragment.newInstance();
                case 2:
                    return ObservationSubsurveyFragment.newInstance();
                case 3:
//                    return ConfirmFragment.newInstance(position + 1);
//                    return RadioPWDEmotionSubsurveyFragment.newInstance();
                    return NotificationsFragment.newInstance();
                case 4:
                    return InterventionFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUMBER_OF_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
//                    return "Agitation Time";
                    return "Time";
                //case 1:
                  //  return "EMOTION";
                case 1:
//                    return "Agitation Location";
                    return "Location";
                case 2:
                    return "Observations";
//                    return "Anything I want";
                case 3:
                    return "Notifications";
                case 4:
                    return "Intervention";

                    // ALSO CHANGE NUMBER_OF_TABS int variable to your new value at the top
            }
            return null;
        }
    }
}

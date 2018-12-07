package com.uva.inertia.besilite;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.UUID;


public class DailySurvey extends AppCompatActivity implements ConfirmFragment.OnConfirmClickedListener{

    String base_url;
    String api_token;
    String deploy_id;
    String complete_endpoint;
    String PWDEmotionSubsurvey_endpoint;
    String CaregiverEmotionSubsurvey_endpoint;
    String PWDSleepSubsurvey_endpoint;

    SharedPreferences sharedPref;
    RequestQueue netQueue;

    HashMap<String, Boolean> pwdEmotions;
    HashMap<String, Boolean> caregiverEmotions;
    HashMap<String, Boolean> pwdSleepQal;

    int pwdEmotionSurveyPK;
    int pwdSleepSurveyPK;
    int careEmotionSurveyPK;

    TabLayout tabLayout;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    static HashMap<String, Tracker> trackermap1, trackermap2, trackermap3;
    ////////////////////////////////////////////////////////////////////////////////////////////////

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
    private noSwipeViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_survey);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);        // THIS LINE WILL CHANGE THE LEFT-MOST ICON IN THE TOOLBAR.  TOOK LOTS OF GOOGLING: https://stackoverflow.com/questions/9252354/how-to-customize-the-back-button-on-actionbar (answer by hitman snipe) ~jjp5nw

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        netQueue = NetworkSingleton.getInstance(getApplicationContext()).getRequestQueue();

        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token", "");
        deploy_id = sharedPref.getString("pref_key_deploy_id", "");
        complete_endpoint = "/api/v1/survey/daily/smart/";
        PWDEmotionSubsurvey_endpoint = "/api/v1/survey/emo/create/";
        CaregiverEmotionSubsurvey_endpoint = "/api/v1/survey/care-emo/create/";
        PWDSleepSubsurvey_endpoint = "/api/v1/survey/slp/create/";

        pwdEmotions = new HashMap<>();
        pwdSleepQal = new HashMap<>();
        caregiverEmotions = new HashMap<>();


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (noSwipeViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabTextColors(Color.BLACK,Color.BLACK);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            Log.v("dailySurvey jjp5nw", "home pressed");

            ////////////////////////Android Analytics Tracking Code////////////////////////////////////
            // Create an Emitter
            Emitter e1 = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getApplicationContext())
                    .method(HttpMethod.POST) // Optional - Defines how we send the request
                    .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST\
                    .build();

            Subject s1 = new Subject.SubjectBuilder().build();
            s1.setUserId(sharedPref.getString("pref_key_api_token", ""));

            // Make and return the Tracker object
            Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "dailySurvey", "com.uva.inertia.besilite", getApplicationContext())
                    .base64(false) // Optional - Defines what protocol used to send events
                    .subject(s1)
                    .build()
            );

            t1.track(ScreenView.builder()
                    .name("dailySurvey -> Toolbar -> Home")
                    .id("dailySurveyHomeButton")
                    .build());

            ///////////////////////////////////////////////////////////////////////////////////////////
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnConfirmClicked() {
        createSurveys();
    }

    private String dumpSurveyToFile(){
        String uuid = UUID.randomUUID().toString();
        JSONObject surveyDump = new JSONObject();
        JSONObject pwdEmoSubsurvey = new JSONObject(pwdEmotions);
        JSONObject careEmoSubsurvey = new JSONObject(caregiverEmotions);
        JSONObject pwdSleepSubsurvey = new JSONObject(pwdSleepQal);
        try {
            //survey data
            //get current time in iso8601
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
            df.setTimeZone(tz);
            String timestamp = df.format(new Date());
            surveyDump.put("timestamp", timestamp);
            surveyDump.put("pwdEmoSubsurvey", pwdEmoSubsurvey);
            surveyDump.put("careEmoSubsurvey",careEmoSubsurvey);
            surveyDump.put("pwdSleepSubsurvey", pwdSleepSubsurvey);

            //file type for parser
            surveyDump.put("filetype", "dailyReport");
        } catch (JSONException e){
            Log.e("ERROR", e.getMessage());
        }

        String filename = uuid;
        FileHelpers.writeStringToInternalStorage(surveyDump.toString(),"offline",filename,getApplicationContext());

        return uuid;
    }

    public void createSurveys(){
        String uuid = dumpSurveyToFile();
        createSubsurveys_PWDEmotions(uuid);
        finish();
    }

    private void createSubsurveys_PWDEmotions(final String uuid){
        Log.v("PWD Emotions", pwdEmotions.toString());
        JSONObject subsurveyObject = new JSONObject(pwdEmotions);
        Log.v("TEST",subsurveyObject.toString());
        Log.v("DAILYSURVEY", pwdEmotions.toString());
        JsonObjectRequestWithToken requestNewPWDEmotionSub = new JsonObjectRequestWithToken(
                Request.Method.POST, base_url+PWDEmotionSubsurvey_endpoint,subsurveyObject,
                api_token, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try{
                    pwdEmotionSurveyPK = response.getInt("id");
                    Log.v("TEST","woo created pwd emotions!");
                    createSubsurveys_CaregiverEmotions(uuid);
                } catch (org.json.JSONException e){
                    Toast toast = Toast.makeText(getApplicationContext(), "Server failed to return a PK for PWDEmotions", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }, NetworkErrorHandlers.toastHandler(getApplicationContext()));

        this.netQueue.add(requestNewPWDEmotionSub);
    }

    private void createSubsurveys_CaregiverEmotions(final String uuid){
        JSONObject subsurveyObject = new JSONObject(caregiverEmotions);
        Log.v("TEST",subsurveyObject.toString());
        JsonObjectRequestWithToken requestNewCareEmotionSub = new JsonObjectRequestWithToken(
                Request.Method.POST, base_url+CaregiverEmotionSubsurvey_endpoint,
                subsurveyObject,
                api_token,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try{
                    careEmotionSurveyPK = response.getInt("id");
                    Log.v("TEST","woo created care emotions!");
                    createSubsurveys_PWDSleep(uuid);
                } catch (org.json.JSONException e){
                    Toast toast = Toast.makeText(getApplicationContext(), "Server failed to return a PK for Caregiver Emotions", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        }, NetworkErrorHandlers.toastHandler(getApplicationContext()));

        this.netQueue.add(requestNewCareEmotionSub);
    }

    private void createSubsurveys_PWDSleep(final String uuid){
        JSONObject subsurveyObject = new JSONObject(pwdSleepQal);
        Log.v("TEST",subsurveyObject.toString());
        JsonObjectRequestWithToken requestNewPWDSleepSub = new JsonObjectRequestWithToken(
                Request.Method.POST, base_url+PWDSleepSubsurvey_endpoint,subsurveyObject, api_token,
        new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try{
                    pwdSleepSurveyPK = response.getInt("id");
                    Log.v("TEST","woo created pwd sleep!");
                    createCompleteSurvey(uuid);
                } catch (org.json.JSONException e){
                    Toast toast = Toast.makeText(getApplicationContext(), "Server failed to return a PK for PWDSleep", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }, NetworkErrorHandlers.toastHandler(getApplicationContext()));

        this.netQueue.add(requestNewPWDSleepSub);
    }

    private void createCompleteSurvey(final String file_uuid){
        try{
            JSONObject surveyObject  = new JSONObject();
            //get current time in iso8601
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
            df.setTimeZone(tz);
            String timestamp = df.format(new Date());
            surveyObject.put("timestamp", timestamp);
            surveyObject.put("caregiverEmotions",careEmotionSurveyPK);
            surveyObject.put("PWDEmotions",pwdEmotionSurveyPK);
            surveyObject.put("PWDSleepEvents",pwdSleepSurveyPK);

            Log.v("DAILYSURVEY", pwdEmotions.toString());

            JsonObjectRequestWithToken requestNewCompleteSurvey = new JsonObjectRequestWithToken( Request.Method.POST, base_url+complete_endpoint,surveyObject, api_token, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try{
                        int pk = response.getInt("pk");
                        Log.v("TEST","pk for new complete survey is: "+pk);
                        Toast toast = Toast.makeText(getApplicationContext(), "Daily Report Submitted", Toast.LENGTH_SHORT);
                        toast.show();

                        FileHelpers.deleteFileFromInternalStorage("offline",file_uuid,getApplicationContext());

                        finish();
                    } catch (org.json.JSONException e){
                        Toast toast = Toast.makeText(getApplicationContext(), "Server failed to return a PK for complete survey", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
            }, NetworkErrorHandlers.toastHandler(getApplicationContext()));

            this.netQueue.add(requestNewCompleteSurvey);

        } catch (org.json.JSONException e){
            Log.e("TEST","Something went very wrong creating survey object");
        }


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
            switch (position) {
                case 0:
                    return CaregiverEmotionsFragment.newInstance();
                case 1:
                    return PWDEmotionsFragment.newInstance();
                case 2:
                    return PWDSleepFragment.newInstance();
                //case 3:
                  //  return ConfirmFragment.newInstance(position + 1);
            }
            return null;

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Caregiver";
                case 1:
                    return "Participant Mood";
                case 2:
                    return "Participant Sleep";
                //case 3:
                  //  return "Submit";
            }
            return null;
        }
    }


    public static View.OnClickListener updateMapOnClick(final HashMap<String, Boolean> hm, final String key, final HashMap<String, Tracker> tmap)
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox c = (CheckBox)v;
                hm.put(key,c.isChecked());
                Log.v("DAILYSURVEY","Click handler called from: " + hm.toString());

                /////////////////////////////////ANALYTICS///////////////////////////////////////////
                //tmap.get(key) is the Tracker associated with the check box
                //when toggled, track an event ScreenView (for now, change to Structured event later?)
                tmap.get(key).track(ScreenView.builder()
                        .name(key + " box " + ((c.isChecked()) ? ("checked") : ("unchecked")))
                        .id(key)
                        .build()
                );
                Log.v("ONCLICK", key + " box " + ((c.isChecked()) ? ("checked") : ("unchecked")) + "\n\tTracker object = " + tmap.get(key));
                Log.v("SNOWPLOWTRACKER", "appid=" + tmap.get(key).getAppId() + " namespace=" + tmap.get(key).getNamespace() + " hashCode=" + tmap.get(key).hashCode() + " subject=" + tmap.get(key).getSubject());
                Log.v("ONCLICK", "tmap = " + tmap);
                /////////////////////////////////////////////////////////////////////////////////////
            }
        };
    }

    void selectPage(int pageIndex){
        tabLayout.setScrollPosition(pageIndex,0f,true);
        mViewPager.setCurrentItem(pageIndex);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class CaregiverEmotionsFragment extends Fragment {

        CheckBox Isolated;
        CheckBox Exhausted;
        CheckBox Worried;
        CheckBox Frustrated;
        CheckBox Discouraged;
        CheckBox Rested;
        CheckBox Busy;
        CheckBox HangingInThere;
        CheckBox Okay;
        CheckBox Calm;
        CheckBox Satisfied;
        CheckBox Hopeful;
        CheckBox Motivated;
        CheckBox Confident;
        CheckBox InControl;

        DailySurvey ds;

        Button backButton, nextButton;
        SharedPreferences sharedPref;


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static CaregiverEmotionsFragment newInstance() {
            CaregiverEmotionsFragment fragment = new CaregiverEmotionsFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        public CaregiverEmotionsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_caregiver_emotions_survey,
                    container, false);

            ds = (DailySurvey) getActivity();

            sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            ////////////////////////Android Analytics Tracking Code////////////////////////////////////
            // Create an Emitter
            Emitter e1 = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
                    .method(HttpMethod.POST) // Optional - Defines how we send the request
                    .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                     // Optional - Defines what protocol used to send events
                    .build();

            Subject s1 = new Subject.SubjectBuilder().build();
            s1.setUserId(sharedPref.getString("pref_key_api_token", ""));
            // Make and return the Tracker object
            Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "dailyCareEmoSurvey", "com.uva.inertia.besilite", getContext())
                    .base64(false)
                    .build()
            );

            t1.track(ScreenView.builder()
                    .name("Caregiver Daily Emotion Survey")
                    .id("caregiveremo")
                    .build());

            Log.v("SNOWPLOW1", "t1 = " + t1);
            ///////////////////////////////////////////////////////////////////////////////////////////

            ////////////////////////////////TRACKING CODE FOR CHECKBOXES///////////////////////////////

            // Create an Emitter
            Emitter echeck = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
                    .method(HttpMethod.POST) // Optional - Defines how we send the request
                    .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                    // Optional - Defines what protocol used to send events
                    .build();

            Subject scheck = new Subject.SubjectBuilder().build();
            scheck.setUserId(sharedPref.getString("pref_key_api_token", ""));
            // K is the same key given to cEmo HashMap
            // V is the Tracker class.  At the creation of each checkbox, the associated Tracker is
            // initialized (Tracker.init...)
            trackermap1 = new HashMap<String, Tracker>();

            //on each click, the track event will also be called after the onClick method executes.

            ///////////////////////////////////////////////////////////////////////////////////////////
            String[] keys = {
                    "Isolated",
                    "Exhausted",
                    "Worried",
                    "Frustrated",
                    "Discouraged",
                    "Rested",
                    "Busy",
                    "HangingInThere",
                    "Okay",
                    "Calm",
                    "Satisfied",
                    "Hopeful",
                    "Motivated",
                    "Confident",
                    "InControl"
            };

            int iteratorIndex = -1;

            ///////////////////////////////////////////////////////////////////////////////////////////

            HashMap<String, Boolean> cEmo = ds.caregiverEmotions;

            Isolated = (CheckBox) rootView.findViewById(R.id.checkIsolated);
            Isolated.setOnClickListener(updateMapOnClick(cEmo, keys[++iteratorIndex], trackermap1));

//            trackermap.put(keys[iteratorIndex], Tracker.init(new Tracker.TrackerBuilder(echeck, keys[iteratorIndex], "com.uva.inertia.besilite", getContext()).base64(false).build()));
//            trackermap.put("Isolated")

            /*
            Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "dailyCareEmoSurvey", "com.uva.inertia.besilite", getContext())
                    .base64(false)
                    .build()
            );

            t1.track(ScreenView.builder()
                    .name("Caregiver Daily Emotion Survey")
                    .id("caregiveremo")
                    .build());
            //*/

            Exhausted = (CheckBox) rootView.findViewById(R.id.checkExhausted);
            Exhausted.setOnClickListener(updateMapOnClick(cEmo, keys[++iteratorIndex], trackermap1));

            Worried = (CheckBox) rootView.findViewById(R.id.checkWorried);
            Worried.setOnClickListener(updateMapOnClick(cEmo, keys[++iteratorIndex], trackermap1));

            Frustrated = (CheckBox) rootView.findViewById(R.id.checkFrustrated);
            Frustrated.setOnClickListener(updateMapOnClick(cEmo, keys[++iteratorIndex], trackermap1));

            Discouraged = (CheckBox) rootView.findViewById(R.id.checkDiscouraged);
            Discouraged.setOnClickListener(updateMapOnClick(cEmo, keys[++iteratorIndex], trackermap1));

            Rested = (CheckBox) rootView.findViewById(R.id.checkRested);
            Rested.setOnClickListener(updateMapOnClick(cEmo, keys[++iteratorIndex], trackermap1));

            Busy = (CheckBox) rootView.findViewById(R.id.checkBusy);
            Busy.setOnClickListener(updateMapOnClick(cEmo, keys[++iteratorIndex], trackermap1));

            HangingInThere = (CheckBox) rootView.findViewById(R.id.checkHangingInThere);
            HangingInThere.setOnClickListener(updateMapOnClick(cEmo, keys[++iteratorIndex], trackermap1));

            Okay = (CheckBox) rootView.findViewById(R.id.checkOkay);
            Okay.setOnClickListener(updateMapOnClick(cEmo, keys[++iteratorIndex], trackermap1));

            Calm = (CheckBox) rootView.findViewById(R.id.checkCalm);
            Calm.setOnClickListener(updateMapOnClick(cEmo, keys[++iteratorIndex], trackermap1));

            Satisfied = (CheckBox) rootView.findViewById(R.id.checkSatisfied);
            Satisfied.setOnClickListener(updateMapOnClick(cEmo, keys[++iteratorIndex], trackermap1));

            Hopeful = (CheckBox) rootView.findViewById(R.id.checkHopeful);
            Hopeful.setOnClickListener(updateMapOnClick(cEmo, keys[++iteratorIndex], trackermap1));

            Motivated  = (CheckBox) rootView.findViewById(R.id.checkMotivated );
            Motivated.setOnClickListener(updateMapOnClick(cEmo, keys[++iteratorIndex], trackermap1));

            Confident = (CheckBox) rootView.findViewById(R.id.checkConfident);
            Confident.setOnClickListener(updateMapOnClick(cEmo, keys[++iteratorIndex], trackermap1));

            InControl = (CheckBox) rootView.findViewById(R.id.checkInControl);
            InControl.setOnClickListener(updateMapOnClick(cEmo, keys[++iteratorIndex], trackermap1));

            backButton = (Button) rootView.findViewById(R.id.caregiver_emo_back);
            backButton.setOnClickListener(new View.OnClickListener()    {
                @Override
                public void onClick(View v)
                {
                    ////////////////////////Android Analytics Tracking Code////////////////////////////////////
                    // Create an Emitter
                    Emitter emitter = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
                            .method(HttpMethod.POST) // Optional - Defines how we send the request
                            .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                            // Optional - Defines what protocol used to send events
                            .build();

                    Subject subject = new Subject.SubjectBuilder().build();
                    subject.setUserId(sharedPref.getString("pref_key_api_token", ""));
                    // Make and return the Tracker object
                    Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "dailySurveyCaregiver", "com.uva.inertia.besilite", getContext())
                            .base64(false)
                            .subject(subject)
                            .build()
                    );

                    tracker.track(ScreenView.builder()
                            .name("Daily Report / Caregiver -> Back")
                            .id("dailySurveyCaregiverBackButton")
                            .build());
                    ///////////////////////////////////////////////////////////////////////////////////////////

//                    ((DailySurvey) getActivity()).selectPage(0);
                    ((DailySurvey)getActivity()).onBackPressed();
                }

            });
            nextButton = (Button) rootView.findViewById(R.id.caregiver_emo_next);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ////////////////////////Android Analytics Tracking Code////////////////////////////////////
                    // Create an Emitter
                    Emitter emitter = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
                            .method(HttpMethod.POST) // Optional - Defines how we send the request
                            .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                            // Optional - Defines what protocol used to send events
                            .build();

                    Subject subject = new Subject.SubjectBuilder().build();
                    subject.setUserId(sharedPref.getString("pref_key_api_token", ""));
                    // Make and return the Tracker object
                    Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "dailySurveyCaregiver", "com.uva.inertia.besilite", getContext())
                            .base64(false)
                            .subject(subject)
                            .build()
                    );

                    tracker.track(ScreenView.builder()
                            .name("Daily Report / Caregiver -> Next")
                            .id("dailySurveyCaregiverNextButton")
                            .build());
                    ///////////////////////////////////////////////////////////////////////////////////////////

                    ((DailySurvey) getActivity()).selectPage(1);
                }
            });


            /////////////////////////////////////////////////////////////////////////////////////////
            for(iteratorIndex = 0; iteratorIndex < keys.length; iteratorIndex++)    {
                trackermap1.put(keys[iteratorIndex], Tracker.init(new Tracker.TrackerBuilder(echeck,
                        keys[iteratorIndex], "com.uva.inertia.besilite", getContext())
                        .base64(false)
                        .build())
                );
            }
            /////////////////////////////////////////////////////////////////////////////////////////
            return rootView;
        }
    }

    public static class PWDEmotionsFragment extends Fragment {

        CheckBox ShortTempered;
        CheckBox Tearful;
        CheckBox LackReact;
        CheckBox VeryWorried;
        CheckBox Frightened;
        CheckBox TalkLess;
        CheckBox AppetiteLoss;
        CheckBox LessInterest;
        CheckBox SadExpression;
        CheckBox Suicidal;
        CheckBox TroubleConcentrating;
        CheckBox LackEnergy;
        CheckBox SlowMove;
        CheckBox SlowSpeech;
        CheckBox PhysicalComplaints;
        CheckBox LowEsteem;
        CheckBox WorstAnticipate;
        Button getChecks;
        Button backButton, nextButton;

        HashMap<String, Boolean> hp = new HashMap<>();
        DailySurvey dailysurvey;
        SharedPreferences sharedPref;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PWDEmotionsFragment newInstance() {
            PWDEmotionsFragment fragment = new PWDEmotionsFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        public PWDEmotionsFragment() {
        }

//        public void updateHashMap(){
//            hp.put("ShortTempered", ShortTempered.isChecked());
//            hp.put("Tearfulness",Tearful.isChecked());
//            hp.put("LackReactionToPleasantEvents",LackReact.isChecked());
//            hp.put("VeryWorried",VeryWorried.isChecked());
//            hp.put("Frightened",Frightened.isChecked());
//            hp.put("TalkLess",TalkLess.isChecked());
//            hp.put("AppetiteLoss",AppetiteLoss.isChecked());
//            hp.put("LessIntrest",LessInterest.isChecked());
//            hp.put("SadExpression",SadExpression.isChecked());
//            hp.put("Suicidal",Suicidal.isChecked());
//            hp.put("TroubleConcentrating",TroubleConcentrating.isChecked());
//            hp.put("LackEnergy",LackEnergy.isChecked());
//            hp.put("SlowMovement",SlowMove.isChecked());
//            hp.put("SlowSpeech",SlowSpeech.isChecked());
//            hp.put("PhysicalComplaints", PhysicalComplaints.isChecked());
//            hp.put("LowEsteem",LowEsteem.isChecked());
//            hp.put("WorstAnticipate",WorstAnticipate.isChecked());
//            dailysurvey.pwdEmotions = hp;
//        }

        @Override
        public void onPause(){
            Log.v("PAUSE","paused");
            super.onPause();

        }



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pwd_emotions_survey, container, false);

            sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            ////////////////////////Android Analytics Tracking Code////////////////////////////////////
            // Create an Emitter
            Emitter e1 = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
                    .method(HttpMethod.POST) // Optional - Defines how we send the request
                    .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                    // Optional - Defines what protocol used to send events
                    .build();

            Subject s1 = new Subject.SubjectBuilder().build();
            s1.setUserId(sharedPref.getString("pref_key_api_token", ""));

            // Make and return the Tracker object
            Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "dailyPWDEmoSurvey", "com.uva.inertia.besilite", getContext())
                    .base64(false)
                    .build()
            );

            t1.track(ScreenView.builder()
                    .name("PWD Daily Emotion Survey")
                    .id("PWDemo")
                    .build());
            ///////////////////////////////////////////////////////////////////////////////////////////

            ////////////////////////////////TRACKING CODE FOR CHECKBOXES///////////////////////////////

            // Create an Emitter
            Emitter echeck = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
                    .method(HttpMethod.POST) // Optional - Defines how we send the request
                    .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                    // Optional - Defines what protocol used to send events
                    .build();

            Subject scheck = new Subject.SubjectBuilder().build();
            scheck.setUserId(sharedPref.getString("pref_key_api_token", ""));
            // K is the same key given to cEmo HashMap
            // V is the Tracker class.  At the creation of each checkbox, the associated Tracker is
            // initialized (Tracker.init...)
            trackermap2 = new HashMap<String, Tracker>();

            //on each click, the track event will also be called after the onClick method executes.

            ///////////////////////////////////////////////////////////////////////////////////////////
            String[] keys = {
                    "ShortTemper",
                    "Tearfulness",
                    "LackReactionToPleasantEvents",
                    "Worrying",
                    "Frightened",
                    "TalkLess",
                    "AppetiteLoss",
                    "LossInterestInUsualActivities",
                    "SadExpression",
                    "Suicidal",
                    "TroublePayingAttention",
                    "LackEnergy",
                    "SlowReaction",
                    "SlowSpeech",
                    "Physical",
                    "LowSelfEsteem",
                    "AnticipationOfWorst"
            };

            int iteratorIndex = -1;
            // keys[++iteratorIndex], trackermap));
            ///////////////////////////////////////////////////////////////////////////////////////////

            dailysurvey = (DailySurvey) getActivity();

            ShortTempered = (CheckBox)rootView.findViewById(R.id.checkShortTempered);
            ShortTempered.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            Tearful = (CheckBox)rootView.findViewById(R.id.checkTearfulness);
            Tearful.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            LackReact = (CheckBox)rootView.findViewById(R.id.checkLackOfReact);
            LackReact.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            VeryWorried = (CheckBox)rootView.findViewById(R.id.checkVeryWorried);
            VeryWorried.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            Frightened = (CheckBox)rootView.findViewById(R.id.checkFrightened);
            Frightened.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            TalkLess = (CheckBox)rootView.findViewById(R.id.checkLessTalk);
            TalkLess.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            AppetiteLoss = (CheckBox)rootView.findViewById(R.id.checkAppetiteLoss);
            AppetiteLoss.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            LessInterest = (CheckBox)rootView.findViewById(R.id.checkLessInterest);
            LessInterest.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            SadExpression = (CheckBox)rootView.findViewById(R.id.checkSadExpression);
            SadExpression.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            Suicidal = (CheckBox)rootView.findViewById(R.id.checkSuicidal);
            Suicidal.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            TroubleConcentrating = (CheckBox)rootView.findViewById(R.id.checkTroubleConcen);
            TroubleConcentrating.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            LackEnergy = (CheckBox)rootView.findViewById(R.id.checkLackEnergy);
            LackEnergy.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            SlowMove = (CheckBox)rootView.findViewById(R.id.checkSlowMove);
            SlowMove.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            SlowSpeech =(CheckBox)rootView.findViewById(R.id.checkSlowSpeech);
            SlowSpeech.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            PhysicalComplaints =  (CheckBox)rootView.findViewById(R.id.checkPhysicalComplaints);
            PhysicalComplaints.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            LowEsteem =(CheckBox)rootView.findViewById(R.id.checkLowEsteem);
            LowEsteem.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            WorstAnticipate = (CheckBox)rootView.findViewById(R.id.checkWorstAnticipate);
            WorstAnticipate.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, keys[++iteratorIndex], trackermap2));

            backButton = (Button) rootView.findViewById(R.id.pwd_mood_back);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ////////////////////////Android Analytics Tracking Code////////////////////////////////////
                    // Create an Emitter
                    Emitter emitter = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
                            .method(HttpMethod.POST) // Optional - Defines how we send the request
                            .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                            // Optional - Defines what protocol used to send events
                            .build();

                    Subject subject = new Subject.SubjectBuilder().build();
                    subject.setUserId(sharedPref.getString("pref_key_api_token", ""));
                    // Make and return the Tracker object
                    Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "dailySurveyPM", "com.uva.inertia.besilite", getContext())
                            .base64(false)
                            .subject(subject)
                            .build()
                    );

                    tracker.track(ScreenView.builder()
                            .name("Daily Report / Participant Mood -> Back")
                            .id("dailySurveyPMBackButton")
                            .build());
                    ///////////////////////////////////////////////////////////////////////////////////////////

                    ((DailySurvey) getActivity()).selectPage(0);
//                    Log.v("DAILYSURVEY", ((DailySurvey) getActivity()).pwdEmotions.toString());
                }
            });

            nextButton = (Button) rootView.findViewById(R.id.pwd_mood_next);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ////////////////////////Android Analytics Tracking Code////////////////////////////////////
                    // Create an Emitter
                    Emitter emitter = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
                            .method(HttpMethod.POST) // Optional - Defines how we send the request
                            .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                            // Optional - Defines what protocol used to send events
                            .build();

                    Subject subject = new Subject.SubjectBuilder().build();
                    subject.setUserId(sharedPref.getString("pref_key_api_token", ""));
                    // Make and return the Tracker object
                    Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "dailySurveyPM", "com.uva.inertia.besilite", getContext())
                            .base64(false)
                            .subject(subject)
                            .build()
                    );

                    tracker.track(ScreenView.builder()
                            .name("Daily Report / Participant Mood -> Next")
                            .id("dailySurveyPMNextButton")
                            .build());
                    ///////////////////////////////////////////////////////////////////////////////////////////

                    ((DailySurvey) getActivity()).selectPage(2);
                    Log.v("DAILYSURVEY", ((DailySurvey) getActivity()).pwdEmotions.toString());
                }
            });


            /////////////////////////////////////////////////////////////////////////////////////////
            for(iteratorIndex = 0; iteratorIndex < keys.length; iteratorIndex++)    {
                trackermap2.put(keys[iteratorIndex], Tracker.init(new Tracker.TrackerBuilder(echeck,
                        keys[iteratorIndex], "com.uva.inertia.besilite", getContext())
                        .base64(false)
                        .build())
                );
            }
            /////////////////////////////////////////////////////////////////////////////////////////


            return rootView;

        }

    }

    public static class PWDSleepFragment extends Fragment {

        DailySurvey ds;

        CheckBox multiBathroom;
        CheckBox badDreams;
        CheckBox moreNaps;
        CheckBox diffFallingAsleep;
        CheckBox wakeUpFreq;
        CheckBox wakeUpEarly;
        CheckBox restlessOveractive;
        Button backButton, submitButton;
        SharedPreferences sharedPref;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         *
         */
        public static PWDSleepFragment newInstance() {
            PWDSleepFragment fragment = new PWDSleepFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        public PWDSleepFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pwd_sleep_survey, container, false);

            sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            ////////////////////////Android Analytics Tracking Code////////////////////////////////////
            // Create an Emitter
            Emitter e1 = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
                    .method(HttpMethod.POST) // Optional - Defines how we send the request
                    .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                    // Optional - Defines what protocol used to send events
                    .build();

            Subject s1 = new Subject.SubjectBuilder().build();
            s1.setUserId(sharedPref.getString("pref_key_api_token", ""));
            // Make and return the Tracker object
            Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "dailyPWDSleepSurvey", "com.uva.inertia.besilite", getContext())
                    .base64(false)
                    .build()
            );

            t1.track(ScreenView.builder()
                    .name("PWD Daily Sleep Survey")
                    .id("PWDsleep")
                    .build());
            ///////////////////////////////////////////////////////////////////////////////////////////

            ////////////////////////////////TRACKING CODE FOR CHECKBOXES///////////////////////////////

            // Create an Emitter
            Emitter echeck = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
                    .method(HttpMethod.POST) // Optional - Defines how we send the request
                    .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                    // Optional - Defines what protocol used to send events
                    .build();

            Subject scheck = new Subject.SubjectBuilder().build();
            scheck.setUserId(sharedPref.getString("pref_key_api_token", ""));
            // K is the same key given to cEmo HashMap
            // V is the Tracker class.  At the creation of each checkbox, the associated Tracker is
            // initialized (Tracker.init...)
            trackermap3 = new HashMap<String, Tracker>();

            //on each click, the track event will also be called after the onClick method executes.

            ///////////////////////////////////////////////////////////////////////////////////////////
            String[] keys = {
                    "MultipleBathroomVisits",
                    "BadDreams",
                    "MoreNaps",
                    "DifficultyFallingAsleep",
                    "WakeUpFrequently",
                    "WakeUpEarly",
                    "RestlessOveractive"
            };

            int iteratorIndex = -1;
            // keys[++iteratorIndex], trackermap));
            //copy paste above line to replace the text after slpQ,
            ///////////////////////////////////////////////////////////////////////////////////////////


            ds = (DailySurvey) getActivity();

            Log.v("DAILYSURVEY", ds.pwdEmotions.toString());

            HashMap<String, Boolean> slpQ = ds.pwdSleepQal;

            multiBathroom =(CheckBox)rootView.findViewById(R.id.checkMultipleBathroomVisits);
            multiBathroom.setOnClickListener(updateMapOnClick(slpQ, keys[++iteratorIndex], trackermap3));

            badDreams =(CheckBox)rootView.findViewById(R.id.checkBadDreams);
            badDreams.setOnClickListener(updateMapOnClick(slpQ, keys[++iteratorIndex], trackermap3));

            moreNaps =(CheckBox)rootView.findViewById(R.id.checkMoreNaps);
            moreNaps.setOnClickListener(updateMapOnClick(slpQ, keys[++iteratorIndex], trackermap3));

            diffFallingAsleep =(CheckBox)rootView.findViewById(R.id.checkDifficultyFallingAsleep);
            diffFallingAsleep.setOnClickListener(updateMapOnClick(slpQ, keys[++iteratorIndex], trackermap3));

            wakeUpFreq =(CheckBox)rootView.findViewById(R.id.checkWakeUpFrequently);
            wakeUpFreq.setOnClickListener(updateMapOnClick(slpQ, keys[++iteratorIndex], trackermap3));

            wakeUpEarly =(CheckBox)rootView.findViewById(R.id.checkWakeUpEarly);
            wakeUpEarly.setOnClickListener(updateMapOnClick(slpQ, keys[++iteratorIndex], trackermap3));

            restlessOveractive =(CheckBox)rootView.findViewById(R.id.checkRestlessOveractive);
            restlessOveractive.setOnClickListener(updateMapOnClick(slpQ, keys[++iteratorIndex], trackermap3));

            backButton = (Button) rootView.findViewById(R.id.daily_survey_back);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ////////////////////////Android Analytics Tracking Code////////////////////////////////////
                    // Create an Emitter
                    Emitter emitter = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
                            .method(HttpMethod.POST) // Optional - Defines how we send the request
                            .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                            // Optional - Defines what protocol used to send events
                            .build();

                    Subject subject = new Subject.SubjectBuilder().build();
                    subject.setUserId(sharedPref.getString("pref_key_api_token", ""));
                    // Make and return the Tracker object
                    Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "dailySurveyPS", "com.uva.inertia.besilite", getContext())
                            .base64(false)
                            .subject(subject)
                            .build()
                    );

                    tracker.track(ScreenView.builder()
                            .name("Daily Report / Participant Sleep -> Back")
                            .id("dailySurveyPSBackButton")
                            .build());
                    ///////////////////////////////////////////////////////////////////////////////////////////


                    ((DailySurvey) getActivity()).selectPage(1);
                }
            });

            submitButton = (Button) rootView.findViewById(R.id.daily_survey_submit);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ////////////////////////Android Analytics Tracking Code////////////////////////////////////
                    // Create an Emitter
                    Emitter emitter = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
                            .method(HttpMethod.POST) // Optional - Defines how we send the request
                            .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                            // Optional - Defines what protocol used to send events
                            .build();

                    Subject subject = new Subject.SubjectBuilder().build();
                    subject.setUserId(sharedPref.getString("pref_key_api_token", ""));
                    // Make and return the Tracker object
                    Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "dailySurveyPS", "com.uva.inertia.besilite", getContext())
                            .base64(false)
                            .subject(subject)
                            .build()
                    );

                    tracker.track(ScreenView.builder()
                            .name("Daily Report / Participant Sleep -> Submit")
                            .id("dailySurveyPSSubmitButton")
                            .build());
                    ///////////////////////////////////////////////////////////////////////////////////////////


                    ((DailySurvey) getActivity()).createSurveys();
                }
            });


            /////////////////////////////////////////////////////////////////////////////////////////
            for(iteratorIndex = 0; iteratorIndex < keys.length; iteratorIndex++)    {
//                trackermap.put(keys[iteratorIndex], Tracker.init(new Tracker.TrackerBuilder(echeck,
//                        keys[iteratorIndex], "com.uva.inertia.besilite", getContext())
//                        .base64(false)
//                        .build())
//                );
                Emitter etemp = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext()).build();
                Subject stemp = new Subject.SubjectBuilder().context(getContext()).build();
                Tracker temp = Tracker.init(new Tracker.TrackerBuilder(etemp, keys[iteratorIndex], "com.uva.inertia.besilite", getContext()).subject(stemp).base64(false).build());
                Log.v("TRACKERINIT", iteratorIndex + ": temp=" + temp + "  key=" + keys[iteratorIndex]);
                trackermap3.put(keys[iteratorIndex], temp);
            }
            /////////////////////////////////////////////////////////////////////////////////////////


            return rootView;
        }
    }



}


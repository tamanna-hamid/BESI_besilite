package com.uva.inertia.besilite;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.UUID;

import com.snowplowanalytics.snowplow.tracker.*;
import com.snowplowanalytics.snowplow.tracker.emitter.BufferOption;
import com.snowplowanalytics.snowplow.tracker.emitter.HttpMethod;
import com.snowplowanalytics.snowplow.tracker.emitter.RequestSecurity;
import com.snowplowanalytics.snowplow.tracker.events.ScreenView;

public class AddActivityBundle extends AppCompatActivity{

    Button back;
    Button addNew;
    Button submit;

    SharedPreferences sharedPref;

    //Allows us to add more items
    CustomAdapter adapter;
    ArrayList<String> tempList;
    //Holds all activity name strings
    CaseInsensitiveArrayList ActivityList;
    ArrayList<CheckboxListViewItem> ConvertedList;
    ArrayList<CheckboxListViewItem> ConvertedListCopy;
//    ArrayList<Integer> checkedItems;\

    Map<Integer, Boolean> checkedItems;

    Map<String,String> ActivityMap = new HashMap<>();
    Map<String,String> RevActivityMap = new HashMap<>();
    String selectedActivity;

    String base_url;
    String endpoint;
    String activityEndpoint;
    String api_token;
    String deploy_id;
    RequestQueue netQueue;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity_bundle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Activity Log");
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.AppTheme_Theme_Styled_ActionBar_TitleTextStyle);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);        // THIS LINE WILL CHANGE THE LEFT-MOST ICON IN THE TOOLBAR.  TOOK LOTS OF GOOGLING: https://stackoverflow.com/questions/9252354/how-to-customize-the-back-button-on-actionbar (answer by hitman snipe) ~jjp5nw


        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        netQueue = NetworkSingleton.getInstance(getApplicationContext()).getRequestQueue();

        ////////////////////////Android Analytics Tracking Code////////////////////////////////////
        // Create an Emitter
        Emitter e1 = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getApplicationContext())
                .method(HttpMethod.POST) // Optional - Defines how we send the request
                .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST

                .build();


        Subject s1 = new Subject.SubjectBuilder().build();
        s1.setUserId(api_token);

        // Make and return the Tracker object
        Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "addActivityPageTracker", "com.uva.inertia.besilite", getApplicationContext())
                .base64(false) // Optional - Defines what protocol used to send events
                .subject(s1)
                .build()
        );

        t1.track(ScreenView.builder()
                .name("Add Activity Page")
                .id("addActivityBundle")
                .build());
        ///////////////////////////////////////////////////////////////////////////////////////////

        back = (Button)findViewById(R.id.back_activity_button);
        addNew = (Button)findViewById(R.id.add_new_activity_button);
        submit = (Button)findViewById(R.id.submit_activity_bundle);

        back.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v)
            {
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
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "backFromActivitiesButton", "com.uva.inertia.besilite", getApplicationContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("AddActivityBundle -> Back")
                        .id("backFromActivitiesButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////
                (AddActivityBundle.this).onBackPressed();
            }
        });


        addNew.setOnClickListener(new View.OnClickListener() {
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
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "addNewActivityButton", "com.uva.inertia.besilite", getApplicationContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("AddActivityBundle -> Add new activity")
                        .id("addNewActivityButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////
                checkedItems = getCheckedActivityMap();
                Log.v("addnew jjp5nw", "checkedItems = " + checkedItems.toString());
                AddNewActivityDialogFrag newAct = AddNewActivityDialogFrag.newInstance("newAct");
                newAct.show(getFragmentManager(), "fragment_add_activity");
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
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
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "submitActivitiesBundleButton", "com.uva.inertia.besilite", getApplicationContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("AddActivityBundle -> Submit")
                        .id("submitActivitiesBundleButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////


                ArrayList<Integer> arr = getCheckedActivityList();
                if (arr.size() > 0)
                    submitBundle(arr);
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "No activities selected", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        final ListView mListView = (ListView) findViewById(R.id.activity_type_listview);
        tempList = new ArrayList<>();

//      Create our adapter to add items
        ActivityList  = new CaseInsensitiveArrayList();
        ConvertedList = generateCheckboxes(ActivityList);
        adapter       = new CustomAdapter(this, ConvertedList);
        Log.v("WORK", adapter.toString());
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        mListView.setSelector(R.color.pressed_color);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                mListView.setItemChecked(position, true);
                selectedActivity = RevActivityMap.get(adapter.getItem(position));
            }
        });

        mListView.setAdapter(adapter);
        getActivityList();

        checkedItems = getCheckedActivityMap();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            Log.v("activityLog jjp5nw", "home pressed");

            ////////////////////////Android Analytics Tracking Code////////////////////////////////////
            // Create an Emitter
            Emitter e1 = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getApplicationContext())
                    .method(HttpMethod.POST) // Optional - Defines how we send the request
                    .option(BufferOption.Single) // Optional - x`Defines how many events we bundle in a POST\
                    .build();

            Subject s1 = new Subject.SubjectBuilder().build();
            s1.setUserId(sharedPref.getString("pref_key_api_token", ""));

            // Make and return the Tracker object
            Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "activityLog", "com.uva.inertia.besilite", getApplicationContext())
                    .base64(false) // Optional - Defines what protocol used to send events
                    .subject(s1)
                    .build()
            );

            t1.track(ScreenView.builder()
                    .name("activityLog -> Toolbar -> Home")
                    .id("activityLogHomeButton")
                    .build());

            ///////////////////////////////////////////////////////////////////////////////////////////
        }

        return super.onOptionsItemSelected(item);
    }



    public ArrayList<CheckboxListViewItem> generateCheckboxes(CaseInsensitiveArrayList ar){
        ArrayList<CheckboxListViewItem> ret = new ArrayList<>();
        for (String s: ar){
            ret.add(new CheckboxListViewItem(s, 0, -1));
        }
        return ret;
    }

    public void onFinishNewActDialog(String temp){
        if (ActivityList.contains(temp))    {
//        if(adapter.getPosition(new CheckboxListViewItem()))
            Context context = getApplicationContext();
            CharSequence text = "This field already exists";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else if (temp.trim().length() == 0){
            Context context = getApplicationContext();
            CharSequence text = "There is nothing typed in";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else{
            submitNewActivity(temp);
        }
        Log.v("ACTIVITY", temp);
    }



    void submitNewActivity(String newActivity){
        endpoint = "/api/v1/survey/fields/smart/a/";
        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token","");
        try{
            JSONObject surveyObject  = new JSONObject();
            //get current time in iso8601
            TimeZone tz = TimeZone.getTimeZone("UTC");
            java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
            df.setTimeZone(tz);
            String timestamp = df.format(new Date());
            surveyObject.put("timestamp", timestamp);
            surveyObject.put("value", newActivity);

            JsonObjectRequestWithToken requestNewActivitySurvey =
                    new JsonObjectRequestWithToken(
                            Request.Method.POST,
                            base_url+endpoint,
                            surveyObject, api_token, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                int pk = response.getInt("pk");
                                Log.v("TEST","pk for new complete survey is: "+pk);
                                getActivityList();
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "New Activity Added", Toast.LENGTH_SHORT);
                                toast.show();
                                //newActivVal.setText("");
                                //finish();
                            } catch (org.json.JSONException e){
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Server failed to return a PK for complete survey",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }, NetworkErrorHandlers.toastHandler(getApplicationContext()));

            this.netQueue.add(requestNewActivitySurvey);

        } catch (org.json.JSONException e){
            Log.e("TEST","Something went very wrong creating survey object");
        }
    }

    void getActivityList(){
        deploy_id = sharedPref.getString("pref_key_deploy_id","");
        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token","");
        activityEndpoint="/api/v1/survey/fields/smart/a/";

        adapter.clear();
        tempList.clear();
        ConvertedList.clear();

        JsonArrayRequestWithToken activityListRequestArray = new JsonArrayRequestWithToken(base_url + activityEndpoint, api_token, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray resp) {
                Log.v("Test", resp.toString());
                buildCheckBoxList(resp);
                FileHelpers.writeStringToInternalStorage(resp.toString(), "cache", "activityCache", getApplicationContext());
                     }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String cachedJson = FileHelpers.readStringFromInternalStorage("cache","activityCache",getApplicationContext());
                try {
                    JSONArray jArray = new JSONArray(cachedJson);
                    buildCheckBoxList(jArray);
                    addNew.setEnabled(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        this.netQueue.add(activityListRequestArray);
    }

    void buildCheckBoxList(JSONArray jArray){
        int pk = -314;
        try {
//            checkedItems = getCheckedActivityMap();

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject o = (JSONObject) jArray.get(i);
                ActivityMap.put(o.getString("pk"), o.getString("value"));
                RevActivityMap.put(o.getString("value"), o.getString("pk"));
                tempList.add(o.getString("value"));
            }

            Collections.sort(tempList, String.CASE_INSENSITIVE_ORDER);
            Log.v("MAPS", tempList.toString());
//            for(String s : tempList)    {
//
//            }
            for (String s : tempList) {
                pk = Integer.parseInt(RevActivityMap.get(s));
                CheckboxListViewItem c;
                try {
                    Log.v("bcbl new jjp5nw", "checkedItems = " + checkedItems.toString());
                    c = new CheckboxListViewItem(s, (checkedItems.get(pk) ? 1 : 0), pk);
                }   catch(Exception e)  {
                    c = new CheckboxListViewItem(s, 0, pk);
                }
//                CheckboxListViewItem c = new CheckboxListViewItem(s, 0, pk);

                adapter.add(c);
                ActivityList.add(s);        // added for duplicate checking in onFinishNewDialog
            }
        } catch (JSONException e) {
            Log.e("ERROR", "Server responded with incorrect JSON");
        } catch (Exception e)   {
            Log.v("bcbl jjp5nw", "pk = " + pk);
            Log.v("bcbl jjp5nw", "checkedItems = " + checkedItems.toString());
        }
    }

    void submitBundle(ArrayList<Integer> pks){
        checkedItems = new TreeMap<Integer, Boolean>();

       // dumpBundle(pks);
        createNewBundle(pks);
        finish();

    }

    void submitBundleMemberList(ArrayList<Integer> pks, int newBundlePK, final String file_uuid){
        deploy_id = sharedPref.getString("pref_key_deploy_id","");
        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token","");
        endpoint="/api/v1/survey/activ/memb/smart/create/";

        JSONArray bundleMemberList = new JSONArray();
        for (int pk: pks){
            JSONObject newBundleMember = new JSONObject();
            try {
                newBundleMember.put("bundle", newBundlePK);
                newBundleMember.put("activity",pk);
                bundleMemberList.put(newBundleMember);
            } catch (JSONException e){
                //meh
            }
        }
        JsonArrayRequestWithTokenViaString submitBundleList =
                new JsonArrayRequestWithTokenViaString(
                        Request.Method.POST,
                        base_url+endpoint,
                        bundleMemberList.toString(), api_token, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Activity Log Submitted", Toast.LENGTH_SHORT);
                            toast.show();
                            //newActivVal.setText("");
                            FileHelpers.deleteFileFromInternalStorage("offline",file_uuid,getApplicationContext());
                            finish();

                    }
                }, NetworkErrorHandlers.toastHandler(getApplicationContext()));

        this.netQueue.add(submitBundleList);


    }

    void createNewBundle(ArrayList<Integer> pks){

        deploy_id = sharedPref.getString("pref_key_deploy_id","");
        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token","");
        endpoint="/api/v1/survey/activ/smart/";

        final String uuid = UUID.randomUUID().toString();

        final ArrayList<Integer> pks_final = pks;
        //stuff ....

            JSONObject postObject = new JSONObject();
            try{
                TimeZone tz = TimeZone.getTimeZone("UTC");
                java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
                df.setTimeZone(tz);
                String timestamp = df.format(new Date());
                postObject.put("timestamp", timestamp);
            } catch (JSONException e){
                Log.e("ERROR", e.getMessage());
            }
            Log.v("TEST",postObject.toString());

        try {
            JSONObject postDump = new JSONObject(postObject.toString());
            postDump.put("filetype","activityBundle");
            JSONArray ja = new JSONArray();
            for (int pk: pks){
                JSONObject newBundleMember = new JSONObject();
                try {
                    newBundleMember.put("bundle", "offline-pk");
                    newBundleMember.put("activity",pk);
                    ja.put(newBundleMember);
                } catch (JSONException e){
                    //meh
                }
            }
            postDump.put("activities",ja);

            FileHelpers.writeStringToInternalStorage(postDump.toString(),"offline",uuid,getApplicationContext());

        } catch (JSONException e) {
            e.printStackTrace();
        }



            JsonObjectRequestWithToken requestNewBundle = new JsonObjectRequestWithToken( Request.Method.POST, base_url+endpoint,postObject, api_token, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try{
                        int newBundlePK = response.getInt("pk");
                        Log.v("TEST","woo created new bundle!");
                        submitBundleMemberList(pks_final, newBundlePK, uuid);
                    } catch (org.json.JSONException e){
                        Toast toast = Toast.makeText(getApplicationContext(), "Server failed to return a PK for new bundle", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }, NetworkErrorHandlers.toastHandler(getApplicationContext()));

            this.netQueue.add(requestNewBundle);

    }

    public ArrayList<Integer> getCheckedActivityList(){
        ArrayList<Integer> ret = new ArrayList<>();

        for (int i = 0; i < adapter.getCount(); i++){
            CheckboxListViewItem c = adapter.getItem(i);
            if (c.getValue() == 1){
                ret.add(c.getPK());
            }
        }

        return ret;
    }

    public Map<Integer, Boolean> getCheckedActivityMap()
    {
        Map<Integer, Boolean> ret = new TreeMap<Integer, Boolean>();

        for(int i = 0; i < adapter.getCount(); i++) {
            CheckboxListViewItem c = adapter.getItem(i);
            ret.put(c.getPK(), ((c.getValue() == 1) ? (true) : (false)));
        }

        return ret;
    }
}
package com.uva.inertia.besilite;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

//test of new branch phase3a-dev
public class AddNewActivity extends AppCompatActivity {

    java.text.DateFormat[] dateFormats;

    Calendar calendar;
    Date date;
    TimeZone tz;

    SharedPreferences sharedPref;

    //Allows us to add more items
    ArrayAdapter<String> adapter;
    ArrayList<String> tempList;
    //Holds all activity name strings
    CaseInsensitiveArrayList ActivityList = new CaseInsensitiveArrayList();

    Map<String,String> ActivityMap = new HashMap<>();
    Map<String,String> RevActivityMap = new HashMap<>();
    String selectedActivity;

    String base_url;
    String endpoint;
    String activityEndpoint;
    String api_token;
    String deploy_id;
    RequestQueue netQueue;

    TextView selDate;
    TextView selTime;
    EditText newActivVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

//        Enable back button, why not there????
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        Log.v("WHY", ""+(getSupportActionBar() != null));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);        // THIS LINE WILL CHANGE THE LEFT-MOST ICON IN THE TOOLBAR.  TOOK LOTS OF GOOGLING: https://stackoverflow.com/questions/9252354/how-to-customize-the-back-button-on-actionbar (answer by hitman snipe) ~jjp5nw

        tz = TimeZone.getTimeZone("UTC");

        calendar = new GregorianCalendar();
        date = new Date();
        calendar.setTime(date);
        dateFormats = new java.text.DateFormat[] {
                java.text.DateFormat.getDateInstance(),
                java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT),
        };
        dateFormats[0].setTimeZone(TimeZone.getDefault());
        dateFormats[1].setTimeZone(TimeZone.getDefault());

        selDate = (TextView) findViewById(R.id.new_activity_date);
        selTime = (TextView) findViewById(R.id.new_activity_time);
        selDate.setText(dateFormats[0].format(date));
        selTime.setText(dateFormats[1].format(date));

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        netQueue = NetworkSingleton.getInstance(getApplicationContext()).getRequestQueue();


        newActivVal = (EditText) findViewById(R.id.new_activity_val);
        Button addNewActivity = (Button) findViewById(R.id.add_new_activity);
        addNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("BUTTON PRESS", "addNewActivity clicked");
                String temp = newActivVal.getText().toString();
                if (ActivityList.contains(temp)){
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
            }
        });

        final ListView mListView = (ListView) findViewById(R.id.actionList);
        tempList = new ArrayList<>();
//      Create our adapter to add items
        adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ActivityList);

        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mListView.setSelector(R.color.pressed_color);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                view.setSelected(true);
                selectedActivity = RevActivityMap.get(adapter.getItem(position));
            }
        });

        mListView.setAdapter(adapter);
        getActivityList();


        final Button submitActivityReport = (Button)findViewById(R.id.submit_activity_report);
        submitActivityReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (selectedActivity.trim().isEmpty()){
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "No activity selected",
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {
                        submitFullActivityReport(calendar);
                    }
                } catch (NullPointerException e){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "No activity selected",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    void submitFullActivityReport(Calendar cal) {
        endpoint = "/api/v1/survey/activ/smart/";
        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token","");
        try{
            JSONObject surveyObject = new JSONObject();
            //get current time in iso8601
            java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
            df.setTimeZone(tz);
            String timestamp = df.format(new Date());
            surveyObject.put("timestamp", timestamp);
            surveyObject.put("acttimestamp", df.format(cal.getTime()));
            surveyObject.put("activity", selectedActivity);

            JsonObjectRequestWithToken requestNewCompleteSurvey =
                    new JsonObjectRequestWithToken(
                            Request.Method.POST, base_url + endpoint, surveyObject,
                            api_token, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int pk = response.getInt("pk");
                        Log.v("TEST", "pk for new complete survey is: " + pk);
                        finishWithResult();
                    } catch (org.json.JSONException e) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Server failed to return a PK for complete survey",
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    String err_msg = new String(error.networkResponse.data);
                    Log.e("ERROR", err_msg);
                    Toast toast = Toast.makeText(getApplicationContext(), err_msg, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

            this.netQueue.add(requestNewCompleteSurvey);

        } catch (org.json.JSONException e){
            Log.e("TEST","Something went very wrong creating survey object");
        }
    }

    private void finishWithResult() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
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
                        newActivVal.setText("");
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

        JsonArrayRequestWithToken activityListRequestArray = new JsonArrayRequestWithToken(base_url+activityEndpoint, api_token, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray resp) {
                Log.v("Test", resp.toString());
                try {
                    for (int i = 0; i < resp.length(); i++) {
                        JSONObject o = (JSONObject) resp.get(i);
                        ActivityMap.put(o.getString("pk"),o.getString("value"));
                        RevActivityMap.put(o.getString("value"), o.getString("pk"));
                        tempList.add(o.getString("value"));
                    }

                    Collections.sort(tempList, String.CASE_INSENSITIVE_ORDER);
                    Log.v("MAPS", tempList.toString());
                    for (String s: tempList){
                        adapter.add(s);
                    }
                } catch (JSONException e) {
                    adapter.add("Server responded with incorrect JSON");
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                adapter.add("Unable to connect to server. Please check your internet connection");
            }
        });

        this.netQueue.add(activityListRequestArray);
    }

    public void setDate(int year, int month, int day){
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        date = calendar.getTime();
        selDate.setText(dateFormats[0].format(date));
    }
    public void setTime( int hourOfDay, int minute){
        Log.v("TIME", ""+hourOfDay);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        date = calendar.getTime();
        Log.v("TIME", date.toString());
        selTime.setText(dateFormats[1].format(date));
        Log.v("TIME", dateFormats[1].format(date));
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    android.text.format.DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Log.v("TIME", ""+hourOfDay);
            ((AddNewActivity) getActivity()).setTime(hourOfDay, minute);
        }
    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            ((AddNewActivity)getActivity()).setDate(year, month, day);
        }
    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

}
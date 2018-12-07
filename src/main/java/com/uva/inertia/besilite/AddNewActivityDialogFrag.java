package com.uva.inertia.besilite;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.snowplowanalytics.snowplow.tracker.Emitter;
import com.snowplowanalytics.snowplow.tracker.Subject;
import com.snowplowanalytics.snowplow.tracker.Tracker;
import com.snowplowanalytics.snowplow.tracker.emitter.BufferOption;
import com.snowplowanalytics.snowplow.tracker.emitter.HttpMethod;
import com.snowplowanalytics.snowplow.tracker.events.ScreenView;

public class AddNewActivityDialogFrag extends DialogFragment{

    AddActivityBundle addbundle;
    String newAct;
    SharedPreferences sharedPref;

    public AddNewActivityDialogFrag() {
        // Empty constructor required for DialogFragment
    }

    public static AddNewActivityDialogFrag newInstance(String title) {
        AddNewActivityDialogFrag frag = new AddNewActivityDialogFrag();
        Bundle args = new Bundle();
        args.putString("Add New Activity Type", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());


        ////////////////////////Android Analytics Tracking Code////////////////////////////////////
        // Create an Emitter
        Emitter e1 = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getActivity())
                .method(HttpMethod.POST) // Optional - Defines how we send the request
                .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                // Optional - Defines what protocol used to send events
                .build();

        Subject s1 = new Subject.SubjectBuilder().build();
        s1.setUserId(sharedPref.getString("pref_key_api_token", ""));
        // Make and return the Tracker object
        Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "addNewActivityBundle", "com.uva.inertia.besilite", getActivity())
                .base64(false)
                .subject(s1)
                .build()
        );


        t1.track(ScreenView.builder()
                .name("Add New Activity Type")
                .id("addNewActivity")
                .build());
        ///////////////////////////////////////////////////////////////////////////////////////////

        String title = getArguments().getString("title");

        addbundle = (AddActivityBundle)getActivity();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        final EditText input = new EditText(getActivity());
        alertDialogBuilder.setView(input);
        alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                passItAlong(input.getText().toString());
                dialog.dismiss();

                ////////////////////////Android Analytics Tracking Code////////////////////////////////////
                // Create an Emitter
                Emitter e1 = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getActivity())
                        .method(HttpMethod.POST) // Optional - Defines how we send the request
                        .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                        // Optional - Defines what protocol used to send events
                        .build();

                Subject s1 = new Subject.SubjectBuilder().build();
                s1.setUserId(sharedPref.getString("pref_key_api_token", ""));
                // Make and return the Tracker object
                Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "addNewActivityDialogFrag", "com.uva.inertia.besilite", getActivity())
                        .base64(false)
                        .subject(s1)
                        .build()
                );


                t1.track(ScreenView.builder()
                        .name("Add New Activity Textfield Add Button")
                        .id("addNewActivityTextfieldAddButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                ////////////////////////Android Analytics Tracking Code////////////////////////////////////
                // Create an Emitter
                Emitter e1 = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getActivity())
                        .method(HttpMethod.POST) // Optional - Defines how we send the request
                        .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                        // Optional - Defines what protocol used to send events
                        .build();

                Subject s1 = new Subject.SubjectBuilder().build();
                s1.setUserId(sharedPref.getString("pref_key_api_token", ""));
                // Make and return the Tracker object
                Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "addNewActivityDialogFrag", "com.uva.inertia.besilite", getActivity())
                        .base64(false)
                        .subject(s1)
                        .build()
                );


                t1.track(ScreenView.builder()
                        .name("Add New Activity Textfield Cancel Button")
                        .id("addNewActivityTextfieldCancelButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////
            }
        });

        return alertDialogBuilder.create();
    }

    public void passItAlong(String text){
        addbundle.onFinishNewActDialog(text);
    }
}


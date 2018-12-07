package com.uva.inertia.besilite;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.util.Log;
import android.widget.EditText;

import com.snowplowanalytics.snowplow.tracker.Emitter;
import com.snowplowanalytics.snowplow.tracker.Subject;
import com.snowplowanalytics.snowplow.tracker.Tracker;
import com.snowplowanalytics.snowplow.tracker.emitter.BufferOption;
import com.snowplowanalytics.snowplow.tracker.emitter.HttpMethod;
import com.snowplowanalytics.snowplow.tracker.events.ScreenView;

import java.util.HashMap;


public class ObservationSubsurveyFragment extends android.support.v4.app.Fragment {

    AgitationReports ar;
    HashMap<String, Boolean> pwdObs;

    CheckBox Restlessness;
    CheckBox Ambulating;
    CheckBox Touching;
    CheckBox Clothing;
    CheckBox Physical1;
    CheckBox Physical2;
    CheckBox OralFixation;
    CheckBox Repetition;
    CheckBox Vocal1;
    CheckBox Vocal2;
    CheckBox Lost;
    CheckBox Withdrawn;
    CheckBox Annoying;
    CheckBox Shadowing;
    CheckBox Communication;
    CheckBox Other;

    String otherText;

    ConfirmFragment.OnConfirmClickedListener mListener;

    SharedPreferences sharedPref;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ObservationSubsurveyFragment newInstance() {
        Log.v("jjp5nw", "ObservationSubsurveyFragment newInstance() called");
        ObservationSubsurveyFragment fragment = new ObservationSubsurveyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ObservationSubsurveyFragment() {
        Log.v("jjp5nw", "ObservationSubsurvey constructor called");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("jjp5nw", "ObservationSubsurveyFragment onCreateView() called");
        View rootView = inflater.inflate(R.layout.fragment_observation_subsurvey, container, false);
        ar = (AgitationReports) getActivity();

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
        Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "agiRepObservations", "com.uva.inertia.besilite", getContext())
                .base64(false)
                .subject(s1)
                .build()
        );

        t1.track(ScreenView.builder()
                .name("Agitation Report Observation Survey")
                .id("agiReportObsSurvey")
                .build());


        ///////////////////////////////////////////////////////////////////////////////////////////

        Button backBtn = (Button) rootView.findViewById(R.id.backFromObs);

        backBtn.setOnClickListener(new View.OnClickListener()   {

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
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "agitationReportObservations", "com.uva.inertia.besilite", getContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("Agitation Report / Observation -> Back")
                        .id("agitationReportObsBackButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////

                //
                ((AgitationReports) getActivity()).selectPage(1);
            }
        });

                Button nextBtn = (Button) rootView.findViewById(R.id.obsNextToNotifs);

        nextBtn.setOnClickListener(new View.OnClickListener() {
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
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "agitationReportObservations", "com.uva.inertia.besilite", getContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("Agitation Report / Observation -> Next")
                        .id("agitationReportObsNextButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////

//                mListener = (ConfirmFragment.OnConfirmClickedListener) getActivity();
//                mListener.OnConfirmClicked();
                ((AgitationReports) getActivity()).selectPage(3);

                Log.v("101117", "jjp5nw nextBtn pressed");
            }
        });

//        Button confirmBtn = (Button) rootView.findViewById(R.id.submitOnObs);
//
//        confirmBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ////////////////////////Android Analytics Tracking Code////////////////////////////////////
//                // Create an Emitter
//                Emitter emitter = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
//                        .method(HttpMethod.POST) // Optional - Defines how we send the request
//                        .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
//                        // Optional - Defines what protocol used to send events
//                        .build();
//
//                Subject subject = new Subject.SubjectBuilder().build();
//                subject.setUserId(sharedPref.getString("pref_key_api_token", ""));
//                // Make and return the Tracker object
//                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "agitationReportObservations", "com.uva.inertia.besilite", getContext())
//                        .base64(false)
//                        .subject(subject)
//                        .build()
//                );
//
//                tracker.track(ScreenView.builder()
//                        .name("Agitation Report / Observation -> Submit")
//                        .id("agitationReportObsSubmitButton")
//                        .build());
//                ///////////////////////////////////////////////////////////////////////////////////////////
//
////                mListener = (ConfirmFragment.OnConfirmClickedListener) getActivity();
////                mListener.OnConfirmClicked();
//                ((AgitationReports) getActivity()).selectPage(2);
//            }
//        });

        pwdObs = ar.pwdObs;

        Restlessness = (CheckBox) rootView.findViewById(R.id.checkRestlessness);
        Restlessness.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Frustration"));
        Ambulating = (CheckBox) rootView.findViewById(R.id.checkAmbulating);
        Ambulating.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Ambulation"));
        Touching = (CheckBox) rootView.findViewById(R.id.checkTouching);
        Touching.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Touching"));
        Clothing = (CheckBox) rootView.findViewById(R.id.checkClothing);
        Clothing.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Clothing"));
        Physical1 = (CheckBox) rootView.findViewById(R.id.checkPhysical1);
        Physical1.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Physical1"));
        Physical2 = (CheckBox) rootView.findViewById(R.id.checkPhysical2);
        Physical2.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Physical2"));
        OralFixation = (CheckBox) rootView.findViewById(R.id.checkOralFixation);
        OralFixation.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "OralFixation"));
        Repetition = (CheckBox) rootView.findViewById(R.id.checkRepetition);

        Repetition.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Repetition"));
        Repetition = (CheckBox) rootView.findViewById(R.id.checkRepetition);
        Repetition.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Repetition"));
        Vocal1 = (CheckBox) rootView.findViewById(R.id.checkVocal1);
        Vocal1.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Vocal1"));
        Vocal2 = (CheckBox) rootView.findViewById(R.id.checkVocal2);
        Vocal2.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Vocal2"));
        Lost = (CheckBox) rootView.findViewById(R.id.checkLost);
        Lost.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Lost"));
        Withdrawn = (CheckBox) rootView.findViewById(R.id.checkWithdrawn);
        Withdrawn.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Withdrawn"));
//        Annoying = (CheckBox) rootView.findViewById(R.id.checkAnnoying);
//        Annoying.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Annoying"));
        Shadowing = (CheckBox) rootView.findViewById(R.id.checkShadowing);
        Shadowing.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Shadowing"));
        Communication = (CheckBox) rootView.findViewById(R.id.checkCommunication);
        Communication.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Communication"));

        Other = (CheckBox) rootView.findViewById(R.id.checkOther);
        Other.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.v("jjp5nw", "checkOther clicked");
                 Log.v("jjp5nw", "checkOther = " + ((CheckBox)v).isChecked());
                 // only bring up the dialog when it is checked
                 if(((CheckBox)v).isChecked())  {
//                     AddNewActivityDialogFrag newAct = AddNewActivityDialogFrag.newInstance("newAct2");
//                     newAct.show(android.support.v4.app.FragmentgetFragmentManager(), "fragment_add_activity");
                    Log.v("jjp5nw", "checkbox is checked case launched");

                     AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                     builder.setTitle("Other observation: ");

                    // Set up the input
                     final EditText input = new EditText(getContext());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                     input.setInputType(InputType.TYPE_CLASS_TEXT);// | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                     input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
                     builder.setView(input);
                     //.setCanceledOnTouchOutside(false);
                     builder.setCancelable(false);
                    Log.v("jjp5nw", "EditText made, builder view set to input");
                    // Set up the buttons
                     builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             Log.v("jjp5nw", "onClick of builder setPositiveButton");
                             otherText = input.getText().toString();
                             Log.v("jjp5nw", "otherText = " + otherText);
                             if(otherText.length() > 0) {
                                 Other.setText("Other (" + otherText + ")");
                             }  else    {
                                 Other.setText("Other");
                                 Other.setChecked(false);
                             }
//                             Other.setText((otherText.length() > 0) ? "Other (" + otherText + ")" : "Other");
                         }
                     });
                     builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             Log.v("jjp5nw", "onClick of builder setNegativeButton");
                             Other.setChecked(false);
                             otherText = "";
                             Log.v("jjp5nw", "otherText = " + otherText);
                             Other.setText("Other");
                             dialog.cancel();
                         }
                     });

                     builder.show();



                 }  else    {
                     Other.setText("Other");
                 }
             }
        });

        return rootView;
    }
}
/*
public static View.OnClickListener updateMapOnClick(final HashMap<String, Boolean> hm, final String key){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox c = (CheckBox)v;
                hm.put(key,c.isChecked());
                Log.v("DAILYSURVEY", "Click handler called from: " + c.toString());
            }
        };
    }
 */
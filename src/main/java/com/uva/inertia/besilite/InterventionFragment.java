package com.uva.inertia.besilite;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.snowplowanalytics.snowplow.tracker.Emitter;
import com.snowplowanalytics.snowplow.tracker.Subject;
import com.snowplowanalytics.snowplow.tracker.Tracker;
import com.snowplowanalytics.snowplow.tracker.emitter.BufferOption;
import com.snowplowanalytics.snowplow.tracker.emitter.HttpMethod;
import com.snowplowanalytics.snowplow.tracker.events.ScreenView;


public class InterventionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

//    private OnFragmentInteractionListener mListener;


    //th4sf
    Button backBtn, submitBtn;
    SharedPreferences sharedPref;
    ConfirmFragment.OnConfirmClickedListener mListener;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static InterventionFragment newInstance() {
        Log.v("nh4ar","InterventionFragment newInstance() called");
        InterventionFragment fragment = new InterventionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public InterventionFragment() {
        // Required empty public constructor
        Log.v("nh4ar", "InterventionFragment constructor called");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity()); //th4sf

        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v("jjp5nw", "NotificationsFragment onCreateView() called");
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_intervention, container, false);

        ///////////////////////////////////////////////////


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
        Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "agiRepIntervention", "com.uva.inertia.besilite", getContext())
                .base64(false)
                .subject(s1)
                .build()
        );

        t1.track(ScreenView.builder()
                .name("Agitation Report Intervention")
                .id("agiReportIntervention")
                .build());


        ///////////////////////////////////////////////////////////////////////////////////////////


        backBtn = (Button) rootView.findViewById(R.id.backFromInter);

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
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "agitationReportNotifications", "com.uva.inertia.besilite", getContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("Agitation Report / Notifications -> Back")
                        .id("agitationReportNotifsBackButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////

                //
                ((AgitationReports) getActivity()).selectPage(3);
            }
        });


        submitBtn = (Button) rootView.findViewById(R.id.submitOnInter);

        submitBtn.setOnClickListener(new View.OnClickListener() {
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
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "agitationReportIntervention", "com.uva.inertia.besilite", getContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("Agitation Report / Intervention -> Submit")
                        .id("agitationReportInterventionSubmitButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////

                mListener = (ConfirmFragment.OnConfirmClickedListener) getActivity();
                mListener.OnConfirmClicked();
                ((AgitationReports) getActivity()).selectPage(4);

                Log.v("101117", "jjp5nw submitBtn pressed");
            }
        });

        submitBtn.setEnabled(true);




//*/




        ////////////////////////////////////////////////////

        return rootView;
    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
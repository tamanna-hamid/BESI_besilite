package com.uva.inertia.besilite;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class RadioPWDEmotionSubsurveyFragment extends android.support.v4.app.Fragment {
    AgitationReports ar;
    RadioButton SadVoice;
    RadioButton Tearful;
    RadioButton LackReact;
    RadioButton VeryWorried;
    RadioButton Frightened;
    RadioButton TalkLess;
    RadioButton AppetiteLoss;
    RadioButton LessInterestInHobbies;
    RadioButton SadExpression;
    RadioButton LackOfInterest;
    RadioButton TroubleConcentrating;
    RadioButton BotheredByUsualActivities;
    RadioButton SlowMove;
    RadioButton SlowSpeech;
    RadioButton SlowReaction;

    RadioGroup col1;
    RadioGroup col2;
    RadioGroup col3;


    HashMap<String, Boolean> pwdEmo = new HashMap<>();

    ArrayList<RadioGroup> columns = new ArrayList<>();

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RadioPWDEmotionSubsurveyFragment newInstance() {
        RadioPWDEmotionSubsurveyFragment fragment = new RadioPWDEmotionSubsurveyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public RadioPWDEmotionSubsurveyFragment() {
    }


    public RadioButton.OnCheckedChangeListener resetOtherCols(final int col, final HashMap<String, Boolean> hm, final String key){
        return new RadioButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hm.put(key,isChecked);
                if (isChecked) {
                    for (int i = 0; i < columns.size(); i++) {
                        if (i + 1 != col) {
                            columns.get(i).clearCheck();
                        }
                    }
                }
            }
        };
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_radio_emotion_subsurvey, container, false);

        ar = (AgitationReports) getActivity();

        col1 = (RadioGroup)rootView.findViewById(R.id.radioCol1);
        columns.add(col1);

        col2 = (RadioGroup)rootView.findViewById(R.id.radioCol2);
        columns.add(col2);

        col3 = (RadioGroup)rootView.findViewById(R.id.radioCol3);
        columns.add(col3);


        pwdEmo = ar.pwdEmo;



//        The code below is a very hacky way of fixing the lack of grids for radio buttons problem
//        Because of this, if the buttons change rows, the on changed handlers will need to be updated as well
//        A better solution may be required in the future


        //COL 1

        SlowSpeech =(RadioButton)rootView.findViewById(R.id.radioSlowSpeech);

        SlowReaction =  (RadioButton)rootView.findViewById(R.id.radioSlowReact);

        LackOfInterest = (RadioButton)rootView.findViewById(R.id.radioLackOfInterest);

        Frightened = (RadioButton)rootView.findViewById(R.id.radioFrightened);

        TalkLess = (RadioButton)rootView.findViewById(R.id.radioLessTalk);

        SlowSpeech.setOnCheckedChangeListener(resetOtherCols(1, pwdEmo, "SlowSpeech"));
        SlowReaction.setOnCheckedChangeListener(resetOtherCols(1,pwdEmo, "SlowReaction"));
        LackOfInterest.setOnCheckedChangeListener(resetOtherCols(1,pwdEmo, "LackOfInterest"));
        Frightened.setOnCheckedChangeListener(resetOtherCols(1,pwdEmo, "Frightened"));
        TalkLess.setOnCheckedChangeListener(resetOtherCols(1,pwdEmo, "TalkLess"));


        //Col 2


        SadVoice = (RadioButton)rootView.findViewById(R.id.radioSadVoice);

        TroubleConcentrating = (RadioButton)rootView.findViewById(R.id.radioTroubleConcen);

        Tearful = (RadioButton)rootView.findViewById(R.id.radioTearfulness);

        AppetiteLoss = (RadioButton)rootView.findViewById(R.id.radioAppetiteLoss);

        BotheredByUsualActivities = (RadioButton)rootView.findViewById(R.id.radioBotheredByUsual);

        SadVoice.setOnCheckedChangeListener(resetOtherCols(2,pwdEmo, "SadVoice"));
        TroubleConcentrating.setOnCheckedChangeListener(resetOtherCols(2, pwdEmo, "TroubleConcentrating"));
        Tearful.setOnCheckedChangeListener(resetOtherCols(2,pwdEmo, "Tearfulness"));
        AppetiteLoss.setOnCheckedChangeListener(resetOtherCols(2, pwdEmo, "AppetiteLoss"));
        BotheredByUsualActivities.setOnCheckedChangeListener(resetOtherCols(2,pwdEmo, "BotheredByUsualActivities"));


        //Col 3

        LackReact = (RadioButton)rootView.findViewById(R.id.radioLackOfReact);

        LessInterestInHobbies = (RadioButton)rootView.findViewById(R.id.radioLessIntrest);

        SlowMove = (RadioButton)rootView.findViewById(R.id.radioSlowMove);

        VeryWorried = (RadioButton)rootView.findViewById(R.id.radioVeryWorried);

        SadExpression = (RadioButton)rootView.findViewById(R.id.radioSadExpression);

        LackReact.setOnCheckedChangeListener(resetOtherCols(3,pwdEmo, "LackReactionToPleasantEvents"));
        LessInterestInHobbies.setOnCheckedChangeListener(resetOtherCols(3,pwdEmo, "LessInterestInHobbies"));
        SlowMove.setOnCheckedChangeListener(resetOtherCols(3,pwdEmo, "SlowMovement"));
        VeryWorried.setOnCheckedChangeListener(resetOtherCols(3,pwdEmo, "VeryWorried"));
        SadExpression.setOnCheckedChangeListener(resetOtherCols(3,pwdEmo, "SadExpression"));


        return rootView;

    }

}

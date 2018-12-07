package com.uva.inertia.besilite;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Ben on 2/21/2016.
 */
public class ConfirmFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    OnConfirmClickedListener mListener;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ConfirmFragment newInstance(int sectionNumber) {
        ConfirmFragment fragment = new ConfirmFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public ConfirmFragment() {

    }

    public interface OnConfirmClickedListener {
        public void OnConfirmClicked();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_daily_survey_confirm, container, false);
        Button confirmBtn = (Button) rootView.findViewById(R.id.submitDaily);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener = (OnConfirmClickedListener) getActivity();
                mListener.OnConfirmClicked();
            }
        });
        return rootView;


    }


}

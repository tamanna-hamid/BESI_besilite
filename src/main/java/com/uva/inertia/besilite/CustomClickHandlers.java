package com.uva.inertia.besilite;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import java.util.HashMap;

/**
 * Created by Ben on 2/24/2016.
 */
public class CustomClickHandlers {

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

    public static View.OnClickListener updateMapOnRadio(final HashMap<String, Boolean> hm, final String key){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton c = (RadioButton)v;
                hm.put(key, c.isChecked());
                Log.v("AGIREPORT", "Click handler called from: " + c.toString());
            }
        };
    }
}

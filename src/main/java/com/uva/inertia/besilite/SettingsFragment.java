package com.uva.inertia.besilite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ben on 2/10/2016.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        Preference b = findPreference("pref_export_data");
        b.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                DialogInterface.OnClickListener action = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            Toast.makeText(getActivity(),"Exporting files!", Toast.LENGTH_LONG).show();
                            File folder = new File(getActivity().getFilesDir(), "offline");
                            if (!folder.mkdirs()) {
                                Log.e("FILES", "Did not create folder");
                            }
                            FileInputStream fin;
                            FileOutputStream fou;

                            Iterator<File> filesIt = Arrays.asList(folder.listFiles()).iterator();
                            for (; filesIt.hasNext(); ) {
                                if (isExternalStorageWritable()) {
                                    try {
                                        //As per http://stackoverflow.com/questions/9292954/how-to-make-a-copy-of-a-file-in-android
                                        File f = filesIt.next();
                                        fin = new FileInputStream(f);
                                        File path = new File(Environment.getExternalStorageDirectory(),"BESI");
                                        if (!path.mkdirs()){
                                            Log.e("FILES", "Failed to create BESI export folder");
                                        };
                                        File out = new File(path, f.getName() + ".json");
                                        fou = new FileOutputStream(out);
                                        FileChannel inChan = fin.getChannel();
                                        FileChannel outChan = fou.getChannel();
                                        inChan.transferTo(0, inChan.size(), outChan);
                                        inChan.close();
                                        outChan.close();
                                        f.delete();
                                        Log.d("FILES", "Exported: " + out.getName());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                };

                AlertDialog.Builder diaBuild = new AlertDialog.Builder(getActivity())
                        .setMessage("This action will delete all offline items from internal storage. Are you sure you want to continue?")
                        .setNegativeButton("No", action)
                        .setPositiveButton("Yes", action);

                diaBuild.show();






                return true;
            }
        });
    }

    /* Checks if external storage is available for read and write */
    /* Source from https://developer.android.com/training/basics/data-storage/files.html */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
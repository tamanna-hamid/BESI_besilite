package com.uva.inertia.besilite;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Ben on 5/19/2016.
 */
public class FileHelpers {

    public static void writeStringToInternalStorage(String data, String folderName, String filename, Context c){
        File folder = new File(c.getFilesDir(), folderName);
        if (!folder.mkdirs()) {
            Log.e("FILES", "Did not create folder");
        }

        File out = new File(folder, filename);
        try {
            FileWriter fw = new FileWriter(out);
            fw.write(data);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readStringFromInternalStorage(String folderName, String filename, Context c){
        String out = "";
        File folder = new File(c.getFilesDir(), folderName);
        try{
            File target = new File(folder, filename);
            FileReader fin = new FileReader(target);
            BufferedReader bfin = new BufferedReader(fin);

            String line;
            //while the lines of the buffered reader are not null
            //append the read line to the output
            while( (line = bfin.readLine()) != null){
                out += line;
            }
            bfin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    public static void deleteFileFromInternalStorage(String folderName, String filename, Context c){
        File folder = new File(c.getFilesDir(), folderName);
        if (!folder.mkdirs()) {
            Log.e("FILES", "Did not create folder");
        }

        File surveyFile = new File(folder, filename);
        if(!surveyFile.delete()){
            Log.e("FILES","File failed to delete");
        }
    }
}
